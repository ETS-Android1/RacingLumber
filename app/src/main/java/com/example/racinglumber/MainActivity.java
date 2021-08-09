package com.example.racinglumber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

//public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener, BottomNavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback{
public class MainActivity extends FragmentActivity implements View.OnClickListener, SensorEventListener, BottomNavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback {
    //public class graphActivity extends   AdapterView.OnItemSelectedListener  {
    private BottomNavigationView bottomNavigationView;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senRotation;
    private Sensor senGravity;
    private FusedLocationProviderClient fusedLocationClient;

    private final int defaultRecordingLength = 10;
    private final int gpsPollingInterval = 1000;
    private final int locationPermissionsRequestCode = 121;
    boolean dataIsRecording = false;

    /*Google map vars*/
    private GoogleMap mMap;
    private float gpsDefaultZoom = 20.0F;
    private int gpsDataIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_record_button);

        /*Set up button listeners*/
        Button recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(this);
        Button left3Button = (Button) findViewById(R.id.left3ButtonMain);
        left3Button.setOnClickListener(this);
        Button left2Button = (Button) findViewById(R.id.left2ButtonMain);
        left2Button.setOnClickListener(this);
        Button left1Button = (Button) findViewById(R.id.left1ButtonMain);
        left1Button.setOnClickListener(this);
        Button right1Button = (Button) findViewById(R.id.right1ButtonMain);
        right1Button.setOnClickListener(this);
        Button right2Button = (Button) findViewById(R.id.right2ButtonMain);
        right2Button.setOnClickListener(this);
        Button right3Button = (Button) findViewById(R.id.right3ButtonMain);
        right3Button.setOnClickListener(this);
        Button setForwardVector = (Button) findViewById(R.id.setForwardVector);
        setForwardVector.setOnClickListener(this);

        /*Start async map fragment*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mainMap);
        mapFragment.getMapAsync(this);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senRotation = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        senGravity = senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /************ MAP FUNCTIONS ************/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng displayedLocation = new LatLng(73.5280, 45.5016);

        mMap.addMarker(new MarkerOptions().position(displayedLocation).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(displayedLocation, gpsDefaultZoom));
    }

    /************ BUTTON FUNCTIONS ************/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        boolean returnVal = true;

        if (!dataIsRecording)
        {
            switch (itemID)
            {
                case R.id.bottom_nav_record_button:
                    break;

                case R.id.bottom_nav_graph_button:
                    startActivity(new Intent(MainActivity.this, graphActivity.class));
                    break;

                case R.id.bottom_nav_save_button:
                    startActivity(new Intent(MainActivity.this, fileManageActivity.class));
                    break;

                default:
                    returnVal = false;
                    break;
            }
        }

        return returnVal;
    }

    @Override
    public void onClick(View v)
    {
        double displayedLat;
        double displayedLong;
        boolean startRecording = false;

        switch(v.getId())
        {
            case R.id.recordButton:
                /*Start/end recording data*/
                dataIsRecording = !dataIsRecording;

                if (dataIsRecording)
                {
                    startRecording();
                    startRecording = true;
                }
                else
                {
                    endRecording();
                }
                break;

            case R.id.left3ButtonMain:
                updateColor(R.id.left3ButtonMain);

                if (gpsDataIndex < 50)
                {
                    gpsDataIndex = 0;
                }
                else
                {
                    gpsDataIndex -= 50;
                }
                break;

            case R.id.left2ButtonMain:
                updateColor(R.id.left2ButtonMain);

                if (gpsDataIndex < 10)
                {
                    gpsDataIndex = 0;
                }
                else
                {
                    gpsDataIndex -= 10;
                }
                break;
            case R.id.left1ButtonMain:
                updateColor(R.id.left1ButtonMain);
                if (gpsDataIndex < 1)
                {
                    gpsDataIndex = 0;
                }
                else
                {
                    gpsDataIndex -= 1;
                }
                break;
            case R.id.right1ButtonMain:
                updateColor(R.id.right1ButtonMain);
                gpsDataIndex += 1;
                break;
            case R.id.right2ButtonMain:
                updateColor(R.id.right2ButtonMain);
                gpsDataIndex += 10;
                break;
            case R.id.right3ButtonMain:
                updateColor(R.id.right3ButtonMain);
                gpsDataIndex += 50;
                break;

            case R.id.setForwardVector:
                dataStorage.initSynthDataArrays();

                if (dataStorage.synthDataArray != null)
                {
                    dataStorage.computeForwardVector(gpsDataIndex);
                    dataStorage.synthDataArray[0].generateSynthDataFromDataStorage();
                }
                break;

            default:
                break;
        }

        if (!startRecording)
        {
            displayedLat = dataStorage.getGPSValueFromAccelDataIndex(true, gpsDataIndex);
            displayedLong = dataStorage.getGPSValueFromAccelDataIndex(false, gpsDataIndex);

            LatLng displayedLocation = new LatLng(displayedLat, displayedLong);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(displayedLocation).title("Current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(displayedLocation));
        }
    }

    private void updateColor(int buttonID)
    {
        /*Color selected button and clear other buttons*/
        Button left3 = (Button) findViewById(R.id.left3ButtonMain);
        Button left2 = (Button) findViewById(R.id.left2ButtonMain);
        Button left1 = (Button) findViewById(R.id.left1ButtonMain);
        Button right1 = (Button) findViewById(R.id.right1ButtonMain);
        Button right2 = (Button) findViewById(R.id.right2ButtonMain);
        Button right3 = (Button) findViewById(R.id.right3ButtonMain);

        left3.setBackgroundColor(Color.LTGRAY);
        left2.setBackgroundColor(Color.LTGRAY);
        left1.setBackgroundColor(Color.LTGRAY);
        right1.setBackgroundColor(Color.LTGRAY);
        right2.setBackgroundColor(Color.LTGRAY);
        right3.setBackgroundColor(Color.LTGRAY);

        switch (buttonID)
        {
            case R.id.left3ButtonMain:
                left3.setBackgroundColor(Color.GREEN);
                break;

            case R.id.left2ButtonMain:
                left2.setBackgroundColor(Color.GREEN);
                break;

            case R.id.left1ButtonMain:
                left1.setBackgroundColor(Color.GREEN);
                break;

            case R.id.right1ButtonMain:
                right1.setBackgroundColor(Color.GREEN);
                break;

            case R.id.right2ButtonMain:
                right2.setBackgroundColor(Color.GREEN);
                break;

            case R.id.right3ButtonMain:
                right3.setBackgroundColor(Color.GREEN);
                break;
        }
    }


    /************ ACCELEROMETER FUNCTIONS ************/

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
            bufferFull = dataStorage.writeSensorValToStorage(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], mySensor.getType());
            if (bufferFull)
            {
                endRecording();
            }
        }
    }

    /************ GPS FUNCTIONS ************/

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            boolean bufferFull;

            if (dataIsRecording && (location != null))
            {
                bufferFull = dataStorage.writeGPSValToStorage(location);
                if (bufferFull)
                {
                    endRecording();
                }
            }
        }
    };

    /************ CONTROL FUNCTIONS ************/

    private void startRecording()
    {
        setDataRecordLength();

        dataStorage.clearStorage();

        Button backward_img = (Button) findViewById(R.id.recordButton);
        backward_img.setBackgroundColor(Color.RED);

        dataIsRecording = true;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(gpsPollingInterval);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, locationPermissionsRequestCode);
        }
        else
        {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

        onResume();
    }

    private void endRecording()
    {
        double displayedLat;
        double displayedLong;

        Button backward_img = (Button) findViewById(R.id.recordButton);
        backward_img.setBackgroundColor(Color.LTGRAY);
        dataIsRecording = false;

        fusedLocationClient.removeLocationUpdates(locationCallback); //this ends gps data polling

        onPause();

        Switch correctTiltSwitch = (Switch) findViewById(R.id.correctTiltSwitch);

        if (correctTiltSwitch.isChecked())
        {
            dataStorage.correctDataSetOrientation();
        }

        displayedLat = dataStorage.getFirstGPSValueFromLastRecording(true);
        displayedLong = dataStorage.getFirstGPSValueFromLastRecording(false);

        LatLng displayedLocation = new LatLng(displayedLat, displayedLong);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(displayedLocation).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(displayedLocation));

        dataStorage.initSynthDataArrays();
        dataStorage.synthDataArray[0].generateSynthDataFromDataStorage();
    }

    /************ USER INTERFACE FUNCTIONS ************/

    private void setDataRecordLength()
    {
        String inputString;
        int recordingMinutes;
        int recordingSeconds;
        int numRecordedSamples;

        final int secondsPerMinute = 60;

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
            recordingSeconds = defaultRecordingLength;
        }

        recordingSeconds += (secondsPerMinute * recordingMinutes);
        numRecordedSamples = 50*recordingSeconds;

        dataStorage.setDataArrayLen(numRecordedSamples);
    }

}//end of mainActivity class
