package com.example.racinglumber;

import androidx.annotation.NonNull;
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
import android.os.PowerManager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

public class MainActivity extends FragmentActivity implements View.OnClickListener, SensorEventListener, BottomNavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback {
    private BottomNavigationView bottomNavigationView;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senRotation;
    private Sensor senGravity;
    private FusedLocationProviderClient fusedLocationClient;

    private final int defaultRecordingLength = 10;//in seconds
    private final int locationPermissionsRequestCode = 121;//arbitrary number
    boolean dataIsRecording = false;

    /*Google map vars*/
    private GoogleMap mMap;
    private float gpsDefaultZoom = 50.0F;
    private int gpsDefaultPeriod = 100; //in milliseconds
    private int gpsDataIndex = 0;//this is the gps data index for the forward vector gps view

    /*System Services*/
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    Boolean appLockSet = false;//if true, do not listen to screen presses and wait for recording to complete
    Boolean appLock = false;//if true, do not listen to screen presses and wait for recording to complete

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Set up bottom navigation listeners*/
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_record_button);

        /*Set up button listeners*/
        Button lockButton = (Button) findViewById(R.id.lockButton);
        lockButton.setOnClickListener(this);
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

        /*Activate sensor listeners/managers*/
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senRotation = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        senGravity = senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
    }

    /************ MAP FUNCTIONS ************/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*This function runs the first time that the gps map fragment is loaded*/
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng displayedLocation = new LatLng(73.5280, 45.5016);//arbitrary starting location

        mMap.addMarker(new MarkerOptions().position(displayedLocation).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(displayedLocation, gpsDefaultZoom));
    }

    /************ BUTTON FUNCTIONS ************/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /*This function switches between views*/
        int itemID = item.getItemId();
        boolean returnVal = true;

        if (appLock)
        {
            return false;
        }

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
        /*This function handles all button presses*/

        double displayedLat;
        double displayedLong;
        boolean startRecording = false;
        boolean startLock = false;

        /*These are the scrolling increments for the gps map view*/
        final int scroll3 = 50;
        final int scroll2 = 10;
        final int scroll1 = 1;

        if (appLock)
        {
            return;
        }

        switch(v.getId())
        {
            case R.id.lockButton:
                /*Lock/unlock buttons to prevent misclick when recording*/
                appLockSet = !appLockSet;
                if (appLockSet)
                {
                    Button backward_img = (Button) findViewById(R.id.lockButton);
                    backward_img.setBackgroundColor(Color.RED);
                }
                else
                {
                    Button backward_img = (Button) findViewById(R.id.lockButton);
                    backward_img.setBackgroundColor(Color.LTGRAY);
                }

                startLock = true;
                break;

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

                if (gpsDataIndex < scroll3)
                {
                    gpsDataIndex = 0;
                }
                else
                {
                    gpsDataIndex -= scroll3;
                }
                break;

            case R.id.left2ButtonMain:
                updateColor(R.id.left2ButtonMain);

                if (gpsDataIndex < scroll2)
                {
                    gpsDataIndex = 0;
                }
                else
                {
                    gpsDataIndex -= scroll2;
                }
                break;

            case R.id.left1ButtonMain:
                updateColor(R.id.left1ButtonMain);

                if (gpsDataIndex < scroll1)
                {
                    gpsDataIndex = 0;
                }
                else
                {
                    gpsDataIndex -= scroll1;
                }
                break;

            case R.id.right1ButtonMain:
                updateColor(R.id.right1ButtonMain);

                if ((gpsDataIndex + scroll1) > dataStorage.GPSIndex)
                {
                    gpsDataIndex = dataStorage.GPSIndex;
                }
                else
                {
                    gpsDataIndex += scroll1;
                }
                break;

            case R.id.right2ButtonMain:
                updateColor(R.id.right2ButtonMain);

                if ((gpsDataIndex + scroll2) > dataStorage.GPSIndex)
                {
                    gpsDataIndex = dataStorage.GPSIndex;
                }
                else
                {
                    gpsDataIndex += scroll2;
                }
                break;

            case R.id.right3ButtonMain:
                updateColor(R.id.right3ButtonMain);
                if ((gpsDataIndex + scroll3) > dataStorage.GPSIndex)
                {
                    gpsDataIndex = dataStorage.GPSIndex;
                }
                else
                {
                    gpsDataIndex += scroll3;
                }
                break;

            case R.id.setForwardVector:
                /* The forward vector is a vector in the direction that the vehicle would go
                 * when accelerating in a straight line.  As long as the app's phone does not move
                 * during recording, this is constant */
                if ((dataStorage.synthDataArray != null) && (dataStorage.GPSIndex > 0))
                {
                    dataStorage.computeForwardVector(gpsDataIndex);
                    dataStorage.synthDataArray[0].generateSynthDataFromDataStorage();
                }
                break;

            default:
                break;
        }

        /*Don't update the map if the user starts recording, because they might not have GPS data yet*/
        if (!startRecording && !startLock)
        {
            /*Update the map view*/
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
        /*This function colors selected button and clears other buttons*/
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
        /*This does nothing, but is required to be defined*/
    }

    public void onSensorChanged(SensorEvent sensorEvent)
    {
        /*This is called by the sensor listeners, and is where the app writes sensor values to storage*/

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
        /*This is called whenever the GPS data updates during recording*/

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
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");

        wakeLock.acquire();

        if (appLockSet)
        {
            appLock = true;
        }

        /*This function is called by hitting the record button and kicks off sensor and GPS listeners*/
        String inputString;
        int gpsInterval;

        final int gpsMinInterval = 10; //minimum of 10ms = 100Hz, since GPS polling rate cannot be faster than accelerometer polling rate

        setDataRecordLength();//read data length from user set minutes+seconds, or use default
        dataStorage.clearStorage();//reset dataStorage arrays and initialize them to data length

        Button backward_img = (Button) findViewById(R.id.recordButton);
        backward_img.setBackgroundColor(Color.RED);

        dataIsRecording = true;

        /*Get GPS polling interval*/
        EditText gpsInput = (EditText) findViewById(R.id.textMinuteInput);
        inputString = gpsInput.getText().toString();

        if (!inputString.isEmpty())
        {
            gpsInterval = Integer.parseInt(inputString);
        }
        else
        {
            gpsInterval = gpsDefaultPeriod;
        }

        if (gpsInterval < gpsMinInterval)
        {
            gpsInterval = gpsMinInterval;
        }

        /*Set up GPS location listener*/
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(gpsInterval);

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
        /*This function finishes recording and starts the tilt correction if it is selected*/

        double displayedLat;
        double displayedLong;

        Button record = (Button) findViewById(R.id.recordButton);
        record.setBackgroundColor(Color.LTGRAY);
        dataIsRecording = false;

        Button backward_img = (Button) findViewById(R.id.lockButton);
        backward_img.setBackgroundColor(Color.LTGRAY);
        appLock = false;
        appLockSet = false;

        fusedLocationClient.removeLocationUpdates(locationCallback); //this ends gps data polling

        onPause(); //this ends accelerometer listening

        Switch correctTiltSwitch = (Switch) findViewById(R.id.correctTiltSwitch);

        if (correctTiltSwitch.isChecked())
        {
            /*If selected, this uses the gravity vector to rotate the data such that the Z vector is
            * vertical (pointing at the sky)*/
            dataStorage.correctDataSetOrientation();
        }

        /*Set the first map data*/
        displayedLat = dataStorage.getFirstGPSValueFromLastRecording(true);
        displayedLong = dataStorage.getFirstGPSValueFromLastRecording(false);

        LatLng displayedLocation = new LatLng(displayedLat, displayedLong);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(displayedLocation).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(displayedLocation));

        /* This is where the synthesized data arrays (0 and 1) are generated.  This will
         * clear any previous data in the synthesized data arrays */
        dataStorage.initSynthDataArrays();
        dataStorage.synthDataArray[0].generateSynthDataFromDataStorage();

        wakeLock.release();
    }

    /************ USER INTERFACE FUNCTIONS ************/

    private void setDataRecordLength()
    {
        /* This function reads the EditText fields where the user sets the data length, and converts
         * that to the dataLength */

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
