package com.lyh.eyescare.manager;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.lyh.eyescare.bean.AppInfo;

import org.litepal.crud.DataSupport;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by lyh on 2017/4/1.
 */

public class AppInfoManager {

    private PackageManager mPackageManager;
    private Context mContext;

    public AppInfoManager(Context context) {
        mContext = context;
        mPackageManager = context.getPackageManager();
    }


    /**
     * 查找所有
     */
    public synchronized List<AppInfo> getAllAppInfos() {
        List<AppInfo> appInfoList = DataSupport.findAll(AppInfo.class);
        Collections.sort(appInfoList, appInfoListComparator);
        return appInfoList;
    }

    /**
     * 删除数据
     */
    public synchronized void deleteAppInfo(List<AppInfo> appInfoList) {
        for (AppInfo info : appInfoList) {
            DataSupport.deleteAll(AppInfo.class, "packageName = ?", info.getPackageName());
        }

    }

    /**
     * 将手机应用信息插入数据库
     */
    public synchronized void instanceAppInfoTable(List<ResolveInfo> resolveInfos) throws PackageManager.NameNotFoundException {
        List<AppInfo> appInfoList = new ArrayList<>();

        for (ResolveInfo resolveInfo : resolveInfos) {
            AppInfo appInfo = new AppInfo(resolveInfo.activityInfo.packageName);
            ApplicationInfo applicationInfo = mPackageManager.getApplicationInfo(appInfo.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
            String appName = mPackageManager.getApplicationLabel(applicationInfo).toString();
            appInfo.setAppName(appName);
            appInfoList.add(appInfo);
        }
        appInfoList = clearRepeatAppInfo(appInfoList);
        DataSupport.saveAll(appInfoList);
    }

    /**
     * 去除重复信息
     *
     * @param appInfoList
     * @return
     */
    public List<AppInfo> clearRepeatAppInfo(List<AppInfo> appInfoList) {
        HashMap<String, AppInfo> hashMap = new HashMap<>();
        for (AppInfo appInfo : appInfoList) {
            if (!hashMap.containsKey(appInfo.getPackageName())) {
                hashMap.put(appInfo.getPackageName(), appInfo);
            }
        }
        List<AppInfo> appInfos = new ArrayList<>();
        for (HashMap.Entry<String, AppInfo> entry : hashMap.entrySet()) {
            appInfos.add(entry.getValue());
        }
        return appInfos;
    }

    /**
     * 更新数据库app状态
     */
    public void updateAppStatus(String packageName, boolean isEyesCare, boolean isCustomLight, boolean isCustomColor) {
        ContentValues values = new ContentValues();
        values.put("isEyesCare", isEyesCare);
        values.put("isCustomLight", isCustomLight);
        values.put("isCustomColor", isCustomColor);
        DataSupport.updateAll(AppInfo.class, values, "packageName = ?", packageName);
    }

    /**
     * 更改是否启动自定义设置
     */
    public void isCustomColor(String packageName, boolean open) {
        ContentValues values = new ContentValues();
        values.put("isCustom", open);
        DataSupport.updateAll(AppInfo.class, values, "packageName = ?", packageName);
    }

    /**
     * 模糊匹配
     */
    public List<AppInfo> queryBlurryList(String appName) {
        List<AppInfo> infos = DataSupport.where("appName like ?", "%" + appName + "%").find(AppInfo.class);
        return infos;
    }

    private Comparator appInfoListComparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            AppInfo appInfo1 = (AppInfo) o1;
            AppInfo appInfo2 = (AppInfo) o2;
            String one = appInfo1.getAppName();
            String two = appInfo2.getAppName();
            Collator collator = Collator.getInstance(Locale.CHINA);
            int flag = 0;
            if (collator.compare(one, two) < 0) {
                flag = -1;
            } else if (collator.compare(one, two) > 0) {
                flag = 1;
            } else {
                flag = 0;
            }
            return flag;
        }
    };


}
