package com.lyh.eyescare.service;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.lyh.eyescare.ColorView;
import com.lyh.eyescare.R;
import com.lyh.eyescare.activity.SettingsActivity;
import com.lyh.eyescare.bean.AppInfo;
import com.lyh.eyescare.constant.Constants;
import com.lyh.eyescare.manager.AppInfoManager;
import com.lyh.eyescare.manager.ColorManager;
import com.lyh.eyescare.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyh on 2017/3/27.
 */

public class EyesCareService extends AccessibilityService {

    public static final int TYPE_OPEN = 0;
    public static final int TYPE_CUSTOM_OPEN = 1;
    public static final int TYPE_CUSTOM_CLOSE = 2;
    public static final int TYPE_CLOSE = 3;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private RemoteViews contentView;
    private ColorView mColorView;
    private Intent notificationIntent;
    private PendingIntent contentIntent;

    private ActivityManager mActivityManager;
    private AppInfoManager mAppInfoManager;
    private List<AppInfo> list;
    private int mStatus;
    private int mColor;
    private Message mMessage;
    private boolean customOpen = false;
    private SpUtil mSpUtil;
    private int alpha;
    private int red;
    private int green;
    private int bule;
    private String currentPackageName;

    public static final String SWITCH_GOGGLES_ACTION = "com.lyh.eyescare.switch.goggles";

    private MyBroadCast receiver;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new MyBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("a");
        filter.addAction("b");
        filter.addAction("c");
        filter.addAction("d");
        registerReceiver(receiver, filter);

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        list = new ArrayList<>();
        mSpUtil = SpUtil.getInstance();
        mAppInfoManager = new AppInfoManager(getApplicationContext());
        if (intent != null) {
            mStatus = intent.getIntExtra(Constants.STATUS, TYPE_OPEN);
            mColor = intent.getIntExtra(Constants.COLOR, 0xffffffff);
            if (mStatus == TYPE_OPEN) {
                ColorManager.addColorView(this);
                ColorManager.changeColor(mColor);
                showNotification();
            } else if (mStatus == TYPE_CUSTOM_OPEN) {
                customOpen = mSpUtil.getBoolean(Constants.CUSTOM_EYESHIELD, true);
            } else if (mStatus == TYPE_CUSTOM_CLOSE) {
                customOpen = mSpUtil.getBoolean(Constants.CUSTOM_EYESHIELD, false);
            } else if (mStatus == TYPE_CLOSE) {
                ColorManager.removeColorView(this);

            }
        }

        new Thread(new LauncherTopApp()).start();

        return super.onStartCommand(intent, flags, startId);
    }


    class LauncherTopApp implements Runnable {
        @Override
        public void run() {
            while (customOpen) {
                try {
                    Thread.sleep(500);
                    currentPackageName = getLauncherTopApp(EyesCareService.this,
                            mActivityManager);

                    Log.d("1111", "currentPackageName = " + currentPackageName);

                    if (mSpUtil.getBoolean(Constants.EYESHIELD, false)) {
                        if (!TextUtils.isEmpty(currentPackageName)) {
                            list = mAppInfoManager.getAllAppInfos();
                        }
                        for (int i = 0; i < list.size(); i++) {
                            if (currentPackageName.equals(list.get(i).getPackageName())) {
                                if (list.get(i).isCustomPattern()) {
                                    if (list.get(i).isCustomLight() && list.get(i).isCustomColor()) {
                                        mColor = Color.argb(list.get(i).getAlpha(), list.get(i).getRed(), list.get(i).getGreen(), list.get(i).getBule());
                                    } else {
                                        if (list.get(i).isCustomLight()) {
                                            alpha = list.get(i).getAlpha();
                                        } else {
                                            alpha = mSpUtil.getInt(Constants.ALPHA, 26);
                                        }
                                        if (list.get(i).isCustomColor()) {
                                            red = list.get(i).getRed();
                                            green = list.get(i).getGreen();
                                            bule = list.get(i).getBule();
                                        } else {
                                            red = mSpUtil.getInt(Constants.RED, 54);
                                            green = mSpUtil.getInt(Constants.GREEN, 36);
                                            bule = mSpUtil.getInt(Constants.BLUE, 0);
                                        }
                                        mColor = Color.argb(alpha, red, green, bule);
                                    }
                                }
                                break;
                            } else if (!TextUtils.isEmpty(currentPackageName) &&
                                    !currentPackageName.equals(list.get(i).getPackageName())) {
                                alpha = mSpUtil.getInt(Constants.ALPHA, 26);
                                red = mSpUtil.getInt(Constants.RED, 54);
                                green = mSpUtil.getInt(Constants.GREEN, 36);
                                bule = mSpUtil.getInt(Constants.BLUE, 0);
                                mColor = Color.argb(alpha, red, green, bule);
                            }
                        }
                        mMessage = mHandler.obtainMessage();
                        mMessage.what = 0;
                        mMessage.obj = mColor;
                        mHandler.sendMessage(mMessage);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("1111", "run: 未知错误");
                }
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mColor = (int) msg.obj;
                    ColorManager.changeColor(mColor);
            }
        }
    };

    /**
     * 获取栈顶应用包名
     */
    public String getLauncherTopApp(Context context, ActivityManager activityManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks.get(0).topActivity.getPackageName();
            }
        } else {
            //5.0以后需要用这方法
            UsageStatsManager sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 1000;
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                }
            }
            if (!android.text.TextUtils.isEmpty(result)) {
                return result;
            }
        }
        return "";
    }

    final int GOGGLES_FLAG = 0x1;
    final int NEXT_FLAG = 0x2;
    final int STOP_FLAG = 0x3;

    private void showNotification() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification = new Notification();
        contentView = new RemoteViews(getPackageName(), R.layout.notification);
        notificationIntent = new Intent(this, SettingsActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        contentView.setOnClickPendingIntent(R.id.clear_notification,
                PendingIntent.getBroadcast(EyesCareService.this, 11, new Intent().setAction("a"), PendingIntent.FLAG_UPDATE_CURRENT));
        contentView.setOnClickPendingIntent(R.id.switch_color,
                PendingIntent.getBroadcast(EyesCareService.this, 11, new Intent().setAction("b"), PendingIntent.FLAG_UPDATE_CURRENT));
        contentView.setOnClickPendingIntent(R.id.switch_light,
                PendingIntent.getBroadcast(EyesCareService.this, 11, new Intent().setAction("c"), PendingIntent.FLAG_UPDATE_CURRENT));
        contentView.setOnClickPendingIntent(R.id.switch_goggles,
                PendingIntent.getBroadcast(EyesCareService.this, 11, new Intent().setAction("d"), PendingIntent.FLAG_UPDATE_CURRENT));
        contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.cool);
        builder.setContentIntent(contentIntent);
        builder.setContent(contentView);
        mNotification = builder.build();
        //mNotification.flags |= Notification.FLAG_INSISTENT;
        //mNotification.flags |= Notification.FLAG_NO_CLEAR;
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(0, mNotification);
    }

    private Notification getNotification() {
        RemoteViews remoteViews;
        final int PAUSE_FLAG = 0x1;
        final int NEXT_FLAG = 0x2;
        final int STOP_FLAG = 0x3;
        final boolean isGoggles = mSpUtil.getInstance().getBoolean(Constants.EYESHIELD);

        remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);
//        String text = TextUtils.isEmpty(albumName) ? artistName : artistName + " - " + albumName;
//        remoteViews.setTextViewText(R.id.title, getTrackName());
//        remoteViews.setTextViewText(R.id.text, text);

        //此处action不能是一样的 如果一样的 接受的flag参数只是第一个设置的值
        Intent pauseIntent = new Intent(SWITCH_GOGGLES_ACTION);
        pauseIntent.putExtra("FLAG", PAUSE_FLAG);
        PendingIntent pausePIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);
        remoteViews.setImageViewResource(R.id.switch_goggles, isGoggles ? R.mipmap.switch_goggles_on : R.mipmap.switch_goggles_off);
        remoteViews.setOnClickPendingIntent(R.id.switch_goggles, pausePIntent);

//        Intent nextIntent = new Intent(NEXT_ACTION);
//        nextIntent.putExtra("FLAG", NEXT_FLAG);
//        PendingIntent nextPIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
//        remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPIntent);

//        Intent preIntent = new Intent(STOP_ACTION);
//        preIntent.putExtra("FLAG", STOP_FLAG);
//        PendingIntent prePIntent = PendingIntent.getBroadcast(this, 0, preIntent, 0);
//        remoteViews.setOnClickPendingIntent(R.id.iv_stop, prePIntent);

//        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0,
//                new Intent(this.getApplicationContext(), PlayingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//        final Intent nowPlayingIntent = new Intent();
//        //nowPlayingIntent.setAction("com.wm.remusic.LAUNCH_NOW_PLAYING_ACTION");
//        nowPlayingIntent.setComponent(new ComponentName("com.wm.remusic", "com.wm.remusic.activity.PlayingActivity"));
//        nowPlayingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent clickIntent = PendingIntent.getBroadcast(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent click = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (mNotification == null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setContent(remoteViews)
                    .setSmallIcon(R.mipmap.cool)
                    .setContentIntent(pausePIntent);

            mNotification = builder.build();
        } else {
            mNotification.contentView = remoteViews;
        }

        return mNotification;
    }

    class MyBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("a")) {
                Toast.makeText(EyesCareService.this, "11111111111111",
                        Toast.LENGTH_SHORT).show();
                Log.d("11111", "service 111111111");
            }
            if (intent.getAction().equals("b")) {
                Toast.makeText(EyesCareService.this, "222222222222222",
                        Toast.LENGTH_SHORT).show();
                Log.d("11111", "service 222222222222222");
            }
            if (intent.getAction().equals("c")) {
                Toast.makeText(EyesCareService.this, "333333333333",
                        Toast.LENGTH_SHORT).show();
                Log.d("11111", "service 333333333333");
            }
            if (intent.getAction().equals("d")) {
                Toast.makeText(EyesCareService.this, "4444444444444",
                        Toast.LENGTH_SHORT).show();
                Log.d("11111", "service 4444444444444");
            }

        }

    }
}
