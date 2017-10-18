package mdms.osam.mnd.proxy;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Administrator on 2017-10-17.
 */

public class HttpProxy {
    String requestURL;
    int status;

    public HttpProxy(String requestURL) {
        this.requestURL = requestURL;
    }

    public int getResponseStatus(){
        return status;
    }

    public String getJSON() {
        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(10 * 1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Connection", "Kepp-Alive");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Accept", "application/json");

            conn.setDoInput(true);
            conn.connect();

            int status = conn.getResponseCode();
            Log.i("test", "ProxyResponseCode:" + status);

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("test", "NETWORK ERROR:" + e);
        }
        return null;
    }

    public String postData(Object obj) {
        String result = "NO DATA";
        HttpURLConnection conn = null;
        URL url = null;
        try {
            url = new URL(requestURL);

            InputStream is = null;
            ByteArrayOutputStream baos = null;

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(10 * 1000);
            conn.setRequestProperty("Connection", "Kepp-Alive");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "application/json");

            conn.setDoOutput(true);
            conn.setDoInput(true);

            ObjectMapper om = new ObjectMapper();
            String json = om.writeValueAsString(obj);

            status = conn.getResponseCode();
            Log.i("test", "ProxyResponseCode:" + status);

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    //JSONObject responseJSON = new JSONObject(sb.toString());
                    result = sb.toString();
                    break;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}

