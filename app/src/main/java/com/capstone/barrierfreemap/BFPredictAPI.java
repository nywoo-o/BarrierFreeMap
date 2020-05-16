package com.capstone.barrierfreemap;

import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class BFPredictAPI {
    final static String ON_EMULATOR_TEST_URL = "http://10.0.2.2:5000/predict";
    final static String ON_DIVICE_TEST_URL = "http://127.0.0.1:5000/predict";
    final static String BF_URL = ON_DIVICE_TEST_URL;

    String getAccessibility(String encodedImage) {
        Log.e("d", "in acc " + encodedImage.length());
        Log.e("d", "" +encodedImage);
        HttpURLConnection urlConnection = null;
        String ret = null;
        try {
            URL url = new URL(BF_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            Log.e("d", "where" );
            OutputStream out = urlConnection.getOutputStream();
            String strParams = "encoded_string="+encodedImage;
            out.write(strParams.getBytes(StandardCharsets.UTF_8));
            out.flush();
            Log.e("d", "out leng" +  out.toString());
            out.close();

            //writeToOutputStream(urlConnection.getOutputStream(), encodedImage);
            Log.e("d", urlConnection.getResponseCode() + " ");

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }
            String result = readFromInputStream(urlConnection.getInputStream());
            result = convertStandardJSONString(result);
            Log.e("d", result);
            JSONObject jsonObject = new JSONObject(result);
            JSONArray array = jsonObject.getJSONArray("result");
            JSONArray acc = array.getJSONArray(0);
            JSONArray inacc = array.getJSONArray(1);
            Log.e("d", acc.getString(1) + " " + inacc.getString(1));
            ret = "acc: " + acc.getString(1) + " inacc: " + inacc.getString(1);
            //json {result:[["acc":"pre"], ["inacc", "pred"]]}
        } catch (IOException | JSONException e) {
            Log.e("bf", "ERROR");
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return ret;
    }

    private void writeToOutputStream(OutputStream out, String encodedImage) throws IOException {
        String strParams = "encoded_string="+encodedImage;
        out.write(strParams.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
        Log.e("d", "where " + encodedImage.length() + encodedImage.charAt(75586));
    }
    private String readFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = reader.readLine()) != null) {
            result += line;
        }
        return result;
    }

    private static String convertStandardJSONString(String data_json) {
        data_json = data_json.substring(1, data_json.length()-1);
        data_json = data_json.replace("\\", "");
        return data_json;
    }
}
