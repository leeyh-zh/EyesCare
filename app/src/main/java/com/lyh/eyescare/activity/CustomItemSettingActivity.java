package com.lyh.eyescare.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.lyh.eyescare.R;
import com.lyh.eyescare.bean.AppInfo;
import com.lyh.eyescare.constant.Constants;
import com.lyh.eyescare.manager.ColorManager;
import com.lyh.eyescare.view.CustomPopWindow;

import org.litepal.crud.DataSupport;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lyh.eyescare.R.id.exit;

/**
 * Created by lyh on 2017/4/8.
 */

public class CustomItemSettingActivity extends AppCompatActivity {
    @BindView(exit)
    View mExit;
    @BindView(R.id.item_pop_icon)
    ImageView mItemPopIcon;
    @BindView(R.id.item_pop_appname)
    TextView mItemPopAppname;
    @BindView(R.id.item_pop_packagename)
    TextView mItemPopPackagename;
    @BindView(R.id.item_custom_pattern_switch)
    Switch mItemCustomPatternSwitch;
    @BindView(R.id.item_custom_pattern)
    LinearLayout mItemCustomPattern;
    @BindView(R.id.tv_light_title)
    TextView mLightTitle;
    @BindView(R.id.tv_light_value)
    TextView mLightValue;
    @BindView(R.id.alpha_seekBar)
    SeekBar mAlphaSeekBar;
    @BindView(R.id.item_custom_light_switch)
    Switch mItemCustomLightSwitch;
    @BindView(R.id.item_custom_light)
    LinearLayout mItemCustomLight;
    @BindView(R.id.tv_color_value)
    TextView mColorValue;
    @BindView(R.id.item_custom_color_switch)
    Switch mItemCustomColorSwitch;
    @BindView(R.id.item_custom_color)
    LinearLayout mItemCustomColor;
    @BindView(R.id.red_seekBar)
    SeekBar mRedSeekBar;
    @BindView(R.id.green_seedBar)
    SeekBar mGreenSeedBar;
    @BindView(R.id.bule_seedBar)
    SeekBar mBuleSeedBar;
    @BindView(R.id.btn_no_color)
    Button mBtnNoColor;
    @BindView(R.id.btn_filter_blue)
    Button mBtnFilterBlue;
    @BindView(R.id.btn_cool_colors)
    Button mBtnCoolColors;
    @BindView(R.id.btn_warm_tone)
    Button mBtnWarmTone;
    @BindView(R.id.btn_eye_green)
    Button mBtnEyeGreen;
    @BindView(R.id.btn_ink_blue)
    Button mBtnInkBlue;
    @BindView(R.id.btn_tea_black)
    Button mBtnTeaBlack;
    @BindView(R.id.btn_strawberry_powder)
    Button mBtnStrawberryPowder;
    @BindView(R.id.scrollView)
    HorizontalScrollView mScrollView;


    private PackageManager mPackageManager;
    private AppInfo mAppInfo;

    private int alpha;
    private int red;
    private int green;
    private int blue;
    private ContentValues mValues;
    private CustomPopWindow mPopWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.item_custom_setting);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initData() {
        mValues = new ContentValues();
        mPackageManager = getPackageManager();
        mAppInfo = getIntent().getParcelableExtra("appInfo");
        alpha = mAppInfo.getAlpha();
        red = mAppInfo.getRed();
        green = mAppInfo.getGreen();
        blue = mAppInfo.getBule();
    }

    private void initView() {
        mItemPopAppname.setText(mAppInfo.getAppName());
        mItemPopPackagename.setText(mAppInfo.getPackageName());
        mItemPopIcon.setImageDrawable(mPackageManager.getApplicationIcon(mAppInfo.getApplicationInfo()));
        mItemCustomPatternSwitch.setChecked(mAppInfo.isCustomPattern());
        mItemCustomLightSwitch.setChecked(mAppInfo.isCustomLight());
        mItemCustomColorSwitch.setChecked(mAppInfo.isCustomColor());
        mAlphaSeekBar.setProgress(alpha);
        mLightValue.setText("" + alpha * 100 / 255 + "%");
        mRedSeekBar.setProgress(red);
        mGreenSeedBar.setProgress(green);
        mBuleSeedBar.setProgress(blue);
        mColorValue.setText(ColorManager.toHexEncoding(red, green, blue));
        mAlphaSeekBar.setOnSeekBarChangeListener(alphaChange);
        mRedSeekBar.setOnSeekBarChangeListener(redChange);
        mGreenSeedBar.setOnSeekBarChangeListener(greenChange);
        mBuleSeedBar.setOnSeekBarChangeListener(buleChange);

        mItemCustomLightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mValues.put("isCustomLight", false);
                    mAppInfo.setCustomLight(false);
                } else {
                    mValues.put("isCustomLight", true);
                    mAppInfo.setCustomLight(true);
                }
            }
        });
    }

    private SeekBar.OnSeekBarChangeListener alphaChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mValues.put(Constants.ALPHA, progress);
            alpha = progress;
            mLightValue.setText("" + alpha * 100 / 255 + "%");

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private SeekBar.OnSeekBarChangeListener redChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mValues.put(Constants.RED, progress);
            red = progress;
            mColorValue.setText(ColorManager.toHexEncoding(red, green, blue));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private SeekBar.OnSeekBarChangeListener greenChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mValues.put(Constants.GREEN, progress);
            green = progress;
            mColorValue.setText(ColorManager.toHexEncoding(red, green, blue));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private SeekBar.OnSeekBarChangeListener buleChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mValues.put(Constants.BLUE, progress);
            blue = progress;
            mColorValue.setText(ColorManager.toHexEncoding(red, green, blue));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    @OnClick({R.id.btn_no_color, R.id.btn_filter_blue, R.id.btn_cool_colors, R.id.btn_warm_tone,
            R.id.btn_eye_green, R.id.btn_ink_blue, R.id.btn_tea_black, R.id.btn_strawberry_powder,
            R.id.exit, R.id.item_custom_pattern, R.id.item_custom_color})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_no_color:
                red = 0;
                green = 0;
                blue = 0;
                mRedSeekBar.setProgress(0);
                mGreenSeedBar.setProgress(0);
                mBuleSeedBar.setProgress(0);
                break;
            case R.id.btn_filter_blue:
                showPopTop(mBtnFilterBlue);
                break;
            case R.id.btn_cool_colors:
                showPopTop(mBtnCoolColors);
                break;
            case R.id.btn_warm_tone:
                showPopTop(mBtnWarmTone);
                break;
            case R.id.btn_eye_green:
                showPopTop(mBtnEyeGreen);
                break;
            case R.id.btn_ink_blue:
                showPopTop(mBtnInkBlue);
                break;
            case R.id.btn_tea_black:
                showPopTop(mBtnTeaBlack);
                break;
            case R.id.btn_strawberry_powder:
                showPopTop(mBtnStrawberryPowder);
                break;
            case R.id.exit:
                Log.d("1111", "exit: intent");
                Intent intent = new Intent();
                intent.putExtra("appInfo",mAppInfo);
                setResult(2,intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;
            case R.id.item_custom_pattern:
                if (mItemCustomPatternSwitch.isChecked()) {
                    mItemCustomPatternSwitch.setChecked(false);
                    mValues.put("isCustomPattern", false);
                    mAppInfo.setCustomPattern(false);
                } else {
                    mItemCustomPatternSwitch.setChecked(true);
                    mValues.put("isCustomPattern", true);
                    mAppInfo.setCustomPattern(true);
                }
                break;
            case R.id.item_custom_color:
                if (mItemCustomColorSwitch.isChecked()) {
                    mItemCustomColorSwitch.setChecked(false);
                    mValues.put("isCustomColor", false);
                    mAppInfo.setCustomColor(false);
                } else {
                    mItemCustomColorSwitch.setChecked(true);
                    mValues.put("isCustomColor", true);
                    mAppInfo.setCustomColor(true);
                }
                break;

        }
    }

    private void showPopTop(final Button button) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_color, null);
        handleLogic(contentView, button.getId());
        mPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .create();
        mPopWindow.showAsDropDown(button, 0, -(button.getHeight() / 4 + mPopWindow.getHeight()));
    }

    private void handleLogic(View contentView, final int id) {
        Button.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopWindow != null) {
                    mPopWindow.dissmiss();
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
        mRedSeekBar.setProgress(red);
        mGreenSeedBar.setProgress(green);
        mBuleSeedBar.setProgress(blue);
        mColorValue.setText(ColorManager.toHexEncoding(red, green, blue));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataSupport.updateAll(AppInfo.class, mValues, "packageName = ?", mAppInfo.getPackageName());
//        Intent intent = new Intent();
//        intent.putExtra("appInfo",mAppInfo);
//        setResult(2,intent);
//        Log.d("1111", "onDestroy: intent");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("appInfo","111123234");
        setResult(2,intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        super.onBackPressed();
    }


}
