package mdms.osam.mnd.mdms_client;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
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

    UserDeviceInfo udi;

    SharedPreferences mPref;

    private final String REG_PREF_KEY = "isRegistered";
    private final String REG_URL = "http://10.53.128.125:3000/registerUser";
    private final int RESPONSE_CODE = 3;

    private static AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setView();

       mPref = PreferenceManager.getDefaultSharedPreferences(this);

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
                } else {
                    Toast.makeText(this, "일시적 오류입니다. 잠시 후 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void sendInfo(UserDeviceInfo udi) {
        try {

            RequestParams params = new RequestParams();
            ObjectMapper om = new ObjectMapper();
            String jsonEntity;
            StringEntity entity;

            jsonEntity = om.writeValueAsString(udi);
            Log.i("jsonEntity", jsonEntity);
            entity = new StringEntity(jsonEntity);
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            client.post(this, REG_URL, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                    String jsonData = new String(bytes);
                    Log.i("Test", "responseData: " + jsonData);
                    Log.i("statusCode", String.valueOf(statusCode));

                    if (statusCode == 201) {
                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putBoolean(REG_PREF_KEY,true);
                        editor.commit();
                        Toast.makeText(RegisterActivity.this, "등록에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(RegisterActivity.this, "네트워크 문제로 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putBoolean(REG_PREF_KEY,false);
                    editor.commit();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
