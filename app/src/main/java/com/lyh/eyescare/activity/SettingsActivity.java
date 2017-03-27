package com.lyh.eyescare.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lyh.eyescare.ColorManager;
import com.lyh.eyescare.R;
import com.lyh.eyescare.service.BackService;
import com.lyh.eyescare.service.EyesCareService;

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
    @BindView(R.id.switch_eyes)
    Switch switchEyes;
    @BindView(R.id.switch_eyes_root)
    LinearLayout switchEyesRoot;
    @BindView(R.id.switch_light)
    Switch switchLight;
    @BindView(R.id.switch_light_root)
    LinearLayout switchLightRoot;

    private int red;
    private int alpha;
    private int blue;
    private int green;
    private String tvColor;
    private int time;
    private BackService curservice;
    private Camera camera;
    private PopupWindow popupWindow;

    @BindView(R.id.tvpercent)
    TextView tvpercent;
    @BindView(R.id.spercent)
    SeekBar alphaSeekBar;
    @BindView(R.id.eyes_title)
    TextView eyesTitle;
    @BindView(R.id.light_title)
    TextView lightTitle;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private boolean eyeProtectionOpen = false;

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
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
            }
        }
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
        tvColor = settings.getString("tvColor","0x000000");
        eyeProtectionOpen = settings.getBoolean("eyeProtectionOpen", false);
        editor = settings.edit();
    }

    private void initView() {
        tvpercent.setText("" + alpha * 100 / 255 + "%");
        colorConfig.setText(tvColor);
        alphaSeekBar.setProgress(alpha);
        alphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alpha = progress;
                tvpercent.setText("" + progress * 100 / 255 + "%");
                if (curservice != null) {
                    curservice.changeColor(Color.argb(alpha, red, green, blue));
                }
                if (eyeProtectionOpen) {
                    ColorManager.changeColor(Color.argb(alpha, red, green, blue));
                }
                editor.putInt("alpha", alpha).commit();
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
        switchEyesRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!switchEyes.isChecked()) {
                    if (!isAccessibilitySettingsOn(SettingsActivity.this)) {
                        // 引导至辅助功能设置页面
                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                        Toast.makeText(SettingsActivity.this, "请先开启EyesCare辅助功能", Toast.LENGTH_SHORT).show();
                    } else {
                        //checkAccessibility();

//                    Intent intent = new Intent(SettingsActivity.this, BackService.class);
//                    intent.putExtra("color", Color.argb(alpha, red, green, blue));
//                    startService(intent);
//                    intent.putExtra("status", 1);
//                    PendingIntent pendingIntent = PendingIntent.getService(SettingsActivity.this,
//                            0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    bindService(intent, conn, Context.BIND_AUTO_CREATE);
                        Intent intent = new Intent(SettingsActivity.this, EyesCareService.class);
                        intent.putExtra("status", EyesCareService.TYPE_OPEN);
                        intent.putExtra("color", Color.argb(alpha, red, green, blue));
                        startService(intent);
                        editor.putBoolean("eyeProtectionOpen", true);
                        editor.commit();
                        ColorManager.changeColor(Color.argb(alpha, red, green, blue));
                        switchEyes.setChecked(true);
                    }
                } else {
//                    Intent intent = new Intent(SettingsActivity.this, BackService.class);
//                    PendingIntent pendIntent = PendingIntent.getBroadcast(SettingsActivity.this,
//                            0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    if (curservice != null) {
//                        unbindService(conn);
//                        stopService(intent);
//                        curservice = null;
//                    }
                    Intent intent = new Intent(SettingsActivity.this, EyesCareService.class);
                    intent.putExtra("status", EyesCareService.TYPE_CLOSE);
                    startService(intent);
                    editor.putBoolean("eyeProtectionOpen", false);
                    editor.commit();
                    switchEyes.setChecked(false);
                }
            }
        });

        switchLightRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLight.setChecked(!switchLight.isChecked());
                if (switchLight.isChecked()) {
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


        colorSetting.setOnClickListener(new View.OnClickListener()

        {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ColorSettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    private void checkAccessibility() {
        // 判断辅助功能是否开启
        if (!isAccessibilitySettingsOn(this)) {
            // 引导至辅助功能设置页面
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            Toast.makeText(this, "请先开启FloatBall辅助功能", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        alpha = settings.getInt("alpha", 0);
        red = settings.getInt("red", 0);
        green = settings.getInt("green", 0);
        blue = settings.getInt("blue", 0);
        tvColor = settings.getString("tvColor","0x000000");
        colorConfig.setText(tvColor);
        alphaSeekBar.setProgress(alpha);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        int status = intent.getIntExtra("status", 0);
//        if (status == Status.STOP) {
//            if (curservice != null) {
//                unbindService(conn);
//            }
//            Intent intentv = new Intent(SettingsActivity.this, BackService.class);
//            stopService(intentv);
//            curservice = null;
//        }
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
//                if (curservice != null) {
//                    unbindService(conn);
//                }
//                Intent intent = new Intent(SettingsActivity.this, BackService.class);
//                stopService(intent);
//                curservice = null;
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
