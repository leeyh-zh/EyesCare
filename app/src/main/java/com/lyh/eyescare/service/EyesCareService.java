package com.lyh.eyescare.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.lyh.eyescare.ColorManager;
import com.lyh.eyescare.ColorView;

/**
 * Created by lyh on 2017/3/27.
 */

public class EyesCareService extends AccessibilityService {

    public static final int TYPE_OPEN = 0;
    public static final int TYPE_CLOSE = 1;

    private ColorView mColorView;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "onStartCommand", Toast.LENGTH_SHORT).show();
        if (intent != null) {
            int status = intent.getIntExtra("status",0);
            int color = intent.getIntExtra("color", 0xffffffff);
            if (status == TYPE_OPEN) {
                ColorManager.addColorView(this);
                ColorManager.changeColor(color);

            } else {
                ColorManager.removeColorView(this);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();

    }
}
