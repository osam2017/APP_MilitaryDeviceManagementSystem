package mdms.osam.mnd.mdms_client;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import mdms.osam.mnd.service.SupervisedService;

import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    TextView wifiStatus;
    TextView gpsStatus;
    TextView bluetoothStatus;

    TextView worktimeText;
    SharedPreferences mPref;
    private final String REG_PREF_KEY = "isRegistered";
    boolean isWorktime = false;

    Button startWorktime;
    Button finishWorktime;

    Button wifiButton;
    Button gpsButton;
    Button cameraButton;

    BroadcastReceiver mReceiver;
    IntentFilter intentfilter;

    WifiManager wifi;

    String gpsEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkUserRegister();

        setDefaultView();
        renderStatusView();

        if (isMyServiceRunning(SupervisedService.class)) {
            worktimeText.setText("일과중입니다.");
            worktimeText.setBackgroundColor(Color.parseColor("#00FF00"));
            isWorktime = true;
            registerSuperviseReceiver();
            disableFunctions();
        } else {
            worktimeText.setText("일과가 종료되었습니다.");
            worktimeText.setBackgroundColor(Color.parseColor("#e20300"));
            isWorktime = false;
            enableFunctions();
        }
        renderStatusView();

        //setWorktimeCondition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderStatusView();
        requestTurnGPSOff();
    }

    /**
     * 사용자가 등록되었는지 여부를 확인하고, 미등록된 경우
     */
    private void checkUserRegister() {
        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isRegisteredExist = mPref.contains(REG_PREF_KEY);

        if (isRegisteredExist) {
            if (!mPref.getBoolean(REG_PREF_KEY, false)) {
                Intent i = new Intent(this, RegisterActivity.class);
                startActivity(i);
            }
        } else {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }
    }

    /**
     * 일과중인지 여부를 확인하여 다음과 같은 동작을 수행한다.
     * 일과중인 경우 상단 상태창에 '일과중입니다.'라고 텍스트를 설정하고, wifi, gps, 카메라 동작을 감지하는 BroadcastReceiver를 등록한다.
     * 일과가 종료된 경우 상단 상태창에 '일과가 종료되었습니다.'라고 텍스트를 설정하고, wifi, gps, 카메라 동작을 감지하는 BroadcastReceiver를 해지한다.
     */
    private void setWorktimeCondition() {
        if (isWorktime) {
            worktimeText.setText("일과중입니다.");
            worktimeText.setBackgroundColor(Color.parseColor("#00FF00"));
            registerSuperviseReceiver();
            disableFunctions();
        } else {
            worktimeText.setText("일과가 종료되었습니다.");
            worktimeText.setBackgroundColor(Color.parseColor("#e20300"));
            unregisterSuperviseReceiver();
            enableFunctions();
        }
        renderStatusView();
    }

    private void disableFunctions() {
        //wifi 비활성화
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(false);

        //gps 켜져있으면 종료하도록 설정
        requestTurnGPSOff();

        //bluetooth 비활성화

    }

    private void requestTurnGPSOff(){
        //gps 켜져있으면 종료하도록 설정
        if (isGPSEnabled()) {
            //gps가 사용가능하면
            new AlertDialog.Builder(this).setTitle("GPS 설정").setMessage("GPS가 켜져 있습니다. \nGPS를 비활성화 해주십시오.").setPositiveButton("GPS 설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    //GPS 설정 화면을 띄움
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }).create().show();
    }
    }

    private void enableFunctions(){
        //에뮬레이터에서는 wifi 활성화 불가능
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
    }

    private boolean chkGpsService() {

        //GPS가 켜져 있는지 확인함.
        gpsEnabled = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!(gpsEnabled.matches(".*gps.*") && gpsEnabled.matches(".*network.*"))) {
            //gps가 사용가능한 상태가 아니면
            new AlertDialog.Builder(this).setTitle("GPS 설정").setMessage("GPS가 꺼져 있습니다. \nGPS를 활성화 하시겠습니까?").setPositiveButton("GPS 켜기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    //GPS 설정 화면을 띄움
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }).setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).create().show();

        }
        return false;
    }

    private  boolean isGPSEnabled(){
        boolean gpsEnable = false;
        LocationManager manager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsEnable = true;
        }
        return gpsEnable;

    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    private boolean isBluetoothEnabled(){

        boolean bluetoothEnable = false;

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            bluetoothEnable=false;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enable :)
                bluetoothEnable=true;
            }
        }
        return bluetoothEnable;
    }

    /**
     * wifi, gps, 카메라 동작을 감지하는 BroadcastReceiver를 등록한다.
     */
    private void registerSuperviseReceiver() {
        /*intentfilter = new IntentFilter();
        //receive 할 action들을 지정
        intentfilter.addAction(ACTION_STATE_CHANGED);
        intentfilter.addAction((Intent.ACTION_CAMERA_BUTTON));

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(), "리시버에 변동이 감지됨", Toast.LENGTH_SHORT).show();
                String action = intent.getAction();
                Log.d("action", action);
                Intent startIntent = new Intent(MainActivity.this, WriteReasonActivity.class);
                switch (action){
                    case Intent.ACTION_CAMERA_BUTTON:
                        startIntent.putExtra("action","camera");
                        startActivity(startIntent);

                }

            }
        };

        Toast.makeText(getApplicationContext(), "Wifi, GPS, 카메라 기능이 제한됩니다.", Toast.LENGTH_SHORT).show();
        registerReceiver(mReceiver, intentfilter);*/
        Intent lintent = new Intent(this, SupervisedService.class);
        startService(lintent);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * wifi, gps, 카메라 동작을 감지하는 BroadcastReceiver를 해지한다.
     */
    private void unregisterSuperviseReceiver() {
        /*if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            Toast.makeText(getApplicationContext(), "Wifi, GPS, 카메라 사용이 가능합니다.", Toast.LENGTH_SHORT).show();
            mReceiver = null;
        }*/
        Intent lintent = new Intent(this, SupervisedService.class);
        this.stopService(lintent);
    }

    /**
     * 액티비티 레이아웃 설정
     */
    private void setDefaultView() {
        setContentView(R.layout.activity_main);

        worktimeText = (TextView) findViewById(R.id.tv_isWorkTime);
        startWorktime = (Button) findViewById(R.id.bt_startWorktime);
        finishWorktime = (Button) findViewById(R.id.bt_finishWorktime);
        wifiButton = (Button) findViewById(R.id.bt_wifi);
        gpsButton = (Button) findViewById(R.id.bt_gps);
        cameraButton = (Button) findViewById(R.id.bt_camera);
        wifiStatus = (TextView)findViewById(R.id.tv_wifi);
        gpsStatus = (TextView)findViewById(R.id.tv_gps);
        bluetoothStatus = (TextView)findViewById(R.id.tv_bluetooth);

        startWorktime.setOnClickListener(this);
        finishWorktime.setOnClickListener(this);
    }

    private void renderStatusView(){

        wifiStatus.setText(isGPSEnabled()?"On":"Off");
        gpsStatus.setText(isLocationEnabled(MainActivity.this)?"On":"Off");
        bluetoothStatus.setText(isBluetoothEnabled()?"On":"Off");

    }

    /**
     * 일과시간 여부(08:30~17:30)를 검사한다.
     * 실제 어플리케이션 릴리즈 시 사용될 예정
     *
     * @return isWorktime
     */
    private boolean isNowInWorkTime() {

        DateFormat df = new SimpleDateFormat("HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        String[] hourmin = date.split(":");

        int minTime = 8 * 60 + 30;
        int maxTime = 17 * 60 + 30;

        int nowTime = Integer.valueOf(hourmin[0]) * 60 + Integer.valueOf(hourmin[1]);
        Log.i("nowTime", hourmin[0] + ":" + hourmin[1]);

        return nowTime <= maxTime && nowTime >= minTime;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_startWorktime:
                isWorktime = true;
                Toast.makeText(this, "일과가 시작되었습니다. ", Toast.LENGTH_SHORT).show();
                setWorktimeCondition();
                break;
            case R.id.bt_finishWorktime:
                isWorktime = false;
                Toast.makeText(this, "일과가 종료되었습니다.", Toast.LENGTH_SHORT).show();
                setWorktimeCondition();
                break;
            case R.id.bt_camera:
                Intent broadcastCameraIntent = new Intent(Intent.ACTION_CAMERA_BUTTON);
                sendBroadcast(broadcastCameraIntent);
                break;

        }
    }
}
