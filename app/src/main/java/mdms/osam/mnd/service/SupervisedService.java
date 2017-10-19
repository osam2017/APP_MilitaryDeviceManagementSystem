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

    public static BroadcastReceiver restrictedFunctionReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d("SupervisedService", "Restricted Function Detected");

            String action = intent.getAction();
            Log.d("action Occured", action);
            Intent startIntent = new Intent(context, WriteReasonActivity.class);
            switch (action){
                case Intent.ACTION_CAMERA_BUTTON:
                case "com.android.camera.NEW_PICTURE":
                    startIntent.putExtra("usedFunction","camera");
                    context.startActivity(startIntent);
                    break;
                case LOCATION_SERVICE:
                    startIntent.putExtra("usedFunction","gps");
                    context.startActivity(startIntent);
                    break;
                case WIFI_SERVICE:
                    startIntent.putExtra("usedFunction","wifi");
                    context.startActivity(startIntent);
                    break;
                case BLUETOOTH_SERVICE:
                    startIntent.putExtra("usedFunction","bluetooth");
                    context.startActivity(startIntent);
                    break;

            }

        }
    };
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        Toast.makeText(this, "Wifi, GPS, 카메라 기능이 제한됩니다.",Toast.LENGTH_LONG).show();
        IntentFilter intentfilter = new IntentFilter();
        //receive 할 action들을 지정

        intentfilter.addAction(Intent.ACTION_CAMERA_BUTTON);
        intentfilter.addAction("com.android.camera.NEW_PICTURE");
        intentfilter.addAction(LOCATION_SERVICE);
        intentfilter.addAction(WIFI_SERVICE);
        intentfilter.addAction(BLUETOOTH_SERVICE);
        registerReceiver(restrictedFunctionReceiver, intentfilter);

        //test receiver registered - registered!
        /*try{
            unregisterReceiver(restrictedFunctionReceiver);
            Log.e("checkReceiverRegistered","registered");
        }catch (IllegalArgumentException e){
            Log.e("checkReceiverRegistered","unregistered");
        }*/

    }

    @Override
    public void onDestroy()
    {
        Toast.makeText(this, "Wifi, GPS, 카메라 기능 제한이 해제됩니다.",Toast.LENGTH_LONG).show();
        //register 이후 sendBroadcast()를 한번이라도 호출하면 receiver가 사라지는 이슈 발생
        //try catch로 예외처리
        try{
            unregisterReceiver(restrictedFunctionReceiver);
            Log.e("unregisterReceiver","success");
        }catch (IllegalArgumentException e){
            Log.e("unregisterReceiver","failed");
        }
    }


}
