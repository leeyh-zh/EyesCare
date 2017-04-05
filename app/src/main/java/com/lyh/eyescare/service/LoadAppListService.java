package com.lyh.eyescare.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lyh.eyescare.bean.AppInfo;
import com.lyh.eyescare.manager.AppInfoManager;
import com.lyh.eyescare.utils.SpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadAppListService extends IntentService {

    private PackageManager mPackageManager;
    private AppInfoManager mAppInfoManager;
    long time = 0;

    public LoadAppListService() {
        super("LoadAppListService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPackageManager = getPackageManager();
        mAppInfoManager = new AppInfoManager(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent handleIntent) {

        time = System.currentTimeMillis();

        boolean isInitDb = SpUtil.getInstance().getBoolean("initDB", false);

        //每次都获取手机上的所有应用
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = mPackageManager.queryIntentActivities(intent, 0);
        //非第一次，对比数据
        if (isInitDb) {
            Log.i("1111","isInitDb");
            List<ResolveInfo> appList = new ArrayList<>();
            List<AppInfo> dbList = mAppInfoManager.getAllAppInfos(); //获取数据库列表
            //处理应用列表
            for (ResolveInfo resolveInfo : resolveInfos) {
                appList.add(resolveInfo);
            }
            if (appList.size() > dbList.size()) { //如果有安装新应用
                List<ResolveInfo> reslist = new ArrayList<>();
                HashMap<String, AppInfo> hashMap = new HashMap<>();
                for (AppInfo info : dbList) {
                    hashMap.put(info.getPackageName(), info);
                }
                for (ResolveInfo info : appList) {
                    if (!hashMap.containsKey(info.activityInfo.packageName)) {
                        reslist.add(info);
                    }
                }
                try {
                    if (reslist.size() != 0)
                        mAppInfoManager.instanceAppInfoTable(reslist); //将剩下不同的插入数据库
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (appList.size() < dbList.size()) { //如果有卸载应用
                List<AppInfo> commlist = new ArrayList<>();
                HashMap<String, ResolveInfo> hashMap = new HashMap<>();
                for (ResolveInfo info : appList) {
                    hashMap.put(info.activityInfo.packageName, info);
                }
                for (AppInfo info : dbList) {
                    if (!hashMap.containsKey(info.getPackageName())) {
                        commlist.add(info);
                    }
                }
                //Logger.d("有应用卸载，个数是 = " + dbList.size());
                if (commlist.size() != 0)
                    mAppInfoManager.deleteAppInfo(commlist);//将多的从数据库删除
            } else {
                //Logger.d("应用没多没少，正常");
            }
        } else {
            //数据库只插入一次
            Log.i("1111","插入数据库");
            SpUtil.getInstance().putBoolean("initDB", true);
            try {
                mAppInfoManager.instanceAppInfoTable(resolveInfos);    //插入数据库
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        Log.i("1111", "耗时 = " + (System.currentTimeMillis() - time));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppInfoManager = null;
    }

}
