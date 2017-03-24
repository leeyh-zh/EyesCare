package com.lyh.eyescare.activity;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.lyh.eyescare.R;
import com.lyh.eyescare.constant.Status;
import com.lyh.eyescare.service.BackService;
import com.lyh.eyescare.view.ColorPopupWindow;

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
    @BindView(R.id.timer_title)
    TextView timerTitle;
    @BindView(R.id.timer_switch)
    Switch timerSwitch;
    @BindView(R.id.save_config_title)
    TextView saveConfigTitle;
    @BindView(R.id.save_config_switch)
    Switch saveConfigSwitch;
    @BindView(R.id.tvr)
    TextView tvr;
    @BindView(R.id.sr)
    SeekBar sr;
    @BindView(R.id.tvg)
    TextView tvg;
    @BindView(R.id.sg)
    SeekBar sg;
    @BindView(R.id.tvb)
    TextView tvb;
    @BindView(R.id.sb)
    SeekBar sb;
    private SharedPreferences settings;

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
    }

    private void initView() {
        tvpercent.setText("" + alpha * 100 / 255 + "%");
        tvr.setText("" + red * 100 / 255 + "%");
        tvg.setText("" + green * 100 / 255 + "%");
        tvb.setText("" + blue * 100 / 255 + "%");

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

        sr.setProgress(red);
        sr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                red = progress;
                tvr.setText("" + progress * 100 / 255 + "%");
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

        sg.setProgress(green);
        sg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                green = progress;
                tvg.setText("" + progress * 100 / 255 + "%");
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

        sb.setProgress(blue);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                blue = progress;
                tvb.setText("" + progress * 100 / 255 + "%");
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
                } else {
                    Intent intent = new Intent(SettingsActivity.this, BackService.class);
                    PendingIntent pendIntent = PendingIntent.getBroadcast(SettingsActivity.this,
                            0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (curservice != null) {
                        unbindService(conn);
                        stopService(intent);
                        curservice = null;
                    }
                }
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

//                View popupView = SettingsActivity.this.getLayoutInflater().inflate(
//                        R.layout.color_popupwindow, null);
//                PopupWindow window = new PopupWindow(popupView,400,600);
//                window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
//                // TODO: 2016/5/17 设置可以获取焦点
//                window.setFocusable(true);
//                // TODO: 2016/5/17 设置可以触摸弹出框以外的区域
//                window.setOutsideTouchable(true);
//                // TODO：更新popupwindow的状态
//                window.update();
//                // TODO: 2016/5/17 以下拉的方式显示，并且可以设置显示的位置
//                window.showAsDropDown(colorSetting, 0, 20);
//                window.show
                ColorPopupWindow popupwindow = new ColorPopupWindow(SettingsActivity.this);
                popupwindow.showAtLocation(colorSetting, Gravity.BOTTOM, 0, 0);
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
