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
import static com.example.racinglumber.dataStorage.RecordType.rotation;

public class fileManageActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener , View.OnClickListener {
    private BottomNavigationView bottomNavigationView;
    final char dataDelimiter = '~';

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manage);

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        Button loadButton1 = (Button) findViewById(R.id.loadButton1);
        loadButton1.setOnClickListener(this);

        Button loadButton2 = (Button) findViewById(R.id.loadButton2);
        loadButton2.setOnClickListener(this);

        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_save_button);
    }

    //////////////////////////File Access Functions//////////////////////////

    private static final int fileSaveRequestCode = 1;
    private static final int fileLoadRequestCode1 = 2;
    private static final int fileLoadRequestCode2 = 3;
    private static final int fileDeleteRequestCode = 4;

    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "saveData.txt");

        startActivityForResult(intent, fileSaveRequestCode);
    }

    private void openFile(int dataset) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        if (dataset == 1)
        {
            startActivityForResult(intent, fileLoadRequestCode1);
        }
        else
        {
            startActivityForResult(intent, fileLoadRequestCode2);
        }
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
                case fileLoadRequestCode1:
                case fileLoadRequestCode2:
                    loadSaveToDataStorage(resultData.getData());

                    if (dataStorage.synthDataArray == null)
                    {
                        dataStorage.synthDataArray = new synthesizedData[dataStorage.dataArrayLen];
                    }

                    if (requestCode == fileLoadRequestCode1)
                    {
                        dataStorage.synthDataArray[0] = new synthesizedData();
                        dataStorage.synthDataArray[0].generateSynthDataFromDataStorage();
                    }
                    else
                    {
                        dataStorage.synthDataArray[1] = new synthesizedData();
                        dataStorage.synthDataArray[1].generateSynthDataFromDataStorage();
                    }
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

    ///////TODO NEXT this needs to load the forward vector from storage

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
        int valInt;
        long valLong;

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

            //////////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            //Initialize data storage
            dataStorage.clearStorage();
            ///public static void clearStorage()
                ////////////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

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

            //Find delimiter.  After this is acceleration timestamps
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through acceleration timestamps
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char)inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char)inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valLong = Long.parseLong(valString);
                dataStorage.accelEventTime[i] = valLong;
                valString = "";
            }

            //Find delimiter.  After this is rotation vector x
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through x rotation array
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char)inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char)inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valFlt = Float.parseFloat(valString);
                dataStorage.xRotationArray[i] = valFlt;
                valString = "";
            }

            //Find delimiter.  After this is Rotation vector y
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through y rotation array
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char)inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char)inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valFlt = Float.parseFloat(valString);
                dataStorage.yRotationArray[i] = valFlt;
                valString = "";
            }

            //Find delimiter.  After this is Rotation vector z
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through z Rotation array
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char)inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char)inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valFlt = Float.parseFloat(valString);
                dataStorage.zRotationArray[i] = valFlt;
                valString = "";
            }

            //Find delimiter.  After this is rotation timestamps
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through rotation timestamps
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char)inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char)inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valLong = Long.parseLong(valString);
                dataStorage.rotationEventTime[i] = valLong;
                valString = "";
            }

            //Find delimiter.  After this is Gravity vector x
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through x gravity array
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char)inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char)inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valFlt = Float.parseFloat(valString);
                dataStorage.xGravityArray[i] = valFlt;
                valString = "";
            }

            //Find delimiter.  After this is Gravity vector y
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through y gravity array
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char)inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char)inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valFlt = Float.parseFloat(valString);
                dataStorage.yGravityArray[i] = valFlt;
                valString = "";
            }

            //Find delimiter.  After this is Gravity vector z
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through z Gravity array
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char)inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char)inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valFlt = Float.parseFloat(valString);
                dataStorage.zGravityArray[i] = valFlt;
                valString = "";
            }

            //Find delimiter.  After this is gravity timestamps
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through gravity timestamps
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char)inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char)inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valLong = Long.parseLong(valString);
                dataStorage.gravityEventTime[i] = valLong;
                valString = "";
            }

            //Find delimiter.  After this is GPS latitude array
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through latitude array
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char) inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char) inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valFlt = Float.parseFloat(valString);
                dataStorage.latitudeArray[i] = valFlt;
                valString = "";
            }

            //Find delimiter.  After this is GPS longitude array
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through longitude array
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char) inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char) inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valFlt = Float.parseFloat(valString);
                dataStorage.longitudeArray[i] = valFlt;
                valString = "";
            }

            //Find delimiter.  After this is GPS timestamps
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Loop through gravity timestamps
            for (int i = 0; i < dataArrLenCalc; i++)
            {
                //Read an array of bytes into a string
                tempChar = (char)inStream.read();//take first char right away since it'll be valid
                do {
                    valString += tempChar;
                    tempChar = (char)inStream.read();
                } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

                valLong = Long.parseLong(valString);
                dataStorage.GPSEventTime[i] = valLong;
                valString = "";
            }

            //Find delimiter.  After this is forward vectors
            do {
                tempChar = (char)inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            //Decode forward vectors
            //Read an array of bytes into a string
            tempChar = (char) inStream.read();//take first char right away since it'll be valid
            do {
                valString += tempChar;
                tempChar = (char) inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            valFlt = Float.parseFloat(valString);
            dataStorage.forwardVectorX = valFlt;
            valString = "";

            //Read an array of bytes into a string
            tempChar = (char) inStream.read();//take first char right away since it'll be valid
            do {
                valString += tempChar;
                tempChar = (char) inStream.read();
            } while ((tempChar != dataDelimiter) && (inStream.available() > 0));

            valFlt = Float.parseFloat(valString);
            dataStorage.forwardVectorY = valFlt;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

            //////////////////x acceleration//////////////////
                /*Name of data*/
            writtenString = dataStorage.getName(dataStorage.Axis.X, dataStorage.RecordType.acceleration)+dataDelimiter;

                /*Data*/
            for (int index = 0; index < dataArrayLen; index++)
            {
                accelVal = dataStorage.getSensorValue(dataStorage.Axis.X, dataStorage.RecordType.acceleration, index);
                writtenString += Float.toString(accelVal);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

            //////////////////y acceleration//////////////////
                /*Name of data*/
            writtenString = dataStorage.getName(dataStorage.Axis.Y, dataStorage.RecordType.acceleration)+dataDelimiter;

                /*Data*/
            for (int index = 0; index < dataArrayLen; index++)
            {
                accelVal = dataStorage.getSensorValue(dataStorage.Axis.Y, dataStorage.RecordType.acceleration, index);
                writtenString += Float.toString(accelVal);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

            //////////////////z acceleration//////////////////
                /*Name of data*/
            writtenString = dataStorage.getName(dataStorage.Axis.Z, dataStorage.RecordType.acceleration)+dataDelimiter;

                /*Data*/
            for (int index = 0; index < dataArrayLen; index++)
            {
                accelVal = dataStorage.getSensorValue(dataStorage.Axis.Z, dataStorage.RecordType.acceleration, index);
                writtenString += Float.toString(accelVal);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

            /*Data timestamps (same for x,y,z)*/
            writtenString = "Acceleration Timestamps"+dataDelimiter;

            for (int index = 0; index < dataArrayLen; index++)
            {
                timestamp = dataStorage.getTimestampValue(acceleration, index);
                writtenString += Long.toString(timestamp);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

            //////////////////x rotation//////////////////
            /*Name of data*/
            writtenString = dataStorage.getName(dataStorage.Axis.X, rotation)+dataDelimiter;

            /*Data*/
            for (int index = 0; index < dataArrayLen; index++)
            {
                accelVal = dataStorage.getSensorValue(dataStorage.Axis.X, rotation, index);
                writtenString += Float.toString(accelVal);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

            //////////////////y rotation//////////////////
            /*Name of data*/
            writtenString = dataStorage.getName(dataStorage.Axis.Y, rotation)+dataDelimiter;

            /*Data*/
            for (int index = 0; index < dataArrayLen; index++)
            {
                accelVal = dataStorage.getSensorValue(dataStorage.Axis.Y, rotation, index);
                writtenString += Float.toString(accelVal);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

            //////////////////z rotation//////////////////
            /*Name of data*/
            writtenString = dataStorage.getName(dataStorage.Axis.Z, rotation)+dataDelimiter;

            /*Data*/
            for (int index = 0; index < dataArrayLen; index++)
            {
                accelVal = dataStorage.getSensorValue(dataStorage.Axis.Z, rotation, index);
                writtenString += Float.toString(accelVal);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

            /*Rotation timestamps (same for x,y,z)*/
            writtenString = "Rotation Timestamps"+dataDelimiter;

            for (int index = 0; index < dataArrayLen; index++)
            {
                timestamp = dataStorage.getTimestampValue(rotation, index);
                writtenString += Long.toString(timestamp);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

            //////////////////x gravity//////////////////
            /*Name of data*/
            writtenString = dataStorage.getName(dataStorage.Axis.X, dataStorage.RecordType.gravity)+dataDelimiter;

            /*Data*/
            for (int index = 0; index < dataArrayLen; index++)
            {
                accelVal = dataStorage.getSensorValue(dataStorage.Axis.X, dataStorage.RecordType.gravity, index);
                writtenString += Float.toString(accelVal);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

            //////////////////y gravity//////////////////
            /*Name of data*/
            writtenString = dataStorage.getName(dataStorage.Axis.Y, dataStorage.RecordType.gravity)+dataDelimiter;

            /*Data*/
            for (int index = 0; index < dataArrayLen; index++)
            {
                accelVal = dataStorage.getSensorValue(dataStorage.Axis.Y, dataStorage.RecordType.gravity, index);
                writtenString += Float.toString(accelVal);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

            //////////////////z gravity//////////////////
            /*Name of data*/
            writtenString = dataStorage.getName(dataStorage.Axis.Z, dataStorage.RecordType.gravity)+dataDelimiter;

            /*Data*/
            for (int index = 0; index < dataArrayLen; index++)
            {
                accelVal = dataStorage.getSensorValue(dataStorage.Axis.Z, dataStorage.RecordType.gravity, index);
                writtenString += Float.toString(accelVal);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

            /*Gravity timestamps (same for x,y,z)*/
            writtenString = "Gravity Timestamps"+dataDelimiter;

            for (int index = 0; index < dataArrayLen; index++)
            {
                timestamp = dataStorage.getTimestampValue(dataStorage.RecordType.gravity, index);
                writtenString += Long.toString(timestamp);
                writtenString += dataDelimiter;
            }
            writtenString += '\n';
            bw.write(writtenString);

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
            writtenString += '\n';
            bw.write(writtenString);

            /*4. Encode forward vector*/

            writtenString = "Forward vector(x then y)"+dataDelimiter;
            writtenString += Float.toString(dataStorage.forwardVectorX);
            writtenString += dataDelimiter;
            writtenString += Float.toString(dataStorage.forwardVectorY);
            writtenString += dataDelimiter;
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
                if (dataStorage.dataArrayLen > 0)
                {
                    this.createFile(); //Save button clicked
                }
                break;

            case R.id.loadButton1:
                this.openFile(1); //Load button clicked
                break;

            case R.id.loadButton2:
                this.openFile(2); //Load button clicked
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