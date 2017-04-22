package com.lyh.eyescare.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lyh.eyescare.CustomContract;
import com.lyh.eyescare.CustomPresenter;
import com.lyh.eyescare.R;
import com.lyh.eyescare.bean.AppInfo;
import com.lyh.eyescare.constant.Constants;
import com.lyh.eyescare.manager.AppInfoManager;
import com.lyh.eyescare.manager.ColorManager;
import com.lyh.eyescare.service.EyesCareService;
import com.lyh.eyescare.service.LoadAppListService;
import com.lyh.eyescare.utils.AppUtil;
import com.lyh.eyescare.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lyh.eyescare.constant.Constants.DEFAULTVALUE;

/**
 * Created by lyh on 2017/3/24.
 */

public class SettingsActivity extends AppCompatActivity implements CustomContract.View {

    private static final int RESULT_ACTION_USAGE_ACCESS_SETTINGS = 1;
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
    private static Camera mCamera;

    private boolean eyeshield;
    private boolean customEyeshield;

    private ContentValues mValues;
    private List<AppInfo> mAppInfos;
    private AppInfoManager mAppInfoManager;
    private CustomPresenter mCustomPresenter;
    private static SpUtil mSpUtil;
    //private ServiceBroadCast receiver;

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

        if (!AppUtil.isStatAccessPermissionSet(this)) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, RESULT_ACTION_USAGE_ACCESS_SETTINGS);
        }

        //receiver = new ServiceBroadCast();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("a");
//        filter.addAction("b");
//        filter.addAction("c");
//        filter.addAction("d");
//        registerReceiver(receiver, filter);

        init();
        initStatus();
        statusSettings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_ACTION_USAGE_ACCESS_SETTINGS) {
            if (AppUtil.isStatAccessPermissionSet(this)) {
            } else {
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init() {
        mSpUtil = SpUtil.getInstance();
        startService(new Intent(this, LoadAppListService.class));
        mAppInfoManager = new AppInfoManager(this);
        mCustomPresenter = new CustomPresenter(this, this);
        mCustomPresenter.loadAppInfo(this);
        mAppInfos = new ArrayList<>();
        mValues = new ContentValues();
    }

    private void initStatus() {
        alpha = mSpUtil.getInt(Constants.ALPHA, 0);
        red = mSpUtil.getInt(Constants.RED, 54);
        green = mSpUtil.getInt(Constants.GREEN, 36);
        blue = mSpUtil.getInt(Constants.BLUE, 0);
        colorValue = mSpUtil.getString(Constants.COLORVALUE, DEFAULTVALUE);
        eyeshield = mSpUtil.getBoolean(Constants.EYESHIELD, false);
        customEyeshield = mSpUtil.getBoolean(Constants.CUSTOM_EYESHIELD, false);
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
                mSpUtil.putInt(Constants.ALPHA, alpha);
                mValues.put(Constants.ALPHA, alpha);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void startEyeshield() {
        Intent intent = new Intent(SettingsActivity.this, EyesCareService.class);
        intent.putExtra(Constants.STATUS, EyesCareService.TYPE_OPEN);
        startService(intent);
        mSpUtil.putBoolean(Constants.EYESHIELD, true);
        mSpUtil.putInt(Constants.COLOR_ARGB, Color.argb(alpha, red, green, blue));
        ColorManager.changeColor(Color.argb(alpha, red, green, blue));
    }

    private void startCustomEyeshield() {
        Intent intent = new Intent(SettingsActivity.this, EyesCareService.class);
        intent.putExtra(Constants.STATUS, EyesCareService.TYPE_CUSTOM_OPEN);
        startService(intent);
        mSpUtil.putBoolean(Constants.CUSTOM_EYESHIELD, true);
    }

    private void stopCustomEyeshield() {
        Intent intent = new Intent(SettingsActivity.this, EyesCareService.class);
        intent.putExtra(Constants.STATUS, EyesCareService.TYPE_CUSTOM_CLOSE);
        startService(intent);
        mSpUtil.putBoolean(Constants.CUSTOM_EYESHIELD, false);
    }

    private void stopEyeshield() {
        Intent intent = new Intent(SettingsActivity.this, EyesCareService.class);
        intent.putExtra(Constants.STATUS, EyesCareService.TYPE_CLOSE);
        startService(intent);
        mSpUtil.putBoolean(Constants.EYESHIELD, false);
    }

    public static void openLight() {
        try {
            mCamera = Camera.open();
            mCamera.startPreview();
            Camera.Parameters parameter = mCamera.getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameter);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSpUtil.putBoolean(Constants.LIGHT, true);
    }

    public static void closeLight() {
        try {
            Camera.Parameters parameter = mCamera.getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameter);
            mCamera.stopPreview();
            mCamera.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSpUtil.putBoolean(Constants.LIGHT, false);
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
        if (customEyeshield && eyeshield) {
            mSwitchCustomSetting.setChecked(true);
            startCustomEyeshield();
        } else {
            mSwitchCustomSetting.setChecked(false);
            stopCustomEyeshield();
        }
        switchLight.setChecked(mSpUtil.getBoolean(Constants.LIGHT));
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
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(receiver);
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
                        openLight();
                        switchLight.setChecked(true);
                    }
                } else {
                    closeLight();
                    switchLight.setChecked(false);
                }
                break;
            case R.id.switch_custom_setting_root:
                if (!mSwitchCustomSetting.isChecked()) {
                    if (mSpUtil.getBoolean(Constants.EYESHIELD, false)) {
                        mSwitchCustomSetting.setChecked(true);
                        startCustomEyeshield();
                    } else {
                        mSwitchCustomSetting.setChecked(false);
                        Toast.makeText(SettingsActivity.this, "请先开启护眼模式", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mSwitchCustomSetting.setChecked(false);
                    mSpUtil.putBoolean(Constants.CUSTOM_EYESHIELD, false);
                    stopCustomEyeshield();
                }
                break;
            case R.id.color_setting:
                startActivity(new Intent(SettingsActivity.this, ColorSettingActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.app_color_setting:
                startActivity(new Intent(SettingsActivity.this, CustomSettingActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            default:
                break;
        }
    }

    @Override
    public void loadAppInfoSuccess(List<AppInfo> list) {
        mAppInfos = mAppInfoManager.getAllAppInfos();
    }

    @Override
    public void showProgressBar() {

    }

    public class ServiceBroadCast extends BroadcastReceiver {

        public ServiceBroadCast() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("a")) {
                Log.d("11111", "111111111");
            }
            if (intent.getAction().equals("b")) {
                Log.d("11111", "2222222222222");
            }
            if (intent.getAction().equals("c")) {
                Log.d("11111", "3333333333");
            }
            if (intent.getAction().equals("d")) {
                Log.d("11111", "4444444444444");
                if (mSpUtil.getBoolean(Constants.EYESHIELD)) {
                    stopEyeshield();
                } else {
                    startEyeshield();
                }

            }

        }

    }

}
