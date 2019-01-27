package io.pp.net_disk_demo.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {
    private static final String TAG = "BitmapUtil";

    public static void saveBitmap(Bitmap bitmap,String filePath) {
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }

        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "saveBitmap() error: " + e.getMessage());

            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "saveBitmap() error: " + e.getMessage());

            e.printStackTrace();
        }
    }
}