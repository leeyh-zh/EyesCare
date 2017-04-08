package com.lyh.eyescare.adapter;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyh.eyescare.CustomContract;
import com.lyh.eyescare.CustomPresenter;
import com.lyh.eyescare.R;
import com.lyh.eyescare.bean.AppInfo;
import com.lyh.eyescare.manager.AppInfoManager;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.AppInfoViewHolder>
        implements CustomContract.View, View.OnClickListener {

    private List<AppInfo> mAppInfo = new ArrayList<>();
    private Activity mContext;
    private PackageManager packageManager;
    private AppInfoManager mAppInfoManager;

    private final AppInfoManager appInfoManager;
    private CustomPresenter mCustomPresenter;
    private int itemPosition;

    public CustomAdapter(Activity mContext) {
        this.mContext = mContext;
        packageManager = mContext.getPackageManager();
        mAppInfoManager = new AppInfoManager(mContext);
        appInfoManager = new AppInfoManager(mContext);
    }

    public void setInfos(List<AppInfo> appInfo) {
        mAppInfo.clear();
        mAppInfo.addAll(appInfo);
        notifyDataSetChanged();
    }

    @Override
    public AppInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_list, parent, false);
        view.setOnClickListener(this);
        return new AppInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppInfoViewHolder holder, int position) {
        AppInfo appInfo = mAppInfo.get(position);
        holder.itemView.setTag(appInfo);
        itemPosition = position;
        initData(appInfo, holder, position);
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view, itemPosition, (AppInfo) view.getTag());
        }
    }

    /**
     * 添加点击监听
     */
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position, AppInfo data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 初始化数据
     */
    private void initData(final AppInfo appInfo, final AppInfoViewHolder holder, final int position) {
        mCustomPresenter = new CustomPresenter(this, mContext);
        holder.mAppName.setText(packageManager.getApplicationLabel(appInfo.getApplicationInfo()));
        holder.mPackageName = appInfo.getPackageName();
        ApplicationInfo applicationInfo = appInfo.getApplicationInfo();
        holder.mAppIcon.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));
        holder.mCustomPattern.setText(appInfo.isCustomPattern() ? R.string.text_on : R.string.text_off);
        holder.mCustomPattern.setTextColor(appInfo.isCustomPattern() ? mContext.getResources().getColor(R.color.on_color)
                : mContext.getResources().getColor(R.color.off_color));
        holder.mCustomLight.setText(appInfo.isCustomLight() ? R.string.text_on : R.string.text_off);
        holder.mCustomLight.setTextColor(appInfo.isCustomLight() ? mContext.getResources().getColor(R.color.on_color)
                : mContext.getResources().getColor(R.color.off_color));
        holder.mCustomColor.setText(appInfo.isCustomColor() ? R.string.text_on : R.string.text_off);
        holder.mCustomColor.setTextColor(appInfo.isCustomColor() ? mContext.getResources().getColor(R.color.on_color)
                : mContext.getResources().getColor(R.color.off_color));
    }

    @Override
    public int getItemCount() {
        return mAppInfo.size();
    }

    @Override
    public void loadAppInfoSuccess(List<AppInfo> list) {
        //setInfos(list);
    }

    public class AppInfoViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout customItem;
        private ImageView mAppIcon;
        private String mPackageName;
        private TextView mAppName;
        private TextView mCustomPattern;
        private TextView mCustomLight;
        private TextView mCustomColor;

        public AppInfoViewHolder(View itemView) {
            super(itemView);
            customItem = (RelativeLayout) itemView.findViewById(R.id.custom_item);
            mAppIcon = (ImageView) itemView.findViewById(R.id.app_icon);
            mAppName = (TextView) itemView.findViewById(R.id.app_name);
            mCustomPattern = (TextView) itemView.findViewById(R.id.tv_custom_pattern);
            mCustomLight = (TextView) itemView.findViewById(R.id.tv_custom_light);
            mCustomColor = (TextView) itemView.findViewById(R.id.tv_custom_color);
        }
    }


}
