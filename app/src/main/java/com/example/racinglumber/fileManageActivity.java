package com.example.racinglumber;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static com.example.racinglumber.dataStorage.RecordType.acceleration;

public class fileManageActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener , View.OnClickListener {
    private BottomNavigationView bottomNavigationView;
    final char dataDelimiter = '~';

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
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Uri uri;

        /*The result data contains a URI for the document or directory that the user created*/
        if ((resultCode == Activity.RESULT_OK) && (resultData != null))
        {
            switch (requestCode)
            {
                case fileLoadRequestCode:
                    //todo take string and decode it into data
                    loadSaveToDataStorage(resultData.getData());
                    break;

                case fileSaveRequestCode:
                    writeEncodedDataToFile(resultData.getData());
                    break;

                case fileDeleteRequestCode:
                    uri = resultData.getData();
                    DocumentFile openedDoc = DocumentFile.fromSingleUri(getApplicationContext(), uri);
                    openedDoc.delete();
                    break;

                default:
                    break;
            }
        }
    }

    /////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    ///////////TODO TODO TODO return string is android.something, so it's not the actual return string yet

    private void loadSaveToDataStorage(@NonNull Uri uri)
    {
        InputStream inStream;
        char tempChar;
        //DataArrayLen calculation vars
        int tempInt = 0;
        int dataArrLenCalc = 0;

        //Data array variables
        String valString = "";
        float valFlt;

        try
        {
            //Get the string to parse
            inStream = getContentResolver().openInputStream(uri);

            //Find first delimiter.  After this is dataArrayLen
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Parse dataArrayLen
            while (inStream.available() > 0)
            {
                tempChar = (char)inStream.read();
                tempInt = tempChar - '0';

                if ((tempInt > 10) || (tempInt < 0))
                {
                    break;//out of range, so we are done calculating the int
                }
                else
                {
                    dataArrLenCalc *= 10;
                    dataArrLenCalc += tempInt;
                }
            }

            dataStorage.dataArrayLen = dataArrLenCalc;//todo parse string

            //Find second delimiter.  After this is Acceleration vector x
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through x acceleration array
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char)inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char)inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valFlt = Float.parseFloat(valString);
                dataStorage.xDataArray[i] = valFlt;
                valString = "";
            }

            //Find delimiter.  After this is Acceleration vector y
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through y acceleration array
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char)inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char)inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valFlt = Float.parseFloat(valString);
                dataStorage.yDataArray[i] = valFlt;
                valString = "";
            }

            //Find delimiter.  After this is Acceleration vector z
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through z acceleration array
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char)inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char)inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valFlt = Float.parseFloat(valString);
                dataStorage.zDataArray[i] = valFlt;
                valString = "";
            }

            //./////////////////TODO REST OF THE DATA ARRAYS, BUT LOOK INTO ISSUE ON SAVE FIRST...////////////////////////

        } catch (IOException e) {
            e.printStackTrace();
        }



        //todo for loop through each element
        //dataStorage.xDataArray[0] = 1.0f;
    }
    ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private void writeEncodedDataToFile(@NonNull Uri uri) {
        OutputStream outputStream;
        int dataArrayLen;
        float accelVal;
        double GPSVal;
        long timestamp;

        String writtenString = "";

        try {
            outputStream = getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));

            dataArrayLen = dataStorage.getDataArrayLen();

            /*1. Encode the number of data points on its own line*/

            writtenString += "Length of Data Arrays";
            writtenString += dataDelimiter;
            writtenString += Integer.toString(dataArrayLen);
            writtenString += '\n';
            bw.write(writtenString);

            /*2. Encode Accelerometer/Rotation/Gravity Arrays. Split axis and recordType by line*/

            dataStorage.Axis axisVals[] = dataStorage.Axis.values();
            dataStorage.RecordType recordTypeVals[] = dataStorage.RecordType.values();

            for (dataStorage.RecordType recordType : recordTypeVals)
            {
                /*For loop below goes through x, y, z arrays*/
                for (dataStorage.Axis axis : axisVals)
                {
                    /*Name of data*/
                    writtenString = dataStorage.getName(axis, recordType)+dataDelimiter;

                    /*Data*/
                    for (int index = 0; index < dataArrayLen; index++)
                    {
                        accelVal = dataStorage.getSensorValue(axis, recordType, index);
                        writtenString += Float.toString(accelVal);
                        writtenString += dataDelimiter;
                    }
                    writtenString += '\n';
                    bw.write(writtenString);
                }

                /*Data timestamps (same for x,y,z)*/
                writtenString = "Timestamps"+dataDelimiter;

                for (int index = 0; index < dataArrayLen; index++)
                {
                    timestamp = dataStorage.getTimestampValue(recordType, index);
                    writtenString += Long.toString(timestamp);
                    writtenString += dataDelimiter;
                }
                writtenString += '\n';
                bw.write(writtenString);
            }

            /*2. Encode GPS Arrays. Split axis and recordType by line*/

            writtenString = "LATITUDE"+dataDelimiter;

            for (int index = 0; index < dataArrayLen; index++)
            {
                GPSVal = dataStorage.getGPSValue(true, index);
                writtenString += Double.toString(GPSVal);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

            writtenString = "LONGITUDE"+dataDelimiter;

            for (int index = 0; index < dataArrayLen; index++)
            {
                GPSVal = dataStorage.getGPSValue(false, index);
                writtenString += Double.toString(GPSVal);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

            /*3. Encode GPS Timestamps*/

            writtenString = "GPS Timestamps"+dataDelimiter;

            for (int index = 0; index < dataArrayLen; index++)
            {
                timestamp = dataStorage.getGPSTimestampValue(index);
                writtenString += Long.toString(timestamp);
                writtenString += dataDelimiter;
            }
            bw.write(writtenString);

            /*Flush the buffered write and close the file*/
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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