package com.example.racinglumber;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;

//////////////////////////////////
////////////////TODO NEXT TIME: add time storage for gravity and rotation, and see if 100Hz is still maintained
/////////////////////////////////

public class MainActivity extends AppCompatActivity implements View.OnClickListener , SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senRotation;
    private Sensor senGravity;
    int accelArrayLen = 1000;
    int accelIndex = 0;
    int rotationIndex = 0;
    int gravityIndex = 0;
    /////////////Control Flags/////////////
    boolean dataIsRecording = false;
    boolean saveDataToFile = false;
    /////////////Recorded Data/////////////
    float[] xDataArray = new float[accelArrayLen];
    float[] yDataArray = new float[accelArrayLen];
    float[] zDataArray = new float[accelArrayLen];
    long[] accelEventTime = new long[accelArrayLen];
    float[] xRotationArray = new float[accelArrayLen];
    float[] yRotationArray = new float[accelArrayLen];
    float[] zRotationArray = new float[accelArrayLen];
    float[] xGravityArray = new float[accelArrayLen];
    float[] yGravityArray = new float[accelArrayLen];
    float[] zGravityArray = new float[accelArrayLen];

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
            rotationIndex = 0;
            gravityIndex = 0;
            backward_img.setBackgroundColor(Color.RED);
            dataIsRecording = true;
            onResume();
        }
        else
        {
            endRecording();
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
        //sensor delay game is 20,000ms delay = 100Hz
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        senSensorManager.registerListener(this, senRotation, SensorManager.SENSOR_DELAY_GAME);
        senSensorManager.registerListener(this, senGravity, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }

    public void onSensorChanged(SensorEvent sensorEvent)
    {
        Sensor mySensor = sensorEvent.sensor;

        if (dataIsRecording)
        {
            if ((accelIndex >= accelArrayLen)||(rotationIndex >= accelArrayLen)||(gravityIndex >= accelArrayLen))
            {
                endRecording();
            }
            else
            {
                if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
                {
                    //todo do I need the timestamp here
                    xDataArray[accelIndex] = sensorEvent.values[0];
                    yDataArray[accelIndex] = sensorEvent.values[1];
                    zDataArray[accelIndex] = sensorEvent.values[2];
                    accelEventTime[accelIndex] = SystemClock.elapsedRealtime();
                    accelIndex = accelIndex + 1;
                }
                else if (mySensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
                {
                    xRotationArray[rotationIndex] = sensorEvent.values[0];//x is pitch
                    yRotationArray[rotationIndex] = sensorEvent.values[1];//y is roll
                    zRotationArray[rotationIndex] = sensorEvent.values[2];//z is yaw (what we care about, turning angle)
                    rotationIndex = rotationIndex + 1;
                }
                else if (mySensor.getType() == Sensor.TYPE_GRAVITY)
                {
                    xGravityArray[gravityIndex] = sensorEvent.values[0];
                    yGravityArray[gravityIndex] = sensorEvent.values[1];
                    zGravityArray[gravityIndex] = sensorEvent.values[2];
                    gravityIndex = gravityIndex + 1;
                }
                else
                {
                    //do nothing
                }
            }
        }
    }
    ///////////////////Control Functions//////////////////////
    private void endRecording()
    {
        if (saveDataToFile)
        {
            externalStorageFunctionality.writeFloatArrayToFile(xDataArray, this, "xDataArray");
            externalStorageFunctionality.writeFloatArrayToFile(yDataArray, this, "yDataArray");
            externalStorageFunctionality.writeFloatArrayToFile(zDataArray, this, "zDataArray");
            externalStorageFunctionality.writeLongArrayToFile(accelEventTime, this, "accelTimeArray");
        }

        Button backward_img = (Button) findViewById(R.id.recordButton); //todo combine this with onClick to make endRecord()
        backward_img.setBackgroundColor(Color.WHITE);
        dataIsRecording = false;
        onPause();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
