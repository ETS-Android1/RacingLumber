package com.example.racinglumber;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener , SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senRotation;
    private Sensor senGravity;
    int dataArrayLen = 1000;
    int accelIndex = 0; //index of x/y/zDataArray
    int rotationIndex = 0; //index of x/y/zRotationArray
    int gravityIndex = 0; //index of x/y/zGravityArray
    /*Control Flags*/
    boolean dataIsRecording = false;
    boolean saveDataToFile = false;
    /*Recorded Data*/
    float[] xDataArray = new float[dataArrayLen];
    float[] yDataArray = new float[dataArrayLen];
    float[] zDataArray = new float[dataArrayLen];
    long[] accelEventTime = new long[dataArrayLen];

    float[] xRotationArray = new float[dataArrayLen];
    float[] yRotationArray = new float[dataArrayLen];
    float[] zRotationArray = new float[dataArrayLen];
    long[] rotationEventTime = new long[dataArrayLen];

    float[] xGravityArray = new float[dataArrayLen];
    float[] yGravityArray = new float[dataArrayLen];
    float[] zGravityArray = new float[dataArrayLen];

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(this);

        Button graphButton = (Button) findViewById(R.id.graphButton);
        graphButton.setOnClickListener(this);

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
        if (v.getId() == R.id.recordButton)
        {
            /*Start/end recording data*/
            dataIsRecording = !dataIsRecording;

            if (dataIsRecording)
            {
                startRecording();
            }
            else
            {
                endRecording();
            }
        }
        else
        {
            /*graph button was pressed.  End recording and switch to new view*/
            if (!dataIsRecording)
            {
                //todo switch to new activity
                startActivity(new Intent(MainActivity.this, graphActivity.class));
            }
            //todo error conditions.  What if pressed before recording?
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
        senSensorManager.registerListener(this, senGravity, SensorManager.SENSOR_DELAY_GAME); //todo we don't need a gravity vector this accurate
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
            if ((accelIndex >= dataArrayLen)||(rotationIndex >= dataArrayLen)||(gravityIndex >= dataArrayLen))
            {
                endRecording();
            }
            else
            {
                if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
                {
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
                    zRotationArray[rotationIndex] = sensorEvent.values[2];//z is yaw
                    rotationEventTime[rotationIndex] = SystemClock.elapsedRealtime();
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
    private void startRecording()
    {
        accelIndex = 0;
        rotationIndex = 0;
        gravityIndex = 0;

        Button backward_img = (Button) findViewById(R.id.recordButton);
        dataIsRecording = !dataIsRecording;
        backward_img.setBackgroundColor(Color.RED);

        dataIsRecording = true;
        onResume();
    }

    private void endRecording()
    {
        if (saveDataToFile)
        {
            externalStorageFunctionality.writeFloatArrayToFile(xDataArray, this, "xDataArray");
            externalStorageFunctionality.writeFloatArrayToFile(yDataArray, this, "yDataArray");
            externalStorageFunctionality.writeFloatArrayToFile(zDataArray, this, "zDataArray");
            externalStorageFunctionality.writeLongArrayToFile(accelEventTime, this, "accelTimeArray");
            externalStorageFunctionality.writeLongArrayToFile(rotationEventTime, this, "rotationTimeArray");
        }

        Button backward_img = (Button) findViewById(R.id.recordButton);
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
