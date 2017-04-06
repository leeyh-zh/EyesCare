package com.lyh.eyescare.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.lyh.eyescare.R;
import com.lyh.eyescare.bean.AppInfo;
import com.lyh.eyescare.manager.AppInfoManager;
import com.lyh.eyescare.view.CustomPopWindow;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MainViewHolder> {

    private List<AppInfo> mAppInfo = new ArrayList<>();
    private Context mContext;
    private PackageManager packageManager;
    private AppInfoManager mAppInfoManager;
    private Switch itemCustomPatternSwitch;
    private Switch itemCustomLightSwitch;
    private Switch itemCustomColorSwitch;
    private LinearLayout itemCustomPattern;
    private LinearLayout itemCustomLight;
    private LinearLayout itemCustomColor;
    private TextView popAppName;
    private TextView popPackageName;
    private ImageView popIcon;

    private ContentValues values;
    private final AppInfoManager appInfoManager;

    public CustomAdapter(Context mContext) {
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
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_list, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        AppInfo appInfo = mAppInfo.get(position);
        initData(appInfo, holder, position);
    }

    /**
     * 初始化数据
     */
    private void initData(final AppInfo appInfo, final MainViewHolder holder, final int position) {
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
        holder.customItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomItemSetting(appInfo, holder, position);
            }
        });
    }

    private void showCustomItemSetting(final AppInfo appInfo, final MainViewHolder holder, final int position) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.item_custom_setting, null);

        popAppName = (TextView) contentView.findViewById(R.id.item_pop_appname);
        popAppName.setText(appInfo.getAppName());
        popPackageName = (TextView) contentView.findViewById(R.id.item_pop_packagename);
        popPackageName.setText(appInfo.getPackageName());
        popIcon = (ImageView) contentView.findViewById(R.id.item_pop_icon);
        popIcon.setImageDrawable(packageManager.getApplicationIcon(appInfo.getApplicationInfo()));

        CustomPopWindow setting = new CustomPopWindow.PopupWindowBuilder(mContext)
                .setView(contentView)
                .setFocusable(true)
                .setOutsideTouchable(true)
                .setBgDarkAlpha(0.5f)
                .setAnimationStyle(R.style.AnimationBottomFade)
                .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //DataSupport.updateAll(AppInfo.class, values, "packageName = ?", appInfo.getPackageName());
                        //appInfoManager.updateAppStatus(appInfo.getPackageName(),values);
                        //setInfos(appInfoManager.getAllAppInfos());
                    }
                })
                .size(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .enableBackgroundDark(true)
                .create()
                .showAtLocation(holder.customItem, Gravity.BOTTOM, 0, 0);

        itemCustomPatternSwitch = (Switch) contentView.findViewById(R.id.item_custom_pattern_switch);
        itemCustomLightSwitch = (Switch) contentView.findViewById(R.id.item_custom_light_switch);
        itemCustomColorSwitch = (Switch) contentView.findViewById(R.id.item_custom_color_switch);
        itemCustomPattern = (LinearLayout) contentView.findViewById(R.id.item_custom_pattern);
        itemCustomLight = (LinearLayout) contentView.findViewById(R.id.item_custom_light);
        itemCustomColor = (LinearLayout) contentView.findViewById(R.id.item_custom_color);
        itemCustomPatternSwitch.setChecked(appInfo.isCustomPattern());
        itemCustomLightSwitch.setChecked(appInfo.isCustomLight());
        itemCustomColorSwitch.setChecked(appInfo.isCustomColor());

        values = new ContentValues();
        itemCustomPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCustomPatternSwitch.isChecked()) {
                    itemCustomPatternSwitch.setChecked(false);
                    holder.mCustomPattern.setText(R.string.text_off);
                    holder.mCustomPattern.setTextColor(mContext.getResources().getColor(R.color.off_color));
                    values.put("isCustomPattern", false);
                } else {
                    itemCustomPatternSwitch.setChecked(true);
                    holder.mCustomPattern.setText(R.string.text_on);
                    holder.mCustomPattern.setTextColor(mContext.getResources().getColor(R.color.on_color));
                    values.put("isCustomPattern", true);
                }
            }
        });

        itemCustomLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCustomLightSwitch.isChecked()) {
                    itemCustomLightSwitch.setChecked(false);
                    holder.mCustomLight.setText(R.string.text_off);
                    holder.mCustomLight.setTextColor(mContext.getResources().getColor(R.color.off_color));
                    values.put("isCustomLight", false);
                } else {
                    itemCustomLightSwitch.setChecked(true);
                    holder.mCustomLight.setText(R.string.text_on);
                    holder.mCustomLight.setTextColor(mContext.getResources().getColor(R.color.on_color));
                    values.put("isCustomLight", true);
                }
            }
        });

        itemCustomColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCustomColorSwitch.isChecked()) {
                    itemCustomColorSwitch.setChecked(false);
                    holder.mCustomColor.setText(R.string.text_off);
                    holder.mCustomColor.setTextColor(mContext.getResources().getColor(R.color.off_color));
                    values.put("isCustomColor", false);
                } else {
                    itemCustomColorSwitch.setChecked(true);
                    holder.mCustomColor.setText(R.string.text_on);
                    holder.mCustomColor.setTextColor(mContext.getResources().getColor(R.color.on_color));
                    values.put("isCustomColor", true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAppInfo.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout customItem;
        private ImageView mAppIcon;
        private String mPackageName;
        private TextView mAppName;
        private TextView mCustomPattern;
        private TextView mCustomLight;
        private TextView mCustomColor;

        public MainViewHolder(View itemView) {
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
