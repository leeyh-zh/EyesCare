package com.lyh.eyescare.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
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
import android.widget.Toast;

import com.lyh.eyescare.R;
import com.lyh.eyescare.service.BackService;
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

    private int alpha;
    private int red;
    private int blue;
    private int green;
    private BackService curservice;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private boolean eyeProtectionOpen;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackService.LocalService binder = (BackService.LocalService) service;
            curservice = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private CustomPopWindow customPopWindow;
    private CustomPopWindow popWindow;

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
        settings = getSharedPreferences("settings", MODE_PRIVATE);
        eyeProtectionOpen = settings.getBoolean("eyeProtectionOpen", false);
        editor = settings.edit();
        alpha = settings.getInt("alpha", 0);
        red = settings.getInt("red", 0);
        green = settings.getInt("green", 0);
        blue = settings.getInt("blue", 0);
        if (eyeProtectionOpen) {
            Intent intent = new Intent(this, BackService.class);
            intent.putExtra("color", Color.argb(alpha, red, green, blue));
            startService(intent);
            intent.putExtra("status", 1);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }
    }

    private void initView() {
        alphaSeekBar.setProgress(alpha);
        redSeekBar.setProgress(red);
        greenSeedBar.setProgress(green);
        buleSeedBar.setProgress(blue);
        alphaSeekBar.setOnSeekBarChangeListener(alphaChange);
        redSeekBar.setOnSeekBarChangeListener(redChange);
        greenSeedBar.setOnSeekBarChangeListener(greenChange);
        buleSeedBar.setOnSeekBarChangeListener(buleChange);

    }

    private SeekBar.OnSeekBarChangeListener alphaChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            alpha = progress;
            if (curservice != null) {
                curservice.changeColor(Color.argb(alpha, red, green, blue));
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
            if (curservice != null) {
                curservice.changeColor(Color.argb(alpha, red, green, blue));
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
            if (curservice != null) {
                curservice.changeColor(Color.argb(alpha, red, green, blue));
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
            if (curservice != null) {
                curservice.changeColor(Color.argb(alpha, red, green, blue));
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
        popWindow.showAsDropDown(button, 0, -(button.getHeight() / 4 + popWindow.getHeight()));
    }

    private void handleLogic(View contentView, final int id) {
        Button.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popWindow != null) {
                    popWindow.dissmiss();
                }
                String showContent = "";
                switch (v.getId()) {
                    case R.id.menu1:
                        showContent = "点击 Item菜单1";
                        break;
                    case R.id.menu2:
                        showContent = "点击 Item菜单2";
                        break;
                    case R.id.menu3:
                        showContent = "点击 Item菜单3";
                        break;
                    case R.id.menu4:
                        showContent = "点击 Item菜单4";
                        break;
                    case R.id.menu5:
                        showContent = "点击 Item菜单5";
                        break;
                }
                Toast.makeText(ColorSettingActivity.this, showContent + " = " + id, Toast.LENGTH_SHORT).show();
            }
        };
        contentView.findViewById(R.id.menu1).setOnClickListener(listener);
        contentView.findViewById(R.id.menu2).setOnClickListener(listener);
        contentView.findViewById(R.id.menu3).setOnClickListener(listener);
        contentView.findViewById(R.id.menu4).setOnClickListener(listener);
        contentView.findViewById(R.id.menu5).setOnClickListener(listener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(this,SettingsActivity.class);
//        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
