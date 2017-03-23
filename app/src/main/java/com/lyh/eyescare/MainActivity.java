package com.lyh.eyescare;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.lyh.eyescare.constant.Status;
import com.lyh.eyescare.service.BackService;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lyh.eyescare.R.id.btn_light;
import static com.lyh.eyescare.R.id.btn_switch;
import static com.lyh.eyescare.R.id.btn_time;

public class MainActivity extends AppCompatActivity {

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
    @BindView(btn_switch)
    Button btnSwitch;
    @BindView(btn_light)
    Button btnLight;
    @BindView(btn_time)
    Button btnTime;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.tvcolor)
    TextView tvcolor;
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
    @BindView(R.id.scrollView1)
    LinearLayout scrollView1;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        alpha = settings.getInt("alpha", 0);
        red = settings.getInt("red", 0);
        green = settings.getInt("green", 0);
        blue = settings.getInt("blue", 0);
        time = settings.getInt("time", 0);
        init();
    }

    private void init() {
        tvpercent.setText("" + alpha * 100 / 255 + "%");
        tvr.setText("" + red * 100 / 255 + "%");
        tvg.setText("" + green * 100 / 255 + "%");
        tvb.setText("" + blue * 100 / 255 + "%");
        if (time != 0) {
            String minuteString = "" + time % 60;
            if (minuteString.length() < 2) {
                minuteString = "0" + minuteString;
                btnTime.setText("关闭时间" + time / 60 + ":" + minuteString);
            }
        }

        spercent.setProgress(alpha);
        spercent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alpha = progress;
                tvpercent.setText("" + progress * 100 / 255 + "%");
                tvcolor.setBackgroundColor(Color.argb(alpha, red, green, blue));
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
                tvcolor.setTextColor(Color.argb(alpha, red, green, blue));
                tvcolor.setBackgroundColor(Color.argb(alpha, red, green, blue));
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
                tvcolor.setTextColor(Color.rgb(255 - red, 255 - green, 255 - blue));
                tvcolor.setBackgroundColor(Color.argb(alpha, red, green, blue));
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
                tvcolor.setTextColor(Color.rgb(255 - red, 255 - green, 255 - blue));
                tvcolor.setBackgroundColor(Color.argb(alpha, red, green, blue));
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

        tvcolor.setBackgroundColor(Color.argb(alpha, red, green, blue));

    }


    @OnClick({R.id.spercent, btn_switch, btn_light, btn_time, R.id.btn_save, R.id.sr, R.id.sg, R.id.sb})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.spercent:
                break;
            case btn_switch:
                if (btnSwitch.getText().equals("开启护眼模式")) {
                    Intent intent = new Intent(MainActivity.this, BackService.class);
                    intent.putExtra("color", Color.argb(alpha, red, green, blue));
                    startService(intent);
                    intent.putExtra("status", 1);
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +
                            1000 * 60 * time, pendingIntent);
                    bindService(intent, conn, Context.BIND_AUTO_CREATE);
                    btnSwitch.setText("关闭护眼模式");
                } else {
                    Intent intent = new Intent(MainActivity.this, BackService.class);
                    PendingIntent pendIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    am.cancel(pendIntent);
                    unbindService(conn);
                    stopService(intent);
                    curservice = null;
                    btnSwitch.setText("开启护眼模式");
                }
                break;
            case btn_light:
                if (btnLight.getText().equals("开启手电筒")) {
                    camera = Camera.open();
                    camera.startPreview();
                    Camera.Parameters parameter = camera.getParameters();
                    parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameter);
                    camera.startPreview();
                    btnLight.setText("关闭手电筒");
                } else {
                    Camera.Parameters parameter = camera.getParameters();
                    parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameter);
                    camera.stopPreview();
                    camera.release();
                    btnLight.setText("开启手电筒");
                }
                break;
            case btn_time:
                final Calendar c = Calendar.getInstance();
                TimePickerDialog dlg = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                time = (hourOfDay - c.get(Calendar.HOUR_OF_DAY)) * 60 + minute
                                        - c.get(Calendar.MINUTE);
                                String minuteString = "" + minute;
                                if (minuteString.length() < 2)
                                    minuteString = "0" + minuteString;
                                btnTime.setText("关闭时间 " + hourOfDay + ":"
                                        + minuteString);
                                if (curservice != null) {
                                    Intent intent = new Intent(MainActivity.this, BackService.class);
                                    intent.putExtra("status", 1);
                                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                    PendingIntent pendIntent = PendingIntent.getService(
                                            MainActivity.this, 0, intent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()
                                            + 1000 * 60 * time, pendIntent);
                                }
                            }
                        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                        true);
                dlg.show();
                break;
            case R.id.btn_save:
                SharedPreferences settings = getSharedPreferences("setting",
                        MODE_PRIVATE);
                SharedPreferences.Editor edit = settings.edit();
                edit.putInt("alpha", alpha);
                edit.putInt("red", red);
                edit.putInt("green", green);
                edit.putInt("blue", blue);
                edit.putInt("time", time);
                edit.commit();
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int status = intent.getIntExtra("status", 0);
        if (status == Status.STOP) {
            if (curservice != null) {
                unbindService(conn);
            }
            Intent intentv = new Intent(MainActivity.this, BackService.class);
            stopService(intentv);
            curservice = null;
            btnTime.setText("时间到！");
            btnSwitch.setText("开启护眼模式");
        }
    }
}
