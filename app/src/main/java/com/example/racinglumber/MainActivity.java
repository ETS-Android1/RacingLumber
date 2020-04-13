package com.example.racinglumber;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senRotation;
    private Sensor senGravity;
    boolean dataIsRecording = false;
    int accelArrayLen = 100;
    int accelIndex = 0;
    float[] xDataArray = new float[accelArrayLen];
    float[] yDataArray = new float[accelArrayLen];
    float[] zDataArray = new float[accelArrayLen];
    float[] xRotationArray = new float[accelArrayLen];
    float[] yRotationArray = new float[accelArrayLen];
    float[] zRotationArray = new float[accelArrayLen];

    //float[] gravityQuaternion = new float[4]; //this reference needs to persist between sensor events
    float[] gravityQuaternion = {1.0F,0.0F,0.0F,0.0F};

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonname = (Button) findViewById(R.id.recordButton);
        buttonname.setOnClickListener(this);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senRotation = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        senGravity = senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }
///////////BUTTON FUNCTIONS//////////////
    @Override
    public void onClick(View v)
    {
        Button backward_img = (Button) findViewById(R.id.recordButton);
        dataIsRecording = !dataIsRecording;

        if (dataIsRecording)
        {
            accelIndex = 0;
            backward_img.setBackgroundColor(Color.RED);
            dataIsRecording = true;
            onResume();
        }
        else
        {
            backward_img.setBackgroundColor(Color.WHITE);
            dataIsRecording = false;
            //todo we will need to save to file here too
            onPause();
        }
    }
///////////ACCELEROMETER FUNCTIONS/////////////
    protected void onPause()
    {
        super.onPause();
        senSensorManager.unregisterListener(this);

    }

    protected void onResume()
    {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL); //todo I need less delay on corners, more on straights
        senSensorManager.registerListener(this, senRotation, SensorManager.SENSOR_DELAY_NORMAL); //todo I need less delay on corners, more on straights
        senSensorManager.registerListener(this, senGravity, SensorManager.SENSOR_DELAY_NORMAL); //todo I need less delay on corners, more on straights
        //sensor delay game is 20,000ms delay = 100Hz
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }

    public void onSensorChanged(SensorEvent sensorEvent)
    {
        float xValue,yValue,zValue;
        Sensor mySensor = sensorEvent.sensor;

        if (dataIsRecording)
        {
            if (accelIndex < accelArrayLen)
            {
                if ((mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION))
                {
                    xValue = sensorEvent.values[0];
                    yValue = sensorEvent.values[1];
                    zValue = sensorEvent.values[2];
                    //todo do I need the timestamp here
                    xDataArray[accelIndex] = xValue;
                    yDataArray[accelIndex] = yValue;
                    zDataArray[accelIndex] = zValue;
                    accelIndex = accelIndex + 1;
                }
                else if ((mySensor.getType() == Sensor.TYPE_ROTATION_VECTOR))
                {
                    xValue = sensorEvent.values[0];//x is pitch
                    yValue = sensorEvent.values[1];//y is roll
                    zValue = sensorEvent.values[2];//z is yaw (what we care about, turning angle)

                    //TODO get quaternion of gravity, multiply, then take angle of ij axis as rotation
                    float[] rotationQuaternion = new float[4];
                    SensorManager.getQuaternionFromVector(rotationQuaternion, sensorEvent.values);

                    xRotationArray[accelIndex] = xValue;
                    yRotationArray[accelIndex] = yValue;
                    zRotationArray[accelIndex] = zValue;
                    accelIndex = accelIndex + 1;
                }
                else if ((mySensor.getType() == Sensor.TYPE_GRAVITY))
                {
                    SensorManager.getQuaternionFromVector(gravityQuaternion, sensorEvent.values); //update gravity reference
                }
                else
                {
                    //do nothing
                }
            }
            else
            {
//                externalStorageAPI.accelDataWriteToFile(xDataArray, this, "xDataArray");
//                externalStorageAPI.accelDataWriteToFile(yDataArray, this, "yDataArray");
//                externalStorageAPI.accelDataWriteToFile(zDataArray, this, "zDataArray");

                Button backward_img = (Button) findViewById(R.id.recordButton); //todo combine this with onClick to make endRecord()
                backward_img.setBackgroundColor(Color.WHITE);
                dataIsRecording = false;
                onPause();
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
