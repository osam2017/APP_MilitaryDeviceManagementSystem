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

    private final int REQUEST_READ_PHONE_STATE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setView();


    }

    private int sendInfo() {
        hProxy = new HttpProxy("http://10.53.128.126:3000/topic");

        new Thread(new Runnable() {
            public void run() {
                String result = hProxy.postData(udi);
                Log.i("result", result);
            }
        }).start();

        return hProxy.getResponseStatus();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {
                        Toast.makeText(this,"사용자 요청에 의해 중단되었습니다.",Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void checkAndGetPermission(){
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_READ_PHONE_STATE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:

                checkAndGetPermission();
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
                String device_id = "deviceid"+Math.random()*100;

                udi = new UserDeviceInfo(sn,name,mil_class,unit_name,rank,manft,model,device_id,os,os_version);


                if (udi != null) {
                    int result = sendInfo();
                    if(result < 300 && result >= 200 ){
                        startActivity(i);
                    }else{
                        Toast.makeText(this, "네트워크 문제로 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else {
                    Toast.makeText(this, "일시적 오류입니다. 잠시 후 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                }


        }
    }


}
