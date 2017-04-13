package com.lyh.eyescare.service;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
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

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
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
                    String packageName = getLauncherTopApp(EyesCareService.this, mActivityManager);
                    Log.d("1111", "packageName = " + packageName);
                    if (TextUtils.isEmpty(packageName)) {
                        return;
                    }
                    list = mAppInfoManager.getAllAppInfos();
                    if (mSpUtil.getBoolean(Constants.EYESHIELD, false)) {
                        for (AppInfo appInfo : list) {
                            if (getLauncherTopApp(EyesCareService.this, mActivityManager).equals(appInfo.getPackageName())) {
                                if (appInfo.isCustomPattern()) {
                                    if (appInfo.isCustomLight() && appInfo.isCustomColor()) {
                                        mColor = Color.argb(appInfo.getAlpha(), appInfo.getRed(), appInfo.getGreen(), appInfo.getBule());
                                    } else {
                                        if (appInfo.isCustomLight()) {
                                            alpha = appInfo.getAlpha();
                                        } else {
                                            alpha = mSpUtil.getInt(Constants.ALPHA, 26);
                                        }
                                        if (appInfo.isCustomColor()) {
                                            red = appInfo.getRed();
                                            green = appInfo.getGreen();
                                            bule = appInfo.getBule();
                                        } else {
                                            red = mSpUtil.getInt(Constants.RED, 54);
                                            green = mSpUtil.getInt(Constants.GREEN, 36);
                                            bule = mSpUtil.getInt(Constants.BLUE, 0);
                                        }
                                        mColor = Color.argb(alpha, red, green, bule);
                                    }
                                }
                            } else {
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
            long beginTime = endTime - 10000;
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
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification = new Notification();
        contentView = new RemoteViews(getPackageName(), R.layout.notification);
        notificationIntent = new Intent(this, SettingsActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(contentIntent);
        builder.setContent(contentView);
        mNotification = builder.build();
        //mNotification.flags |= Notification.FLAG_INSISTENT;
        //mNotification.flags |= Notification.FLAG_NO_CLEAR;
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(0, mNotification);
    }
}
