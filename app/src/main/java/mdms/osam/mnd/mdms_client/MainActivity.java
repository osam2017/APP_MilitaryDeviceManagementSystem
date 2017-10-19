package mdms.osam.mnd.mdms_client;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import mdms.osam.mnd.broadcastReceiver.AlarmBroadcastReceiver;
import mdms.osam.mnd.helper.HttpRequestHelper;
import mdms.osam.mnd.service.SupervisedService;

public class MainActivity extends AppCompatActivity {
    public class AlarmHelper {
        private Context context;
        int[] startTime, endTime;


        public AlarmHelper(Context context) {
            this.context=context;
            startTime = new int[]{8,30,0};
            endTime = new int[]{17,30,0};
        }
        public void startAlarm() {
            AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(MainActivity.this, AlarmBroadcastReceiver.class);

            PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            //알람시간 calendar에 set해주기

            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), startTime[0], startTime[1], startTime[2]);

            //알람 예약
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }

        /**
         * 일과 시작시간과 종료시간을 지정한다. (default : startTime = new int[]{8,30,0}; endTime = new int[]{17,30,0};)
         * @param startTime
         * @param endTime
         */
        public void setAlarmTime(int[] startTime, int[] endTime){

            if(startTime.length == 3 && endTime.length == 3){
                this.startTime = startTime;
                this.endTime = endTime;
            }

        }
    }

    TextView isWorkTime;
    SharedPreferences mPref;
    private final String REG_PREF_KEY = "isRegistered";

    AlarmHelper ah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ah = new AlarmHelper(this);
        ah.setAlarmTime(new int[]{13,31,0},new int[]{14,28,0});
        ah.startAlarm();



        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        //check isRegistered
        boolean isRegisteredExist = mPref.contains(REG_PREF_KEY);

        if(isRegisteredExist){
            if(!mPref.getBoolean(REG_PREF_KEY, false)){
                Intent i = new Intent(this, RegisterActivity.class);
                startActivity(i);
            }
        }else{
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }



        isWorkTime = (TextView)findViewById(R.id.tv_isWorkTime);
        if(isNowInWorkTime()){
            isWorkTime.setText("일과중입니다.");
            isWorkTime.setBackgroundColor(Color.parseColor("#00FF00"));
        }else{
            isWorkTime.setText("일과가 종료되었습니다.");
            isWorkTime.setBackgroundColor(Color.parseColor("#e20300"));
        }
    }

    /**
     * 일과시간 여부를 검사한다
     * @return
     */
    private boolean isNowInWorkTime(){

        DateFormat df = new SimpleDateFormat("HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        String[] hourmin = date.split(":");

        int minTime = 8 * 60 + 30;
        int maxTime = 17 * 60 + 30;

        int nowTime = Integer.valueOf(hourmin[0]) * 60 + Integer.valueOf(hourmin[1]);
        Log.i("nowTime",hourmin[0]+":"+hourmin[1]);

        return nowTime < maxTime && nowTime > minTime ? true:false;
    }
}
