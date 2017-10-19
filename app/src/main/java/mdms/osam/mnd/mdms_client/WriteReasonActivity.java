package mdms.osam.mnd.mdms_client;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import mdms.osam.mnd.vo.ReasonVO;

public class WriteReasonActivity extends AppCompatActivity {

    private final String REG_URL = "http://10.53.128.126:3000/writeReason";
    private static AsyncHttpClient client = new AsyncHttpClient();
    SharedPreferences mPref;
    private final String REG_SN_KEY = "sn";

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setContentView(R.layout.activity_write_reason);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent receivedIntent = getIntent();
        final String usedFunc = receivedIntent.getExtras().getString("usedFunction");

        AlertDialog.Builder ad = new AlertDialog.Builder(this).setTitle("사유 작성").setMessage(usedFunc+" 기능을 사용한 사유를 작성하십시오.");

        final EditText et = new EditText(this);
        ad.setView(et);

        ad.setPositiveButton("작성완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //서버로 사유를 전송함
                mPref = PreferenceManager.getDefaultSharedPreferences(WriteReasonActivity.this);
                ReasonVO vo = new ReasonVO();
                try {

                    String reason = et.getText().toString();

                    RequestParams params = new RequestParams();
                    ObjectMapper om = new ObjectMapper();
                    String jsonEntity;
                    StringEntity entity;
                    String sn = mPref.getString(REG_SN_KEY,"defaultsn");

                    vo.setSn(sn);
                    vo.setReason(reason);
                    vo.setUsed_func(usedFunc);

                    jsonEntity = om.writeValueAsString(vo);
                    Log.i("jsonEntity", jsonEntity);
                    entity = new StringEntity(jsonEntity);
                    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    client.setTimeout(1000);
                    client.post(WriteReasonActivity.this, REG_URL, entity, "application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                            String jsonData = new String(bytes);
                            Log.i("Test", "responseData: " + jsonData);
                            Log.i("statusCode", String.valueOf(statusCode));
                            finish();

                            if (statusCode == 201) {
                                SharedPreferences.Editor editor = mPref.edit();

                                Toast.makeText(WriteReasonActivity.this, "사유가 작성되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(WriteReasonActivity.this, "네트워크 문제로 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).create().show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_write_reason);

        /*Intent receivedIntent = getIntent();
        final String usedFunc = receivedIntent.getExtras().getString("action");

        AlertDialog.Builder ad = new AlertDialog.Builder(this).setTitle("사유 작성").setMessage(usedFunc+"기능을 사용한 사유를 작성하십시오.");

        final EditText et = new EditText(this);
        ad.setView(et);

        ad.setPositiveButton("작성완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //서버로 사유를 전송함
                mPref = PreferenceManager.getDefaultSharedPreferences(WriteReasonActivity.this);
                ReasonVO vo = new ReasonVO();
                try {

                    String reason = et.getText().toString();

                    RequestParams params = new RequestParams();
                    ObjectMapper om = new ObjectMapper();
                    String jsonEntity;
                    StringEntity entity;
                    String sn = mPref.getString(REG_SN_KEY,"defaultsn");


                    vo.setSn(sn);
                    vo.setReason(reason);
                    vo.setUsed_func(usedFunc);



                    jsonEntity = om.writeValueAsString(vo);
                    Log.i("jsonEntity", jsonEntity);
                    entity = new StringEntity(jsonEntity);
                    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                    client.post(WriteReasonActivity.this, REG_URL, entity, "application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                            String jsonData = new String(bytes);
                            Log.i("Test", "responseData: " + jsonData);
                            Log.i("statusCode", String.valueOf(statusCode));

                            if (statusCode == 201) {
                                SharedPreferences.Editor editor = mPref.edit();

                                Toast.makeText(WriteReasonActivity.this, "사유가 작성되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(WriteReasonActivity.this, "네트워크 문제로 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).create().show();
*/
    }
}
