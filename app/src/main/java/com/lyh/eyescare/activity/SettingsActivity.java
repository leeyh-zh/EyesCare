package com.lyh.eyescare.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lyh.eyescare.manager.ColorManager;
import com.lyh.eyescare.R;
import com.lyh.eyescare.constant.Constants;
import com.lyh.eyescare.service.EyesCareService;
import com.lyh.eyescare.service.LoadAppListService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lyh.eyescare.constant.Constants.DEFAULTVALUE;

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
    @BindView(R.id.brightness)
    TextView brightness;
    @BindView(R.id.spercent)
    SeekBar alphaSeekBar;
    @BindView(R.id.eyes_title)
    TextView eyesTitle;
    @BindView(R.id.light_title)
    TextView lightTitle;
    @BindView(R.id.exit)
    View exit;
    @BindView(R.id.custom_setting_title)
    TextView mCustomSettingTitle;
    @BindView(R.id.switch_custom_setting)
    Switch mSwitchCustomSetting;
    @BindView(R.id.switch_custom_setting_root)
    LinearLayout mSwitchCustomSettingRoot;
    @BindView(R.id.app_color_setting)
    LinearLayout mAppColorSetting;

    private int red;
    private int alpha;
    private int blue;
    private int green;
    private String colorValue;
    private Camera camera;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private boolean eyeshield;
    private boolean accessibility;

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
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
            }
        }
        startService(new Intent(this, LoadAppListService.class));
        settings = getSharedPreferences(Constants.SETTINGS, MODE_PRIVATE);
        editor = settings.edit();
        initStatus();
        statusSettings();
    }

    private void initStatus() {
        alpha = settings.getInt(Constants.ALPHA, 0);
        red = settings.getInt(Constants.RED, 54);
        green = settings.getInt(Constants.GREEN, 36);
        blue = settings.getInt(Constants.BLUE, 0);
        colorValue = settings.getString(Constants.COLORVALUE, DEFAULTVALUE);
        eyeshield = settings.getBoolean(Constants.EYESHIELD, false);
        brightness.setText("" + alpha * 100 / 255 + "%");
        colorConfig.setText(colorValue);
        alphaSeekBar.setProgress(alpha);
    }

    private void statusSettings() {
        alphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alpha = progress;
                brightness.setText("" + progress * 100 / 255 + "%");
                if (eyeshield) {
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
//        switchEyesRoot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!switchEyes.isChecked()) {
//                    if (!isAccessibilitySettingsOn(SettingsActivity.this)) {
//                        Toast.makeText(SettingsActivity.this, "请先开启EyesCare辅助功能", Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//                    } else {
//                        startEyeshield();
//                        switchEyes.setChecked(true);
//                    }
//                } else {
//                    stopEyeshield();
//                    switchEyes.setChecked(false);
//                }
//            }
//        });

//        switchLightRoot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (!switchLight.isChecked()) {
//                    if (ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.CAMERA)
//                            != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
//                    } else {
//                        camera = Camera.open();
//                        camera.startPreview();
//                        Camera.Parameters parameter = camera.getParameters();
//                        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                        camera.setParameters(parameter);
//                        camera.startPreview();
//                        switchLight.setChecked(true);
//                    }
//                } else {
//                    Camera.Parameters parameter = camera.getParameters();
//                    parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                    camera.setParameters(parameter);
//                    camera.stopPreview();
//                    camera.release();
//                    switchLight.setChecked(false);
//                }
//            }
//        });


//        colorSetting.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SettingsActivity.this, ColorSettingActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//            }
//        });
    }

    private void startEyeshield() {
        Intent intent = new Intent(SettingsActivity.this, EyesCareService.class);
        intent.putExtra(Constants.STATUS, EyesCareService.TYPE_OPEN);
        intent.putExtra("color", Color.argb(alpha, red, green, blue));
        startService(intent);
        editor.putBoolean(Constants.EYESHIELD, true);
        editor.commit();
        ColorManager.changeColor(Color.argb(alpha, red, green, blue));
    }

    private void stopEyeshield() {
        Intent intent = new Intent(SettingsActivity.this, EyesCareService.class);
        intent.putExtra(Constants.STATUS, EyesCareService.TYPE_CLOSE);
        startService(intent);
        editor.putBoolean(Constants.EYESHIELD, false);
        editor.commit();
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
        initStatus();
        if (isAccessibilitySettingsOn(this) && eyeshield) {
            startEyeshield();
            switchEyes.setChecked(true);
        } else {
            switchEyes.setChecked(false);
        }
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initStatus();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.switch_eyes_root, R.id.switch_light_root, R.id.switch_custom_setting_root, R.id.color_setting, R.id.app_color_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.switch_eyes_root:
                if (!switchEyes.isChecked()) {
                    if (!isAccessibilitySettingsOn(SettingsActivity.this)) {
                        Toast.makeText(SettingsActivity.this, "请先开启EyesCare辅助功能", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    } else {
                        startEyeshield();
                        switchEyes.setChecked(true);
                    }
                } else {
                    stopEyeshield();
                    switchEyes.setChecked(false);
                }
                break;
            case R.id.switch_light_root:
                if (!switchLight.isChecked()) {
                    if (ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                    } else {
                        camera = Camera.open();
                        camera.startPreview();
                        Camera.Parameters parameter = camera.getParameters();
                        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(parameter);
                        camera.startPreview();
                        switchLight.setChecked(true);
                    }
                } else {
                    Camera.Parameters parameter = camera.getParameters();
                    parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameter);
                    camera.stopPreview();
                    camera.release();
                    switchLight.setChecked(false);
                }
                break;
            case R.id.switch_custom_setting_root:
                break;
            case R.id.color_setting:
                startActivity(new Intent(SettingsActivity.this, ColorSettingActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.app_color_setting:
                startActivity(new Intent(SettingsActivity.this, CustomSettingActivity.class));
                break;
        }
    }
}
