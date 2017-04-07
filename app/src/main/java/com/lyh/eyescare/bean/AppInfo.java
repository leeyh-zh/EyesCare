package com.lyh.eyescare.bean;

import android.content.pm.ApplicationInfo;
import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by lyh on 2017/4/1.
 */

public class AppInfo extends DataSupport implements Parcelable {
    private long id;
    private String packageName;
    private String appName;
    private boolean isCustomPattern;  //是否开启自定义
    private boolean isCustomLight;  //是否开启自定义
    private boolean isCustomColor;  //是否开启自定义
    private ApplicationInfo applicationInfo;
    private boolean isSysApp; //是否是系统应用
    private String topTitle;

    private int alpha;
    private int red;
    private int green;
    private int bule;

    public AppInfo(String packageName) {
        this.packageName = packageName;
        isCustomPattern = true;
        isCustomLight = false;
        isCustomColor = false;
        alpha = 26;
        red = 54;
        green = 36;
        bule = 0;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBule() {
        return bule;
    }

    public void setBule(int bule) {
        this.bule = bule;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isCustomPattern() {
        return isCustomPattern;
    }

    public void setCustomPattern(boolean customPattern) {
        isCustomPattern = customPattern;
    }

    public boolean isCustomLight() {
        return isCustomLight;
    }

    public void setCustomLight(boolean customLight) {
        isCustomLight = customLight;
    }

    public boolean isCustomColor() {
        return isCustomColor;
    }

    public void setCustomColor(boolean customColor) {
        isCustomColor = customColor;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public boolean isSysApp() {
        return isSysApp;
    }

    public void setSysApp(boolean sysApp) {
        isSysApp = sysApp;
    }

    public String getTopTitle() {
        return topTitle;
    }

    public void setTopTitle(String topTitle) {
        this.topTitle = topTitle;
    }

    protected AppInfo(Parcel in) {
        id = in.readLong();
        packageName = in.readString();
        appName = in.readString();
        isCustomPattern = in.readByte() != 0;
        isCustomLight = in.readByte() != 0;
        isCustomColor = in.readByte() != 0;
        applicationInfo = in.readParcelable(ApplicationInfo.class.getClassLoader());
        isSysApp = in.readByte() != 0;
        topTitle = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(packageName);
        dest.writeString(appName);
        dest.writeByte((byte) (isCustomPattern ? 1 : 0));
        dest.writeByte((byte) (isCustomLight ? 1 : 0));
        dest.writeByte((byte) (isCustomColor ? 1 : 0));
        dest.writeParcelable(applicationInfo, flags);
        dest.writeByte((byte) (isSysApp ? 1 : 0));
        dest.writeString(topTitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };
}
