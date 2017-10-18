package mdms.osam.mnd.helper;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Administrator on 2017-10-17.
 */

public class HttpRequestHelper {

    private String url;
    private String jsonData;
    private int responseCode;
    private String responseData;

    public HttpRequestHelper(String url, Object sendData){
        this.url = url;
        ObjectMapper om = new ObjectMapper();
        String jsonData = null;
        try {
            jsonData = om.writeValueAsString(sendData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.jsonData = jsonData;
    }

    public void post(){
        HttpRequestAsyncTask httpAsync = new HttpRequestAsyncTask();
        httpAsync.execute();
    }

    public int getResponseCode(){
        return responseCode;
    }

    public void postRequest(String url, String jsonData){
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);


            // 5. set json to StringEntity
            StringEntity se = new StringEntity(jsonData);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            responseCode = httpResponse.getStatusLine().getStatusCode();
            Log.i("responseCode",String.valueOf(responseCode));

            if(responseCode==200){
                String server_response = EntityUtils.toString(httpResponse.getEntity());
                Log.i("Server response", server_response );
            } else {
                Log.i("Server response", "Failed to get server response" );

            }

        } catch (Exception e) {
            Log.d("Exception Occured", e.getLocalizedMessage());
        }
    }

    public void getRequest(){

    }

    public class HttpRequestAsyncTask extends AsyncTask<String, String, Integer> {

        /**
         * 서버 요청 작업을 진행한다.
         */
        @Override
        protected Integer doInBackground(String... arg) {

            postRequest(url,jsonData);
            int result = getResponseCode();
            return result;
        }

    }



}
