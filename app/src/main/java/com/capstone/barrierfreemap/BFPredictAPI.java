package com.capstone.barrierfreemap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class BFPredictAPI {
    final static String ON_EMULATOR_TEST_URL = "http://10.0.2.2:5000/predict";
    final static String ON_DIVICE_TEST_URL = "http://127.0.0.1:5000/predict";
    final static String BF_URL = ON_DIVICE_TEST_URL;

    private UImanager uiManager;

    BFPredictAPI(UImanager uiManager) {
        this.uiManager = uiManager;
    }

    void getProbability(Bitmap bitmap){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig  = Bitmap.Config.RGB_565;

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        byte[] byteArray = out.toByteArray();

        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "androidFlask.jpg",
                        RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();

        postRequest(BF_URL, postBodyImage);
    }

    void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

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
                            try {
                                String res = response.body().string();
                                Log.e("d", res);
                                JSONObject jsonObject = new JSONObject(res);
                                JSONArray array = jsonObject.getJSONArray("result");
                                JSONArray acc = array.getJSONArray(0);
                                JSONArray inacc = array.getJSONArray(1);
                                String ret = "acc: " + acc.getString(1) + " inacc: " + inacc.getString(1);
                                uiManager.setStatusText(ret);
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}
