package com.lyh.eyescare.activity;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.lyh.eyescare.R;
import com.lyh.eyescare.constant.Status;
import com.lyh.eyescare.service.BackService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lyh on 2017/3/24.
 */

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.color_config_title)
    TextView colorConfigTitle;
    @BindView(R.id.color_config)
    TextView colorConfig;
    @BindView(R.id.color_setting)
    LinearLayout colorSetting;
    private int red;
    private int alpha;
    private int blue;
    private int green;
    private int time;
    private BackService curservice;
    private Camera camera;
    private PopupWindow popupWindow;

    @BindView(R.id.tvpercent)
    TextView tvpercent;
    @BindView(R.id.spercent)
    SeekBar spercent;
    @BindView(R.id.eyes_title)
    TextView eyesTitle;
    @BindView(R.id.eyes_switch)
    Switch eyesSwitch;
    @BindView(R.id.light_title)
    TextView lightTitle;
    @BindView(R.id.light_switch)
    Switch lightSwitch;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private boolean eyeProtectionOpen ;

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
        setContentView(R.layout.activity_main_new);
        ButterKnife.bind(this);
        initData();
        initView();
        statusSettings();
    }

    private void initData() {
        settings = getSharedPreferences("settings", MODE_PRIVATE);
        alpha = settings.getInt("alpha", 0);
        red = settings.getInt("red", 0);
        green = settings.getInt("green", 0);
        blue = settings.getInt("blue", 0);
        time = settings.getInt("time", 0);
        eyeProtectionOpen = settings.getBoolean("eyeProtectionOpen",false);
        editor = settings.edit();
    }

    private void initView() {
        tvpercent.setText("" + alpha * 100 / 255 + "%");
        spercent.setProgress(alpha);
        spercent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alpha = progress;
                tvpercent.setText("" + progress * 100 / 255 + "%");
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

        });
    }

    private void statusSettings() {

        eyesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(SettingsActivity.this, BackService.class);
                    intent.putExtra("color", Color.argb(alpha, red, green, blue));
                    startService(intent);
                    intent.putExtra("status", 1);
                    PendingIntent pendingIntent = PendingIntent.getService(SettingsActivity.this,
                            0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    bindService(intent, conn, Context.BIND_AUTO_CREATE);
                    editor.putBoolean("eyeProtectionOpen",true);
                } else {
                    Intent intent = new Intent(SettingsActivity.this, BackService.class);
                    PendingIntent pendIntent = PendingIntent.getBroadcast(SettingsActivity.this,
                            0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (curservice != null) {
                        unbindService(conn);
                        stopService(intent);
                        curservice = null;
                    }
                    editor.putBoolean("eyeProtectionOpen",false);
                }
                editor.commit();
            }
        });

        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    camera = Camera.open();
                    camera.startPreview();
                    Camera.Parameters parameter = camera.getParameters();
                    parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameter);
                    camera.startPreview();
                } else {
                    Camera.Parameters parameter = camera.getParameters();
                    parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameter);
                    camera.stopPreview();
                    camera.release();
                }
            }
        });

        colorSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // getPopupWindow();
                Intent intent = new Intent(SettingsActivity.this,ColorSettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                //finish();
            }
        });
    }

    /***
     * 获取PopupWindow实例
     */
    private void getPopupWindow() {

        if (null != popupWindow) {
            closePopupWindow();
            return;
        } else {
            initPopuptWindow();
        }
    }

    /**
     * 关闭窗口
     */
    private void closePopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
            //WindowManager.LayoutParams params = getWindow().getAttributes();
            //params.alpha = 1f;
            //getWindow().setAttributes(params);
        }
    }

    private void initPopuptWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.color_popupwindow, null);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        //popupWindow.setAnimationStyle(R.style.colorPopupWindow);
        //ColorDrawable dw = new ColorDrawable(0xb0000000);
        //popupWindow.setBackgroundDrawable(dw);
        //WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //layoutParams.alpha = 0.95f; //0.0-1.0
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//让窗口背景后面的任何东西变暗
        //getWindow().setAttributes(layoutParams);
        popupWindow.showAtLocation(colorSetting, Gravity.BOTTOM, 0, 0);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closePopupWindow();
                return false;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int status = intent.getIntExtra("status", 0);
        if (status == Status.STOP) {
            if (curservice != null) {
                unbindService(conn);
            }
            Intent intentv = new Intent(SettingsActivity.this, BackService.class);
            stopService(intentv);
            curservice = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.close:
                if (curservice != null) {
                    unbindService(conn);
                }
                Intent intent = new Intent(SettingsActivity.this, BackService.class);
                stopService(intent);
                curservice = null;
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
