package com.lyh.eyescare.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lyh.eyescare.R;
import com.lyh.eyescare.constant.Constants;
import com.lyh.eyescare.manager.ColorManager;
import com.lyh.eyescare.utils.SpUtil;
import com.lyh.eyescare.view.CustomPopWindow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lyh on 2017/3/25 0025.
 */

public class ColorSettingActivity extends AppCompatActivity {
    @BindView(R.id.tv_color_config)
    TextView tvColorConfig;
    @BindView(R.id.tv_light)
    TextView tvLight;
    @BindView(R.id.alpha_seekBar)
    SeekBar alphaSeekBar;
    @BindView(R.id.red_seekBar)
    SeekBar redSeekBar;
    @BindView(R.id.green_seedBar)
    SeekBar greenSeedBar;
    @BindView(R.id.bule_seedBar)
    SeekBar buleSeedBar;
    @BindView(R.id.btn_no_color)
    Button btnNoColor;
    @BindView(R.id.btn_filter_blue)
    Button btnFilterBlue;
    @BindView(R.id.btn_cool_colors)
    Button btnCoolColors;
    @BindView(R.id.btn_warm_tone)
    Button btnWarmTone;
    @BindView(R.id.btn_eye_green)
    Button btnEyeGreen;
    @BindView(R.id.btn_ink_blue)
    Button btnInkBlue;
    @BindView(R.id.btn_tea_black)
    Button btnTeaBlack;
    @BindView(R.id.btn_strawberry_powder)
    Button btnStrawberryPowder;
    @BindView(R.id.scrollView)
    HorizontalScrollView scrollView;
    @BindView(R.id.exit)
    View exit;


    private int alpha;
    private int red;
    private int blue;
    private int green;
    private String tvColor;
    private boolean eyeProtectionOpen;
    private CustomPopWindow popWindow;
    private SpUtil mSpUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.color_popupwindow);
        ButterKnife.bind(this);
        initData();
        initView();
    }


    private void initData() {
        mSpUtil = SpUtil.getInstance();
        eyeProtectionOpen = mSpUtil.getBoolean(Constants.EYESHIELD, false);
        alpha = mSpUtil.getInt(Constants.ALPHA, 0);
        red = mSpUtil.getInt(Constants.RED, 0);
        green = mSpUtil.getInt(Constants.GREEN, 0);
        blue = mSpUtil.getInt(Constants.BLUE, 0);
        tvColor = mSpUtil.getString(Constants.COLORVALUE, Constants.DEFAULTVALUE);
        if (eyeProtectionOpen) {
            ColorManager.changeColor(Color.argb(alpha, red, green, blue));
        }
    }

    private void initView() {
        alphaSeekBar.setProgress(alpha);
        tvLight.setText("" + alpha * 100 / 255 + "%");
        redSeekBar.setProgress(red);
        greenSeedBar.setProgress(green);
        buleSeedBar.setProgress(blue);
        tvColorConfig.setText(ColorManager.toHexEncoding(red, green, blue));
        alphaSeekBar.setOnSeekBarChangeListener(alphaChange);
        redSeekBar.setOnSeekBarChangeListener(redChange);
        greenSeedBar.setOnSeekBarChangeListener(greenChange);
        buleSeedBar.setOnSeekBarChangeListener(buleChange);

    }

    private SeekBar.OnSeekBarChangeListener alphaChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            alpha = progress;
            tvLight.setText("" + alpha * 100 / 255 + "%");
            mSpUtil.putInt(Constants.ALPHA, alpha);
            if (eyeProtectionOpen) {
                ColorManager.changeColor(Color.argb(alpha, red, green, blue));
            }
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
            red = progress;
            tvColorConfig.setText(ColorManager.toHexEncoding(red, green, blue));
            mSpUtil.putInt(Constants.RED, red);
            mSpUtil.putString(Constants.COLORVALUE, ColorManager.toHexEncoding(red, green, blue));
            if (eyeProtectionOpen) {
                ColorManager.changeColor(Color.argb(alpha, red, green, blue));
            }

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
            green = progress;
            tvColorConfig.setText(ColorManager.toHexEncoding(red, green, blue));
            mSpUtil.putInt(Constants.GREEN, green);
            mSpUtil.putString(Constants.COLORVALUE, ColorManager.toHexEncoding(red, green, blue));
            if (eyeProtectionOpen) {
                ColorManager.changeColor(Color.argb(alpha, red, green, blue));
            }
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
            blue = progress;
            tvColorConfig.setText(ColorManager.toHexEncoding(red, green, blue));
            mSpUtil.putInt(Constants.BLUE, blue);
            mSpUtil.putString(Constants.COLORVALUE, ColorManager.toHexEncoding(red, green, blue));
            if (eyeProtectionOpen) {
                ColorManager.changeColor(Color.argb(alpha, red, green, blue));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    @OnClick({R.id.btn_no_color, R.id.btn_filter_blue, R.id.btn_cool_colors, R.id.btn_warm_tone, R.id.btn_eye_green, R.id.btn_ink_blue, R.id.btn_tea_black, R.id.btn_strawberry_powder})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
            default:
                break;
        }
        redSeekBar.setProgress(red);
        greenSeedBar.setProgress(green);
        buleSeedBar.setProgress(blue);
    }

    private void showPopTop(final Button button) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_color, null);
        handleLogic(contentView, button.getId());
        popWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .create();
        popWindow.showAsDropDown(button, -(popWindow.getWidth() - button.getWidth()) / 2, -(button.getHeight() / 3 * 2 + popWindow.getHeight()));
    }

    private void handleLogic(View contentView, final int id) {
        Button.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popWindow != null) {
                    popWindow.dissmiss();
                }
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
                    default:
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
        tvColorConfig.setText(ColorManager.toHexEncoding(red, green, blue));
        mSpUtil.putString(Constants.COLORVALUE, ColorManager.toHexEncoding(red, green, blue));
        if (eyeProtectionOpen) {
            ColorManager.changeColor(Color.argb(alpha, red, green, blue));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpUtil.putInt(Constants.ALPHA, alpha);
        mSpUtil.putInt(Constants.RED, red);
        mSpUtil.putInt(Constants.GREEN, green);
        mSpUtil.putInt(Constants.BLUE, blue);
        mSpUtil.putString(Constants.COLORVALUE, ColorManager.toHexEncoding(red, green, blue));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        mSpUtil.putInt(Constants.ALPHA, alpha);
        mSpUtil.putInt(Constants.RED, red);
        mSpUtil.putInt(Constants.GREEN, green);
        mSpUtil.putInt(Constants.BLUE, blue);
        mSpUtil.putString(Constants.COLORVALUE, ColorManager.toHexEncoding(red, green, blue));
    }
}
