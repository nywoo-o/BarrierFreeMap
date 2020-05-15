package com.capstone.barrierfreemap;

import android.os.AsyncTask;
import android.util.Log;

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


public class BFPredictAPI {
    final static String BF_URL = "http://10.0.2.2:5000/predict";

    String getAccessibility(String encodedImage) {
        Log.e("bf", "in ACC");

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(BF_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            String strParams = "encoded_string="+encodedImage;
            OutputStream out = urlConnection.getOutputStream();
            out.write(strParams.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();

            Log.e("d", urlConnection.getResponseCode() + " ");

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            String page = "";
            while((line = reader.readLine()) != null){
                page += line;
            }
            return page;
            //json {result:[["acc":"pre"], ["inacc", "pred"]]}
        } catch (IOException e) {
            Log.e("bf", "ERROR");
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return "result";
    }

    private static String getStringFromInputStream(InputStream inputStream) {
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while (((line = bufferedReader.readLine()) != null)) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return stringBuilder.toString();
    }

}
