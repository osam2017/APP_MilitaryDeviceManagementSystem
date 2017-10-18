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
