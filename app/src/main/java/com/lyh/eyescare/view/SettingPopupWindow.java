package com.lyh.eyescare.view;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by lyh on 2017/3/24.
 */

public class SettingPopupWindow extends PopupWindow {
    private Activity activity;
    private WindowManager.LayoutParams params;
    private boolean isShow;
    private WindowManager windowManager;
    private ViewGroup rootView;
    private ViewGroup linearLayout;

    private final int animDuration = 250;//动画执行时间
    private boolean isAniming;//动画是否在执行


    public SettingPopupWindow(Activity activity) {
        this.activity = activity;
        windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
    }
}
