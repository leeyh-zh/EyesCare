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

import com.lyh.eyescare.ColorView;
import com.lyh.eyescare.R;
import com.lyh.eyescare.activity.SettingsActivity;
import com.lyh.eyescare.bean.AppInfo;
import com.lyh.eyescare.constant.Constants;
import com.lyh.eyescare.manager.AppInfoManager;
import com.lyh.eyescare.manager.ColorManager;
import com.lyh.eyescare.utils.SpUtil;

import java.lang.ref.WeakReference;
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
    public int mNotifyMode = TYPE_OPEN;
    private int mNotificationId = 1000;
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
    private static int mColor;
    private Message mMessage;
    private static SpUtil mSpUtil;
    private int alpha;
    private int red;
    private int green;
    private int bule;
    private String currentPackageName;

    private final MyHandler mHandler = new MyHandler(this);
    private EyesCareReceiver receiver;
    public final String GOGGLES_ACTION = "com.lyh.eyescare.switch.goggles";
    public final String LIGHT_ACTION = "com.lyh.eyescare.switch.light";
    public final String CANCEL_ACTION = "com.lyh.eyescare.switch.cancel";
    private boolean isGoggles = false;
    private boolean isCustom = false;
    private boolean isLight = false;
    private Thread mThread;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new EyesCareReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GOGGLES_ACTION);
        filter.addAction(LIGHT_ACTION);
        filter.addAction(CANCEL_ACTION);
        registerReceiver(receiver, filter);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification = new Notification();
        mSpUtil = SpUtil.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        list = new ArrayList<>();
        mAppInfoManager = new AppInfoManager(getApplicationContext());
        if (intent != null) {
            mStatus = intent.getIntExtra(Constants.STATUS, TYPE_OPEN);
            if (mStatus == TYPE_OPEN) {
                isGoggles = true;
                openGoggles();
            } else if (mStatus == TYPE_CUSTOM_OPEN) {
                isCustom = true;
            } else if (mStatus == TYPE_CUSTOM_CLOSE) {
                isCustom = false;
            } else if (mStatus == TYPE_CLOSE) {
                isGoggles = false;
                closeGoggles();
            }
        }
        mThread = new Thread(new LauncherTopApp());
        mThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public void openGoggles() {
        ColorManager.addColorView(this);
        ColorManager.changeColor(mSpUtil.getInt(Constants.COLOR_ARGB, Color.argb(26, 54, 36, 0)));
        showNotification();
    }

    public void closeGoggles() {
        ColorManager.removeColorView(this);
        showNotification();
    }

    class LauncherTopApp implements Runnable {
        @Override
        public void run() {
            while (isCustom) {
                try {
                    Thread.sleep(500);
                    currentPackageName = getLauncherTopApp(EyesCareService.this,
                            mActivityManager);

                    if (mSpUtil.getBoolean(Constants.EYESHIELD, false)) {
                        if (!TextUtils.isEmpty(currentPackageName)) {
                            list = mAppInfoManager.getAllAppInfos();
                        }
                        for (AppInfo appInfo : list) {
                            if (currentPackageName.equals(appInfo.getPackageName())) {
                                if (appInfo.isCustomPattern()) {
                                    if (appInfo.isCustomLight() && appInfo.isCustomColor()) {
                                        mColor = Color.argb(appInfo.getAlpha(), appInfo.getRed(), appInfo.getGreen(), appInfo.getBlue());
                                    } else {
                                        if (appInfo.isCustomLight()) {
                                            alpha = appInfo.getAlpha();
                                        } else {
                                            alpha = mSpUtil.getInt(Constants.ALPHA, 26);
                                        }
                                        if (appInfo.isCustomColor()) {
                                            red = appInfo.getRed();
                                            green = appInfo.getGreen();
                                            bule = appInfo.getBlue();
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
                                    !currentPackageName.equals(appInfo.getPackageName())) {
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

    private static class MyHandler extends Handler {

        private final WeakReference<EyesCareService> mService;

        public MyHandler(EyesCareService service) {
            mService = new WeakReference<EyesCareService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            EyesCareService service = mService.get();
            if (service != null) {
                if (msg.what == 0) {
                    mColor = (int) msg.obj;
                    ColorManager.changeColor(mColor);
                }
            }
            removeMessages(msg.what);
            super.handleMessage(msg);
        }
    }

    ;

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

    private void showNotification() {

        contentView = new RemoteViews(getPackageName(), R.layout.notification);
        notificationIntent = new Intent(this, SettingsActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        contentView.setImageViewResource(R.id.switch_light, isLight ?
                R.mipmap.flash_light_on_72 : R.mipmap.flash_light_off_72);
        contentView.setImageViewResource(R.id.switch_goggles, isGoggles ?
                R.mipmap.switch_goggles_on_72 : R.mipmap.switch_goggles_off_72);
        contentView.setTextViewText(R.id.notification_title, getText(isGoggles ?
                R.string.notification_title_on : R.string.notification_title_off));

        contentView.setOnClickPendingIntent(R.id.switch_light,
                PendingIntent.getBroadcast(EyesCareService.this, 11, new Intent().setAction(LIGHT_ACTION), PendingIntent.FLAG_UPDATE_CURRENT));
        contentView.setOnClickPendingIntent(R.id.switch_goggles,
                PendingIntent.getBroadcast(EyesCareService.this, 11, new Intent().setAction(GOGGLES_ACTION), PendingIntent.FLAG_UPDATE_CURRENT));
        contentView.setOnClickPendingIntent(R.id.clear_notification,
                PendingIntent.getBroadcast(EyesCareService.this, 11, new Intent().setAction(CANCEL_ACTION), PendingIntent.FLAG_UPDATE_CURRENT));

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

    class EyesCareReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case CANCEL_ACTION:
                    mNotificationManager.cancelAll();
                    break;
                case LIGHT_ACTION:
                    if (!isLight) SettingsActivity.openLight();
                    else SettingsActivity.closeLight();
                    isLight = !isLight;
                    showNotification();
                    break;
                case GOGGLES_ACTION:
                    isGoggles = !isGoggles;
                    if (!isGoggles) closeGoggles();
                    else openGoggles();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
