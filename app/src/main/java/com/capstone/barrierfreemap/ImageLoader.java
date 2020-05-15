package com.capstone.barrierfreemap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ImageLoader {
    static final int REQUEST_CODE = 0;
    private Context context;

    ImageLoader(Context context){
        this.context = context;
    }

    void openGalleryToGetImage(){
        Intent intent = new Intent();
        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((MainActivity)context).startActivityForResult(intent, REQUEST_CODE);
    }

    String encodingBase64Image(BitmapDrawable bd){
        Bitmap bitmap = bd.getBitmap();
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();

        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        bitmap.compress(compressFormat, quality, byteArrayOS);
        return  Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}
