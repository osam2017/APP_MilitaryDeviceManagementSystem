package mdms.osam.mnd.mdms_client;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import mdms.osam.mnd.proxy.HttpProxy;
import mdms.osam.mnd.vo.UserDeviceInfo;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editSN;
    EditText editName;
    Spinner milClassSpinner;
    Spinner unitNameSpinner;
    AutoCompleteTextView actvRank;
    Button submit;

    ArrayAdapter<CharSequence> classSpinnerAdapter;
    ArrayAdapter<CharSequence> unitSpinnerAdapter;
    ArrayAdapter<String> autoCompleteAdapter;

    HttpProxy hProxy;
    UserDeviceInfo udi;

    boolean isRequestSuccess;

    private static AsyncHttpClient client = new AsyncHttpClient();

    private final int REQUEST_READ_PHONE_STATE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setView();

    }


    private void setView() {
        /*
        spinner setting
         */
        milClassSpinner = (Spinner) findViewById(R.id.sp_mil_class);
        unitNameSpinner = (Spinner) findViewById(R.id.sp_unit_name);

        classSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.mil_class, android.R.layout.simple_spinner_item);
        classSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        milClassSpinner.setAdapter(classSpinnerAdapter);

        unitSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.unit_name, android.R.layout.simple_spinner_item);
        unitSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        milClassSpinner.setAdapter(unitSpinnerAdapter);

        milClassSpinner.setAdapter(classSpinnerAdapter);
        unitNameSpinner.setAdapter(unitSpinnerAdapter);

        /*
        autoComplete
         */
        actvRank = (AutoCompleteTextView) findViewById(R.id.actv_rank);
        String[] ranks = getResources().getStringArray(R.array.rank);
        autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ranks);
        actvRank.setAdapter(autoCompleteAdapter);
        submit = (Button) findViewById(R.id.bt_submit);
        submit.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:

                Intent i = new Intent(this, MainActivity.class);

                editSN = (EditText) findViewById(R.id.et_sn);
                editName = (EditText) findViewById(R.id.et_name);

                String sn = String.valueOf(editSN.getText());
                String name = String.valueOf(editName.getText());
                String rank = String.valueOf(actvRank.getText());
                String unit_name = unitNameSpinner.getSelectedItem().toString();
                String mil_class = milClassSpinner.getSelectedItem().toString();

                String manft = Build.MANUFACTURER;
                String model = Build.MODEL;
                String os = "Android";
                String os_version = Build.VERSION.RELEASE
                        + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();

                String serviceName = Context.TELEPHONY_SERVICE;
                TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(serviceName);
                //String device_id = m_telephonyManager.getDeviceId();
                String device_id = "deviceid" + Math.random() * 100;

                udi = new UserDeviceInfo(sn, name, mil_class, unit_name, rank, manft, model, device_id, os, os_version);


                if (udi != null) {
                    sendInfo(udi);
                    if (isRequestSuccess) {
                        startActivity(i);
                    } else {
                        Toast.makeText(this, "네트워크 문제로 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else {
                    Toast.makeText(this, "일시적 오류입니다. 잠시 후 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void sendInfo(UserDeviceInfo udi) {
       /* hProxy = new HttpProxy("https://osam2017-server-kelvin37.c9users.io/registerUser");
        new Thread(new Runnable() {
            public void run() {
                String[] result = hProxy.postData(udi);
                //responseCode = Integer.valueOf(result[0]);
                Log.i("result", result[1]);
                int resposnseCode = Integer.valueOf(result[0]);

                if (200 <= resposnseCode && resposnseCode < 300) {
                    isRequestSuccess = true;
                } else {
                    isRequestSuccess = false;
                }
            }
        }).start();

        return hProxy.getResponseStatus();*/
        try {

            RequestParams params = new RequestParams();
            ObjectMapper om = new ObjectMapper();
            String jsonEntity;
            StringEntity entity;

            jsonEntity = om.writeValueAsString(udi);
            Log.i("jsonEntity",jsonEntity);
            entity = new StringEntity(jsonEntity);
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            client.post(this, "http://10.53.128.125:3000/registerUser", entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                    String jsonData = new String(bytes);
                    Log.i("Test", "jsondata: " + jsonData);

                    if (200 <= statusCode && statusCode < 300) {
                        isRequestSuccess = true;
                    } else {
                        isRequestSuccess = false;
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
