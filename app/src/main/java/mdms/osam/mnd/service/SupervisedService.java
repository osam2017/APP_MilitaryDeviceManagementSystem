package mdms.osam.mnd.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import mdms.osam.mnd.mdms_client.MainActivity;
import mdms.osam.mnd.mdms_client.WriteReasonActivity;

import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;

public class SupervisedService extends Service {
    public static BroadcastReceiver restrictedFunctionReceiver;

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        Toast.makeText(this, "Wifi, GPS, 카메라 기능이 제한됩니다.",Toast.LENGTH_LONG).show();
        registerScreenOffReceiver();

    }

    @Override
    public void onDestroy()
    {
        Toast.makeText(this, "Wifi, GPS, 카메라 기능 제한이 해제됩니다.",Toast.LENGTH_LONG).show();
        unregisterReceiver(restrictedFunctionReceiver);
        restrictedFunctionReceiver = null;
    }

    private void registerScreenOffReceiver()
    {
        restrictedFunctionReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.d("SupervisedService", "Restricted Function Detected");

                Toast.makeText(getApplicationContext(), "리시버에 변동이 감지됨", Toast.LENGTH_SHORT).show();
                String action = intent.getAction();
                Log.d("action Occured", action);
                Intent startIntent = new Intent(getApplicationContext(), WriteReasonActivity.class);
                switch (action){
                    case Intent.ACTION_CAMERA_BUTTON:
                    case "com.android.camera.NEW_PICTURE":
                        startIntent.putExtra("action","camera");
                        startActivity(startIntent);
                        break;

                }

            }
        };

        IntentFilter intentfilter = new IntentFilter();
        //receive 할 action들을 지정
        intentfilter.addAction(ACTION_STATE_CHANGED);
        intentfilter.addAction((Intent.ACTION_CAMERA_BUTTON));
        intentfilter.addAction("com.android.camera.NEW_PICTURE");

        registerReceiver(restrictedFunctionReceiver, intentfilter);
    }
}
