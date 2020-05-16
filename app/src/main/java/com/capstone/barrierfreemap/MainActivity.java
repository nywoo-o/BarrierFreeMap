package com.capstone.barrierfreemap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_CODE = 0;

    private ImageView imageView;
    private Button galleryButton;
    private Button predictButton;
    private TextView textView;

    private ImageLoader imageLoader;
    BFPredictAPI bfPredictAPI;
    Bitmap tmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageLoader = new ImageLoader(getBaseContext());
        bfPredictAPI = new BFPredictAPI();

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        galleryButton = findViewById(R.id.button);
        predictButton = findViewById(R.id.button2);

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromGallery();
            }
        });

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
                String encodedImage = imageLoader.encodingBase64Image(new BitmapDrawable(tmp));
                Log.e("image", ""+encodedImage.length());
                NetworkTask networkTask = new NetworkTask(encodedImage);
                networkTask.execute();
            }
        });
    }

    private void getImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == REQUEST_CODE)
        {
            switch (resultCode) {
                case RESULT_OK:
                    try{
                        InputStream in = getContentResolver().openInputStream(data.getData());

                        Bitmap img = BitmapFactory.decodeStream(in);
                        in.close();
                        tmp = img;
                        BitmapDrawable drawable = new BitmapDrawable(img);
                        imageView.setImageDrawable(drawable);
                    } catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    class NetworkTask extends AsyncTask<Void, Void, String> {

        private String values;

        NetworkTask(String values){
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = bfPredictAPI.getAccessibility(values);
            return result;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            textView.setText(s);
        }
    }

}
