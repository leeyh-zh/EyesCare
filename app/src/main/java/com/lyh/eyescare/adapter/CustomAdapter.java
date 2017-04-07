package com.lyh.eyescare.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.lyh.eyescare.CustomContract;
import com.lyh.eyescare.CustomPresenter;
import com.lyh.eyescare.R;
import com.lyh.eyescare.bean.AppInfo;
import com.lyh.eyescare.constant.Constants;
import com.lyh.eyescare.manager.AppInfoManager;
import com.lyh.eyescare.manager.ColorManager;
import com.lyh.eyescare.view.CustomPopWindow;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MainViewHolder>
        implements CustomContract.View, View.OnClickListener {

    private List<AppInfo> mAppInfo = new ArrayList<>();
    private Activity mContext;
    private PackageManager packageManager;
    private AppInfoManager mAppInfoManager;

    private Switch itemCustomPatternSwitch, itemCustomLightSwitch, itemCustomColorSwitch;
    private LinearLayout itemCustomPattern, itemCustomLight, itemCustomColor;
    private TextView popAppName, popPackageName, popLightValue, popColorValue;
    private ImageView popIcon;
    private SeekBar alphaSeekBar, redSeekBar, greenSeedBar, buleSeedBar;
    private Button btnNoColor, btnFilterBlue, btnCoolColors, btnWarmTone;
    private Button btnEyeGreen, btnInkBlue, btnTeaBlack, btnStrawberryPowder;
    private int red, green, blue;

    private ContentValues values;
    private final AppInfoManager appInfoManager;
    private CustomPresenter mCustomPresenter;
    private int mStatus;
    private CustomPopWindow mPopBtnColor;

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
        final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        holder.customItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(holder.customItem.getApplicationWindowToken(), 0);
                }
                showCustomItemSetting(appInfo, holder, position);
            }
        });
    }

    private void showCustomItemSetting(final AppInfo appInfo, final MainViewHolder holder, final int position) {
        initPopView(appInfo, holder);
        dataSettings(appInfo, holder);
    }

    private void initPopView(final AppInfo appInfo, final MainViewHolder holder) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.item_custom_setting, null);
        popAppName = (TextView) contentView.findViewById(R.id.item_pop_appname);
        popPackageName = (TextView) contentView.findViewById(R.id.item_pop_packagename);
        popIcon = (ImageView) contentView.findViewById(R.id.item_pop_icon);
        popLightValue = (TextView) contentView.findViewById(R.id.tv_light_value);
        popColorValue = (TextView) contentView.findViewById(R.id.tv_color_value);
        alphaSeekBar = (SeekBar) contentView.findViewById(R.id.alpha_seekBar);
        redSeekBar = (SeekBar) contentView.findViewById(R.id.red_seekBar);
        greenSeedBar = (SeekBar) contentView.findViewById(R.id.green_seedBar);
        buleSeedBar = (SeekBar) contentView.findViewById(R.id.bule_seedBar);
        btnNoColor = (Button) contentView.findViewById(R.id.btn_no_color);
        btnFilterBlue = (Button) contentView.findViewById(R.id.btn_filter_blue);
        btnCoolColors = (Button) contentView.findViewById(R.id.btn_cool_colors);
        btnWarmTone = (Button) contentView.findViewById(R.id.btn_warm_tone);
        btnEyeGreen = (Button) contentView.findViewById(R.id.btn_eye_green);
        btnInkBlue = (Button) contentView.findViewById(R.id.btn_ink_blue);
        btnTeaBlack = (Button) contentView.findViewById(R.id.btn_tea_black);
        btnStrawberryPowder = (Button) contentView.findViewById(R.id.btn_strawberry_powder);
        itemCustomPatternSwitch = (Switch) contentView.findViewById(R.id.item_custom_pattern_switch);
        itemCustomLightSwitch = (Switch) contentView.findViewById(R.id.item_custom_light_switch);
        itemCustomColorSwitch = (Switch) contentView.findViewById(R.id.item_custom_color_switch);
        itemCustomPattern = (LinearLayout) contentView.findViewById(R.id.item_custom_pattern);
        itemCustomLight = (LinearLayout) contentView.findViewById(R.id.item_custom_light);
        itemCustomColor = (LinearLayout) contentView.findViewById(R.id.item_custom_color);

        btnNoColor.setOnClickListener(this);
        btnFilterBlue.setOnClickListener(this);
        btnCoolColors.setOnClickListener(this);
        btnWarmTone.setOnClickListener(this);
        btnEyeGreen.setOnClickListener(this);
        btnInkBlue.setOnClickListener(this);
        btnTeaBlack.setOnClickListener(this);
        btnStrawberryPowder.setOnClickListener(this);


        popAppName.setText(appInfo.getAppName());
        popPackageName.setText(appInfo.getPackageName());
        popIcon.setImageDrawable(packageManager.getApplicationIcon(appInfo.getApplicationInfo()));
        itemCustomPatternSwitch.setChecked(appInfo.isCustomPattern());
        itemCustomLightSwitch.setChecked(appInfo.isCustomLight());
        itemCustomColorSwitch.setChecked(appInfo.isCustomColor());
        popLightValue.setText(appInfo.getAlpha() * 100 / 255 + "%");
        alphaSeekBar.setProgress(appInfo.getAlpha());
        redSeekBar.setProgress(appInfo.getRed());
        greenSeedBar.setProgress(appInfo.getGreen());
        buleSeedBar.setProgress(appInfo.getBule());
        CustomPopWindow setting = new CustomPopWindow.PopupWindowBuilder(mContext)
                .setView(contentView)
                .setFocusable(true)
                .setOutsideTouchable(true)
                .setBgDarkAlpha(0.5f)
                .setAnimationStyle(R.style.AnimationBottomFade)
                .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        DataSupport.updateAll(AppInfo.class, values, "packageName = ?", appInfo.getPackageName());
                        //appInfoManager.updateAppStatus(appInfo.getPackageName(),values);
                        //setInfos(appInfoManager.getAllAppInfos());
                        mCustomPresenter.loadAppInfo(mContext);
                    }
                })
                .size(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .enableBackgroundDark(true)
                .create()
                .showAtLocation(holder.customItem, Gravity.BOTTOM, 0, 0);
    }

    private void dataSettings(AppInfo appInfo, final MainViewHolder holder) {
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

        itemCustomLightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    holder.mCustomLight.setText(R.string.text_off);
                    holder.mCustomLight.setTextColor(mContext.getResources().getColor(R.color.off_color));
                    values.put("isCustomLight", false);
                } else {
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

        alphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                values.put(Constants.ALPHA, progress);
                popLightValue.setText(progress * 100 / 255 + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                values.put(Constants.RED, progress);
                red = progress;
                popColorValue.setText(ColorManager.toHexEncoding(red, green, blue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        greenSeedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                values.put(Constants.GREEN, progress);
                green = progress;
                popColorValue.setText(ColorManager.toHexEncoding(red, green, blue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        buleSeedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                values.put(Constants.BLUE, progress);
                blue = progress;
                popColorValue.setText(ColorManager.toHexEncoding(red, green, blue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAppInfo.size();
    }

    @Override
    public void loadAppInfoSuccess(List<AppInfo> list) {
        //setInfos(list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_no_color:
                red = 0;
                green = 0;
                blue = 0;
                break;
            case R.id.btn_filter_blue:
                showPopTop(btnFilterBlue);
                break;
            case R.id.btn_cool_colors:
                showPopTop(btnCoolColors);
                break;
            case R.id.btn_warm_tone:
                showPopTop(btnWarmTone);
                break;
            case R.id.btn_eye_green:
                showPopTop(btnEyeGreen);
                break;
            case R.id.btn_ink_blue:
                showPopTop(btnInkBlue);
                break;
            case R.id.btn_tea_black:
                showPopTop(btnTeaBlack);
                break;
            case R.id.btn_strawberry_powder:
                showPopTop(btnStrawberryPowder);
                break;
        }

    }

    private void showPopTop(final Button button) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_color, null);
        handleLogic(contentView, button.getId());
        mPopBtnColor = new CustomPopWindow.PopupWindowBuilder(mContext)
                .setView(contentView)
                .create();
        mPopBtnColor.showAsDropDown(button, 0, -(button.getHeight() / 4 + mPopBtnColor.getHeight()));
    }

    private void handleLogic(View contentView, final int id) {
        Button.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopBtnColor != null) {
                    mPopBtnColor.dissmiss();
                }
                String showContent = "";
                switch (v.getId()) {
                    case R.id.menu1:
                        settingRGB(id, R.id.menu1);
                        break;
                    case R.id.menu2:
                        settingRGB(id, R.id.menu2);
                        break;
                    case R.id.menu3:
                        settingRGB(id, R.id.menu3);
                        break;
                    case R.id.menu4:
                        settingRGB(id, R.id.menu4);
                        break;
                    case R.id.menu5:
                        settingRGB(id, R.id.menu5);
                        break;
                }
            }
        };
        contentView.findViewById(R.id.menu1).setOnClickListener(listener);
        contentView.findViewById(R.id.menu2).setOnClickListener(listener);
        contentView.findViewById(R.id.menu3).setOnClickListener(listener);
        contentView.findViewById(R.id.menu4).setOnClickListener(listener);
        contentView.findViewById(R.id.menu5).setOnClickListener(listener);
    }

    private void settingRGB(int color, int button) {
        int i = 0;
        if (button == R.id.menu1) {
            i = 0;
        } else if (button == R.id.menu2) {
            i = 1;
        } else if (button == R.id.menu3) {
            i = 2;
        } else if (button == R.id.menu4) {
            i = 3;
        } else if (button == R.id.menu5) {
            i = 4;
        }
        if (color == R.id.btn_filter_blue) {
            red = Constants.FilterBlue[i][0];
            green = Constants.FilterBlue[i][1];
            blue = Constants.FilterBlue[i][2];
        } else if (color == R.id.btn_cool_colors) {
            red = Constants.CoolColors[i][0];
            green = Constants.CoolColors[i][1];
            blue = Constants.CoolColors[i][2];
        } else if (color == R.id.btn_warm_tone) {
            red = Constants.WarmTone[i][0];
            green = Constants.WarmTone[i][1];
            blue = Constants.WarmTone[i][2];
        } else if (color == R.id.btn_eye_green) {
            red = Constants.EyeGreen[i][0];
            green = Constants.EyeGreen[i][1];
            blue = Constants.EyeGreen[i][2];
        } else if (color == R.id.btn_ink_blue) {
            red = Constants.InkBlue[i][0];
            green = Constants.InkBlue[i][1];
            blue = Constants.InkBlue[i][2];
        } else if (color == R.id.btn_tea_black) {
            red = Constants.TeaBlack[i][0];
            green = Constants.TeaBlack[i][1];
            blue = Constants.TeaBlack[i][2];
        } else if (color == R.id.btn_strawberry_powder) {
            red = Constants.StrawberryPowder[i][0];
            green = Constants.StrawberryPowder[i][1];
            blue = Constants.StrawberryPowder[i][2];
        }
        redSeekBar.setProgress(red);
        greenSeedBar.setProgress(green);
        buleSeedBar.setProgress(blue);
        popColorValue.setText(ColorManager.toHexEncoding(red, green, blue));
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
