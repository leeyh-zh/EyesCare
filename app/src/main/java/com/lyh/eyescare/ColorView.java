package com.lyh.eyescare;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by lyh on 2017/3/27.
 */

public class ColorView extends LinearLayout {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private View colorWindow;

    public ColorView(Context context) {
        this(context, null);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    private void initView() {
        colorWindow = inflate(getContext(), R.layout.float_layout, this);
        mLayoutParams = new WindowManager.LayoutParams();
        // 获取的是WindowManagerImpl.CompatModeWrapper
        // 设置window type
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        // 设置图片格式，效果为背景透明
        mLayoutParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        // 调整悬浮窗显示的停靠位置为左侧置顶
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
        // 设置悬浮窗口长宽数据
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        // 获取浮动窗口视图所在布局
        mWindowManager.addView(colorWindow, mLayoutParams);
    }
}
