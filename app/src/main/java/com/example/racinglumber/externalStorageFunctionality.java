package com.example.racinglumber;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class externalStorageFunctionality {
    public static void writeFloatArrayToFile(float[] testVals, Activity activity, String fileName)
    {
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/racingLumber/";
        String TAG = MainActivity.class.getName();

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        try {
            File folder = new File(path+fileName);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            File myFile = new File(folder, "data.txt");// Filename

            FileOutputStream fileOutputStream = new FileOutputStream(myFile,true);

            ByteBuffer buffer = ByteBuffer.allocate(4 * testVals.length);
            for (float value : testVals){
                buffer.putFloat(value);
            }
            fileOutputStream.write(buffer.array());
        }  catch(FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        }  catch(IOException ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
        }

    }
    //////////////////////////////////TODO combine above and below functions
    public static void writeLongArrayToFile(long[] testVals, Activity activity, String fileName)
    {
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/racingLumber/";
        String TAG = MainActivity.class.getName();

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        try {
            File folder = new File(path+fileName);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            File myFile = new File(folder, "data.txt");// Filename

            FileOutputStream fileOutputStream = new FileOutputStream(myFile,true);

            ByteBuffer buffer = ByteBuffer.allocate(8 * testVals.length);
            for (long value : testVals){
                buffer.putFloat(value);
            }
            fileOutputStream.write(buffer.array());
        }  catch(FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        }  catch(IOException ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
        }

    }
}
