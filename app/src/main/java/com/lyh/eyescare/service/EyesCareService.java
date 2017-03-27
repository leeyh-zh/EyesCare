package com.lyh.eyescare.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.lyh.eyescare.ColorManager;
import com.lyh.eyescare.ColorView;
import com.lyh.eyescare.R;
import com.lyh.eyescare.activity.SettingsActivity;

/**
 * Created by lyh on 2017/3/27.
 */

public class EyesCareService extends AccessibilityService {

    public static final int TYPE_OPEN = 0;
    public static final int TYPE_CLOSE = 1;
    private Notification notification;
    private NotificationManager mNotificationManager;

    private ColorView mColorView;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int status = intent.getIntExtra("status",0);
            int color = intent.getIntExtra("color", 0xffffffff);
            if (status == TYPE_OPEN) {
                ColorManager.addColorView(this);
                ColorManager.changeColor(color);
                Notification.Builder builder = new Notification.Builder(getApplication());
                builder.setContentTitle("护眼模式已开启");
                builder.setContentText("单击此处返回主界面");
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setTicker("护眼模式开启");
                builder.setContentInfo("补充内容");
                builder.setAutoCancel(true);
                builder.setWhen(System.currentTimeMillis());
                notification = new Notification();
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // 定义通知栏展现的内容信息
                // 定义下拉通知栏时要展现的内容信
                Intent notificationIntent = new Intent(this, SettingsActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(contentIntent);
                notification.flags |= Notification.FLAG_ONGOING_EVENT;
                notification.flags |= Notification.FLAG_NO_CLEAR;
                notification.flags |= Notification.FLAG_SHOW_LIGHTS;
                notification.defaults = Notification.DEFAULT_LIGHTS;
                notification = builder.build();
                mNotificationManager.notify(1, notification);
            } else {
                ColorManager.removeColorView(this);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
