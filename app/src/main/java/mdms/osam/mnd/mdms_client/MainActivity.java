package mdms.osam.mnd.mdms_client;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import mdms.osam.mnd.helper.HttpRequestHelper;

public class MainActivity extends AppCompatActivity {


    private final String URL = "10.53.128.126:3000/log";
    HttpRequestHelper httpHelper;
    TextView isWorkTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HashMap<String,String> sendData = new HashMap<>();
        sendData.put("username","hi");

        //httpHelper = new HttpRequestHelper(URL,sendData);

        //httpHelper.post();

        isWorkTime = (TextView)findViewById(R.id.tv_isWorkTime);
        if(isNowInWorkTime()){
            isWorkTime.setText("일과중입니다.");
            isWorkTime.setBackgroundColor(0x00FF00);
        }else{
            isWorkTime.setText("일과가 종료되었습니다.");
            isWorkTime.setBackgroundColor(0xe20300);
        }
    }

    /**
     * 일과시간 여부를 검사한다
     * @return
     */
    private boolean isNowInWorkTime(){

        String minimumTime = "8:30:00";
        String limitTime = "17:30:00";

        Calendar nowCal = Calendar.getInstance();

        String[] splitedMinimumTime = minimumTime.split(":");
        String[] splitedLimitTime = minimumTime.split(":");

        Calendar minCal = Calendar.getInstance();
        minCal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(splitedMinimumTime[0]));
        minCal.set(Calendar.MINUTE,Integer.parseInt(splitedMinimumTime[1]));
        minCal.set(Calendar.SECOND,Integer.parseInt(splitedMinimumTime[2]));

        System.out.print(Integer.parseInt(splitedMinimumTime[0]));

        Calendar limCal = Calendar.getInstance();
        limCal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(splitedLimitTime[0]));
        limCal.set(Calendar.MINUTE,Integer.parseInt(splitedLimitTime[1]));
        limCal.set(Calendar.SECOND,Integer.parseInt(splitedLimitTime[2]));

        boolean result = false;

        Log.i("minCal",String.valueOf(nowCal.after(minCal)));
        Log.i("limCal",String.valueOf(nowCal.before(limCal)));

        if(nowCal.after(minCal) && nowCal.before(limCal)){
            result = true;
        }

        return result;
    }
}
