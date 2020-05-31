package com.capstone.barrierfreemap;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements UImanager{
    static final int REQUEST_CODE = 0;

    private ImageView imageView;
    private Button galleryButton;
    private Button predictButton;
    private Button searchButton;
    private TextView textView;
    private EditText searchEditText;
    private ImageButton mapIcon;
    private ImageButton galleryIcon;
    private LinearLayout mapSearchBar;
    private LinearLayout galleryBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.predictResultTextView);

        galleryIcon = findViewById(R.id.galleryIconButton);
        galleryBar = findViewById(R.id.galleryBars);
        galleryButton = findViewById(R.id.galleryButton);
        predictButton = findViewById(R.id.predictButton);

        mapIcon = findViewById(R.id.mapIconButton);
        mapSearchBar = findViewById(R.id.mapSearchBars);
        searchButton = findViewById(R.id.searchButton);
        searchEditText = findViewById(R.id.searchEditText);

        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapIcon.setAlpha(1.0f);
                galleryIcon.setAlpha(0.3f);

                mapSearchBar.setVisibility(View.VISIBLE);
                galleryBar.setVisibility(View.GONE);
            }
        });

        galleryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapIcon.setAlpha(0.3f);
                galleryIcon.setAlpha(1.0f);

                mapSearchBar.setVisibility(View.GONE);
                galleryBar.setVisibility(View.VISIBLE);
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromGallery();
            }
        });

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(getString(R.string.predictingStatus));
                BFPredictAPI bfPredictAPI = new BFPredictAPI(MainActivity.this);
                bfPredictAPI.getProbability(getImageBitmap());
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(getString(R.string.searchingStatus));
                GoogleMapAPI googleMapAPI = new GoogleMapAPI(MainActivity.this);
                googleMapAPI.getImage(searchEditText.getText().toString(), 299, 299);
            }
        });
    }

    private void getImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == REQUEST_CODE)
        {
            switch (resultCode) {
                case RESULT_OK:
                    try{
                        Uri uri = data.getData();

                        InputStream in = getContentResolver().openInputStream(uri);
                        Bitmap img = BitmapFactory.decodeStream(in);
                        in.close();

                        img = rotateImage(uri, img);
                        imageView.setImageBitmap(img);
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

    @Override
    public void setStatusText(String text) {
        textView.setText(text);
    }

    @Override
    public Bitmap getImageBitmap(){
        return ((BitmapDrawable) imageView.getDrawable()).getBitmap();
    }

    @Override
    public void setImageViewBitmap(Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Bitmap rotateImage(Uri uri, Bitmap bitmap) throws IOException {
        InputStream in = getContentResolver().openInputStream(uri);
        ExifInterface exif = new ExifInterface(in);
        in.close();

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Matrix matrix = new Matrix();
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            matrix.postRotate(90);
        }
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            matrix.postRotate(180);
        }
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            matrix.postRotate(270);
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
