package com.example.racinglumber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener, BottomNavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback{
    private BottomNavigationView bottomNavigationView;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senRotation;
    private Sensor senGravity;
    private dataStorage recordedVars;
    private FusedLocationProviderClient fusedLocationClient;

    private final int locationPermissionsRequestCode = 121;

    /*Control Flags*/
    boolean dataIsRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_record_button);

        recordedVars = new dataStorage();

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senRotation = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        senGravity = senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this); //todo make this a service?
    }

///////////BUTTON FUNCTIONS//////////////
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        boolean returnVal = true;

        switch (itemID)
        {
            case R.id.bottom_nav_record_button:
                break;
            case R.id.bottom_nav_graph_button:
                if (!dataIsRecording)
                {
                    startActivity(new Intent(MainActivity.this, graphActivity.class));
                }
                break;
            case R.id.bottom_nav_save_button:
                if (!dataIsRecording)
                {
                    startActivity(new Intent(MainActivity.this, fileManageActivity.class));
                }
                break;
            default:
                returnVal = false;
                break;
        }

        return returnVal;
    }

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
        boolean bufferFull;
        Sensor mySensor = sensorEvent.sensor;

        if (dataIsRecording)
        {
            bufferFull = recordedVars.writeSensorValToStorage(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], mySensor.getType());
            if (bufferFull)
            {
                endRecording();
            }

//            if (waitingForLocationResponse == false) //not waiting for a response
//            {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, locationPermissionsRequestCode);
//                }
//                else {
//                    waitingForLocationResponse = true;
//                    fusedLocationClient.getLastLocation()
//                            .addOnSuccessListener(this, new OnSuccessListener<Location>()
//                            {
//                                @Override
//                                public void onSuccess(Location location)
//                                {
//                                    if (location != null)
//                                    {
//                                        waitingForLocationResponse = false;
//                                        processReceivedLocation(location);
//                                    }
//                                }
//                            });
//                }
//            }
        }
    }

//    public void processReceivedLocation(Location location)//todo put in a service?
//    {
//        boolean bufferFull;
//
//        if (dataIsRecording)
//        {
//            bufferFull = recordedVars.writeGPSValToStorage(location.getLatitude(), location.getLongitude());
//            if (bufferFull)
//            {
//                endRecording();
//            }
//
//            //todo timestamp record here or in above function? (writegpsvaltostorage())
//        }
//    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location == null) return;
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            streetViewPanorama.setPosition(latLng);

            boolean bufferFull;

            if (dataIsRecording)
            {
                bufferFull = recordedVars.writeGPSValToStorage(location.getLatitude(), location.getLongitude());
                if (bufferFull)
                {
                    endRecording();
                }

                //todo timestamp record here or in above function? (writegpsvaltostorage())
            }

            // If you do not want to cancel the location updates after receiving the first location then comment the below line.
            //fusedLocationClient.removeLocationUpdates(locationCallback); //todo run this at end recording
        }
    };

    ///////////////////Control Functions//////////////////////
    private void startRecording()
    {
        setDataRecordLength();

        recordedVars.clearStorage();

        Button backward_img = (Button) findViewById(R.id.recordButton);
        dataIsRecording = !dataIsRecording;
        backward_img.setBackgroundColor(Color.RED);

        dataIsRecording = true;

        ///////////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);

        ////////////////////////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>code from https://codinginfinite.com/current-location-google-map-street-view-android/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, locationPermissionsRequestCode);
        }
        else {

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//                waitingForLocationResponse = true;
//                fusedLocationClient.getLastLocation()
//                        .addOnSuccessListener(this, new OnSuccessListener<Location>()
//                        {
//                            @Override
//                            public void onSuccess(Location location)
//                            {
//                                if (location != null)
//                                {
//                                    waitingForLocationResponse = false;
//                                    processReceivedLocation(location);
//                                }
//                            }
//                        });
        }
        //////////////////////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        onResume();
    }

    private void endRecording()
    {
        Button backward_img = (Button) findViewById(R.id.recordButton);
        backward_img.setBackgroundColor(Color.WHITE);
        dataIsRecording = false;

        fusedLocationClient.removeLocationUpdates(locationCallback); //todo run this at end recording

        onPause();

        Switch correctTiltSwitch = (Switch) findViewById(R.id.correctTiltSwitch);

        if (correctTiltSwitch.isChecked())
        {
            recordedVars.correctedDataPoints();
        }
    }

    private void setDataRecordLength()
    {
        String inputString;
        int recordingMinutes;
        int recordingSeconds;
        int numRecordedSamples;

        EditText minuteInput = (EditText) findViewById(R.id.textMinuteInput);
        inputString = minuteInput.getText().toString();

        if (!inputString.isEmpty())
        {
            recordingMinutes = Integer.parseInt(inputString);
        }
        else
        {
            recordingMinutes = 0;
        }

        EditText secondInput = (EditText) findViewById(R.id.textSecondInput);
        inputString = secondInput.getText().toString();

        if (!inputString.isEmpty())
        {
            recordingSeconds = Integer.parseInt(inputString);
        }
        else
        {
            recordingSeconds = 10;
        }

        //todo add variable calcs for different accuracies here.  Assume SensorManager.SENSOR_DELAY_GAME (50 samples/second) for now
        recordingSeconds += (60 * recordingMinutes);
        numRecordedSamples = 50*recordingSeconds; //todo this is the assumed sample rate.  Replace with switch statement

        recordedVars.setDataArrayLen(numRecordedSamples);
    }

}//end of mainActivity class
