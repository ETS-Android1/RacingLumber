package com.example.racinglumber;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class fileManageActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener , View.OnClickListener {
    private BottomNavigationView bottomNavigationView;
    private static String returnString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manage);

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        Button loadButton = (Button) findViewById(R.id.loadButton);
        loadButton.setOnClickListener(this);

        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_save_button);
    }

    //////////////////////////File Access Functions//////////////////////////

    private static final int fileSaveRequestCode = 1;
    private static final int fileLoadRequestCode = 2;
    private static final int fileDeleteRequestCode = 3;

    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "saveData.txt");

        startActivityForResult(intent, fileSaveRequestCode);
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, fileLoadRequestCode);
    }

    private void deleteFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, fileDeleteRequestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        Uri uri;

        /*The result data contains a URI for the document or directory that the user created*/
        if ((resultCode == Activity.RESULT_OK) && (resultData != null))
        {
            switch (requestCode)
            {
                case fileLoadRequestCode:
                    uri = resultData.getData();
                    returnString = uri.toString();
                    break;

                case fileSaveRequestCode:
                    writeInFile(resultData.getData(), getEncodedDataString());
                    break;

                case fileDeleteRequestCode:
                    uri = resultData.getData();
                    DocumentFile openedDoc = DocumentFile.fromSingleUri(getApplicationContext(), uri);
                    openedDoc.delete();
                    break;
            }
        }
    }

    private void writeInFile(@NonNull Uri uri, @NonNull String text) {
        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(text);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //////////////////////////File Encoding and Decoding Functions//////////////////////////

    private String getEncodedDataString()
    {
        final char dataDelimiter = '\t';

        dataStorage recordedVars;
        int dataArrayLen;
        float accelVal;

        String returnString = "";

        recordedVars = new dataStorage();
        dataArrayLen = recordedVars.getDataArrayLen();

        /*1. Encode the number of data points on its own line*/

        returnString += "Length of Data Arrays";
        returnString += dataDelimiter;
        returnString += Integer.toString(dataArrayLen);
        returnString += '\n';

        /*2. Encode Data Arrays. Split axis and recordType by line*/

        dataStorage.Axis axisVals[] = dataStorage.Axis.values();
        dataStorage.RecordType recordTypeVals[] = dataStorage.RecordType.values();

        for (dataStorage.RecordType recordType : recordTypeVals)
        {
            for (dataStorage.Axis axis : axisVals)
            {
                returnString += dataStorage.getName(axis, recordType)+dataDelimiter;

                for (int index = 0; index < dataArrayLen; index++)
                {
                    accelVal = recordedVars.getValue(axis, recordType, index);
                    returnString += Float.toString(accelVal);
                    returnString += dataDelimiter;
                }
                returnString += '\n';
            }
        }
        return returnString;
    }

    //////////////////////////User Interface Functions//////////////////////////

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.saveButton:
                this.createFile(); //Save button clicked
                break;

            case R.id.loadButton:
                this.openFile(); //Load button clicked
                break;

            case R.id.deleteButton:
                this.deleteFile(); //Delete button clicked
                break;

            default:
                break; //invalid button click, do nothing
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        boolean returnVal = true;

        switch (itemID)
        {
            case R.id.bottom_nav_record_button:
                startActivity(new Intent(fileManageActivity.this, MainActivity.class));
                break;
            case R.id.bottom_nav_graph_button:
                startActivity(new Intent(fileManageActivity.this, graphActivity.class));
                break;
            case R.id.bottom_nav_save_button:
                break;
            default:
                returnVal = false;
                break;
        }

        return returnVal;
    }
}