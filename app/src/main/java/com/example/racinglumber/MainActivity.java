package com.example.racinglumber;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senRotation;
    private Sensor senGravity;
    private dataStorage recordedVars;
    int accelIndex = 0; //index of x/y/zDataArray
    int rotationIndex = 0; //index of x/y/zRotationArray
    int gravityIndex = 0; //index of x/y/zGravityArray
    /*Control Flags*/
    boolean dataIsRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(this);

        Button graphButton = (Button) findViewById(R.id.graphButton);
        graphButton.setOnClickListener(this);

        recordedVars = new dataStorage();

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senRotation = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        senGravity = senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
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
            if ((accelIndex >= dataStorage.dataArrayLen)||(rotationIndex >= dataStorage.dataArrayLen)||(gravityIndex >= dataStorage.dataArrayLen))
            {
                endRecording();
                //todo why does calling fileStorage.writeFloatArrayToFile() here cause a system error?
            }
            else
            {
                if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
                {
                    recordedVars.xDataArray[accelIndex] = sensorEvent.values[0];
                    recordedVars.yDataArray[accelIndex] = sensorEvent.values[1];
                    recordedVars.zDataArray[accelIndex] = sensorEvent.values[2];
                    recordedVars.accelEventTime[accelIndex] = SystemClock.elapsedRealtime();
                    accelIndex = accelIndex + 1;
                }
                else if (mySensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
                {
                    recordedVars.xRotationArray[rotationIndex] = sensorEvent.values[0];//x is pitch
                    recordedVars.yRotationArray[rotationIndex] = sensorEvent.values[1];//y is roll
                    recordedVars.zRotationArray[rotationIndex] = sensorEvent.values[2];//z is yaw
                    recordedVars.rotationEventTime[rotationIndex] = SystemClock.elapsedRealtime();
                    rotationIndex = rotationIndex + 1;
                }
                else if (mySensor.getType() == Sensor.TYPE_GRAVITY)
                {
                    recordedVars.xGravityArray[gravityIndex] = sensorEvent.values[0];
                    recordedVars.yGravityArray[gravityIndex] = sensorEvent.values[1];
                    recordedVars.zGravityArray[gravityIndex] = sensorEvent.values[2];
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
        Button backward_img = (Button) findViewById(R.id.recordButton);
        backward_img.setBackgroundColor(Color.WHITE);
        dataIsRecording = false;
        onPause();
        //todo save to file here?
    }
}
