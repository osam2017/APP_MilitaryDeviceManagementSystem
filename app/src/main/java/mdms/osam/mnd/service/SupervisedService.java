package mdms.osam.mnd.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SupervisedService extends Service {
    private static BroadcastReceiver restrictedFunctionReceiver;

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
                // do something, e.g. send Intent to main app
                /*
                switch(intent.getAction()){

                }

                 */

                //Intent i = new Intent(context, WriteReasonActivity.class);
                //startActivity(i);
            }
        };


        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);


        registerReceiver(restrictedFunctionReceiver, filter);
    }
}
