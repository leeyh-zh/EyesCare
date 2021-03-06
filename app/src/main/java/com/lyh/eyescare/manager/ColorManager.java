package com.lyh.eyescare.manager;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.lyh.eyescare.R;

/**
 * Created by lyh on 2017/3/27.
 */

public class ColorManager {

    //public static ColorView mColorView;
    private static WindowManager mWindowManager;
    private static LinearLayout mColorView;
    private static WindowManager.LayoutParams mLayoutParams;

    public static void addColorView(Context context) {
        if (mColorView == null) {
            mWindowManager = getWindowManager(context);
            mColorView = (LinearLayout) LinearLayout.inflate(context, R.layout.float_layout, null);
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
            mColorView.setFitsSystemWindows(true);
            mColorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            // 调整悬浮窗显示的停靠位置为左侧置顶
            mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
            mLayoutParams.x = 0;
            mLayoutParams.y = 0;
            // 设置悬浮窗口长宽数据
            mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            // 获取浮动窗口视图所在布局
            mWindowManager.addView(mColorView, mLayoutParams);
        }
    }

    public static void removeColorView(Context context) {
        if (mColorView != null) {
            getWindowManager(context).removeView(mColorView);
            mColorView = null;
        }
    }

    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public static void changeColor(int color) {
        if (mColorView != null) {
            mColorView.setBackgroundColor(color);
        }
    }

    public static String toHexEncoding(int red, int green, int bule) {
        String R, G, B;
        StringBuilder sb = new StringBuilder();
        R = Integer.toHexString(red).toUpperCase();
        G = Integer.toHexString(green).toUpperCase();
        B = Integer.toHexString(bule).toUpperCase();
        //判断获取到的R,G,B值的长度 如果长度等于1 给R,G,B值的前边添0
        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;

        sb.append("#");
        sb.append(R);
        sb.append(G);
        sb.append(B);
        return sb.toString();
    }
}
