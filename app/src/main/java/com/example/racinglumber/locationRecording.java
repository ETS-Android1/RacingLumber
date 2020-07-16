package com.example.racinglumber;

import android.app.Activity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class locationRecording {

    private FusedLocationProviderClient fusedLocationClient;

    public void testFunc(Activity activity)
    {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

    }
}
