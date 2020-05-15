package com.capstone.barrierfreemap;


import android.content.res.Resources;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.core.content.res.ResourcesCompat;

import static com.capstone.barrierfreemap.BFPredictAPI.getStringFromInputStream;

public class BFPredictAPI {
    final static String BF_URL = "http://localhost:5000/predict";

    void getAccessibility() {

        URL url = null;
        try {
            url = new URL(BF_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(30);
            urlConnection.setReadTimeout(30);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            JSONObject json = new JSONObject(getStringFromInputStream(in));

            Log.e("json", json.toString());
            //json {result:[["acc":"pre"], ["inacc", "pred"]]}
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
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
