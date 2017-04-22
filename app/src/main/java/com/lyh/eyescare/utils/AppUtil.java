package com.lyh.eyescare.utils;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.lyh.eyescare.base.BaseActivity;

import java.util.List;

/**
 * Created by xian on 2017/2/17.
 */

public class AppUtil {
    /**
     * 判断是否已经获取 有权查看使用情况的应用程序 权限
     *
     * @param context
     * @return
     */
    public static boolean isStatAccessPermissionSet(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName);
                return appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName) == AppOpsManager.MODE_ALLOWED;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 查看是存在查看使用情况的应用程序界面
     *
     * @return
     */
    public static boolean isNoOption(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 是否有开启通知栏服务
     */
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    public static boolean isNotificationSettingOn(Context mContext) {
        String pkgName = mContext.getPackageName();
        final String flat = Settings.Secure.getString(mContext.getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Home键操作
     */
    public static void goHome(BaseActivity activity) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        activity.startActivity(homeIntent);
        activity.finish();
    }


}
