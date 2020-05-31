package com.capstone.barrierfreemap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoogleMapAPI {
    private static final String URL = "https://maps.googleapis.com/maps/api/streetview";
    private static final String APIKey = "apiKey";

    UImanager uiManager;

    GoogleMapAPI(UImanager uiManger){
        this.uiManager = uiManger;
    }

    void getImage(String loc, int sizew, int sizeh) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegments("maps/api/streetview")
                .addQueryParameter("key", APIKey)
                .addQueryParameter("location", loc)
                .addQueryParameter("source", "outdoor")
                .addQueryParameter("size", sizeh+"x"+sizew).build();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.e("fail", "fail");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                if (response.isSuccessful()){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            InputStream in = response.body().byteStream();
                            // convert inputstram to bufferinoutstream
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                            Bitmap bitmap=BitmapFactory.decodeStream(bufferedInputStream);

                            uiManager.setImageViewBitmap(bitmap);
                            uiManager.setStatusText("검색 완료!");
                        }
                    });
                }
            }
        });
    }

}
