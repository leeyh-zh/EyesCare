package com.lyh.eyescare.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lyh.eyescare.MainActivity;
import com.lyh.eyescare.R;
import com.lyh.eyescare.constant.Status;

/**
 * Created by lyh on 2017/3/23.
 */

public class BackService extends Service {

    private IBinder binder = new LocalService();

    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager windowManager;
    private LinearLayout mFloatLayout;
    private Notification notification;
    private NotificationManager mNotificationManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
    }

    private void createFloatView() {
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        // 获取的是WindowManagerImpl.CompatModeWrapper
        // 设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        // 调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        // 获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        windowManager.addView(mFloatLayout, wmParams);
        // 消息通知栏
        // 定义NotificationManager
        Notification.Builder builder = new Notification.Builder(getApplication());
        builder.setContentTitle("护眼模式已开启");
        builder.setContentText("单击此处返回主界面");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("护眼模式开启");
        builder.setContentInfo("补充内容");
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 定义通知栏展现的内容信息
        // 定义下拉通知栏时要展现的内容信
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        notification = builder.build();
        mNotificationManager.notify(1, notification);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(getApplicationContext(), "时间到,夜间模式关闭！", Toast.LENGTH_SHORT)
                            .show();
                    stopSelf();
                    Intent intent = new Intent();
                    intent.setClass(BackService.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("status", Status.STOP);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    public void showNotifiy(Boolean show) {
        if (show) {
            mNotificationManager.notify(1, notification);
        } else {
            mNotificationManager.cancel(1);
        }
    }

    public class LocalService extends Binder {
        public BackService getService() {
            return BackService.this;
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        int status = intent.getIntExtra("status", 0);
        if (status == 1) {
            Message msg = new Message();
            msg.what = 1;
            mHandler.sendMessage(msg);
        }
        mFloatLayout.setBackgroundColor(intent.getIntExtra("color", 0xffffffff));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void changeColor(int color) {
        mFloatLayout.setBackgroundColor(color);
    }

    @Override
    public void onDestroy() {
        showNotifiy(false);
        windowManager.removeView(mFloatLayout);
        super.onDestroy();
    }
}
