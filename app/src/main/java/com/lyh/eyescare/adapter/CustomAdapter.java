package com.lyh.eyescare.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyh.eyescare.R;
import com.lyh.eyescare.bean.AppInfo;
import com.lyh.eyescare.manager.AppInfoManager;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MainViewHolder> {

    private List<AppInfo> mAppInfo = new ArrayList<>();
    private Context mContext;
    private PackageManager packageManager;
    private AppInfoManager mAppInfoManager;

    public CustomAdapter(Context mContext) {
        this.mContext = mContext;
        packageManager = mContext.getPackageManager();
        mAppInfoManager = new AppInfoManager(mContext);
    }

    public void setInfos(List<AppInfo> lockInfos) {
        mAppInfo.clear();
        mAppInfo.addAll(lockInfos);
        notifyDataSetChanged();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_list, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        final AppInfo appInfo = mAppInfo.get(position);
        initData(holder.mAppName, holder.mAppIcon, appInfo);
    }

    /**
     * 初始化数据
     */
    private void initData(TextView appName,  ImageView appIcon, AppInfo appInfo) {
        appName.setText(packageManager.getApplicationLabel(appInfo.getApplicationInfo()));
        ApplicationInfo applicationInfo = appInfo.getApplicationInfo();
        appIcon.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));
    }

    @Override
    public int getItemCount() {
        return mAppInfo.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        private ImageView mAppIcon;
        private TextView mAppName;
        private CheckBox mSwitchCompat;

        public MainViewHolder(View itemView) {
            super(itemView);
            mAppIcon = (ImageView) itemView.findViewById(R.id.app_icon);
            mAppName = (TextView) itemView.findViewById(R.id.app_name);
        }
    }
}
