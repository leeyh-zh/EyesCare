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
    private ArrayList<AppInfo> list;
    private int mStatus;
    private int mColor;
    private Message mMessage;

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
        mAppInfoManager = new AppInfoManager(getApplicationContext());
        if (intent != null) {
            mStatus = intent.getIntExtra(Constants.STATUS, TYPE_OPEN);
            mColor = intent.getIntExtra(Constants.COLOR, 0xffffffff);
            if (mStatus == TYPE_OPEN) {
                ColorManager.addColorView(this);
                ColorManager.changeColor(mColor);
                showNotification();
            } else {
                ColorManager.removeColorView(this);
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    list = (ArrayList<AppInfo>) mAppInfoManager.getAllAppInfos();
                    Log.d("1111", "packageName = " + getLauncherTopApp(EyesCareService.this, mActivityManager));
                    for (AppInfo appInfo : list) {
                        if (appInfo.getPackageName().equals(getLauncherTopApp(EyesCareService.this, mActivityManager))) {
                            mMessage = mHandler.obtainMessage();
                            mColor = Color.argb(appInfo.getAlpha(), appInfo.getRed(), appInfo.getGreen(), appInfo.getBule());
                            mMessage.what = 0;
                            mMessage.obj = mColor;
                            mHandler.sendMessage(mMessage);
                        }
                    }

                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
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
