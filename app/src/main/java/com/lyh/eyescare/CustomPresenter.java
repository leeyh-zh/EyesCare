package com.lyh.eyescare;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.lyh.eyescare.bean.AppInfo;
import com.lyh.eyescare.manager.AppInfoManager;

import java.util.Iterator;
import java.util.List;

/**
 * Created by lyh on 2017/4/1.
 */

public class CustomPresenter implements CustomContract.Presenter {

    private CustomContract.View mView;
    private PackageManager mPackageManager;
    private Context mContext;
    private AppInfoManager mAppInfoManager;
    private LoadAppInfo mLoadAppInfo;

    public CustomPresenter(CustomContract.View view, Context context) {
        mView = view;
        mContext = context;
        mPackageManager = mContext.getPackageManager();
        mAppInfoManager = new AppInfoManager(context);
    }

    @Override
    public void loadAppInfo(Context context) {
        mLoadAppInfo = new LoadAppInfo();
        mLoadAppInfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void searchAppInfo(String search, ISearchResultListener listener) {
        new SearchInfoAsyncTask(listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, search);
    }

    @Override
    public void onDestroy() {
        if (mLoadAppInfo != null && mLoadAppInfo.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadAppInfo.cancel(true);
        }
    }


    private class LoadAppInfo extends AsyncTask<Void, String, List<AppInfo>> {

        @Override
        protected List<AppInfo> doInBackground(Void... params) {
            List<AppInfo> appInfos = mAppInfoManager.getAllAppInfos();
            Iterator<AppInfo> infoIterator = appInfos.iterator();
            while (infoIterator.hasNext()) {
                AppInfo appInfo = infoIterator.next();
                try {
                    ApplicationInfo applicationInfo = mPackageManager.getApplicationInfo(appInfo.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
                    if (appInfo == null || mPackageManager.getApplicationIcon(applicationInfo) == null) {
                        infoIterator.remove();
                        continue;
                    } else {
                        appInfo.setApplicationInfo(applicationInfo);
                        if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                            appInfo.setSysApp(true);
                            appInfo.setTopTitle("系统应用");
                        } else {
                            appInfo.setSysApp(false);
                            appInfo.setTopTitle("用户应用");
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    infoIterator.remove();
                }
            }
            return appInfos;
        }

        @Override
        protected void onPostExecute(List<AppInfo> appInfoList) {
            super.onPostExecute(appInfoList);
            mView.loadAppInfoSuccess(appInfoList);
        }
    }

    private class SearchInfoAsyncTask extends AsyncTask<String, Void, List<AppInfo>> {
        private ISearchResultListener mSearchResultListener;

        public SearchInfoAsyncTask(ISearchResultListener searchResultListener) {
            mSearchResultListener = searchResultListener;
        }

        @Override
        protected List<AppInfo> doInBackground(String... params) {
            List<AppInfo> appInfos = mAppInfoManager.queryBlurryList(params[0]);
            Iterator<AppInfo> infoIterator = appInfos.iterator();
            while (infoIterator.hasNext()) {
                AppInfo info = infoIterator.next();
                try {
                    ApplicationInfo applicationInfo = mPackageManager.getApplicationInfo(info.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
                    if (appInfos == null || mPackageManager.getApplicationIcon(applicationInfo) == null) {
                        infoIterator.remove(); //将有错的app移除
                        continue;
                    } else {
                        info.setApplicationInfo(applicationInfo);
                        if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                            info.setSysApp(true);
                            info.setTopTitle("系统应用");
                        } else {
                            info.setSysApp(false);
                            info.setTopTitle("用户应用");
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    infoIterator.remove();
                }
            }
            return appInfos;
        }

        @Override
        protected void onPostExecute(List<AppInfo> appInfos) {
            super.onPostExecute(appInfos);
            mSearchResultListener.onSearchResult(appInfos);
        }
    }

    public interface ISearchResultListener {
        void onSearchResult(List<AppInfo> appInfos);
    }
}
