package com.example.instaclone.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class ImageManager {
    private static final String TAG = "ImageManager";

    public static Bitmap getBitmap(String imgUrl) {
        StringBuilder stringBuilder;
        String str = "getBitmap: IOException: ";
        String str2 = TAG;
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = new FileInputStream(new File(imgUrl));
            bitmap = BitmapFactory.decodeStream(fis);
            try {
                fis.close();
            } catch (IOException e) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                str = Arrays.toString(e.getStackTrace());
            }
        } catch (FileNotFoundException e2) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("getBitmap: FileNotFoundException: ");
            stringBuilder.append(e2.getMessage());
            Log.d(str2, stringBuilder.toString());
            try {
                fis.close();
            } catch (IOException e3) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                str = Arrays.toString(e3.getStackTrace());
            }
        } catch (Throwable th) {
            try {
                fis.close();
            } catch (IOException e4) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(Arrays.toString(e4.getStackTrace()));
                Log.d(str2, stringBuilder2.toString());
            }
            throw th;
        }
        //return bitmap;
        //stringBuilder.append(str);
        //Log.d(str2, stringBuilder.toString());
        return bitmap;
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }
}
