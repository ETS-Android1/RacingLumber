package com.example.racinglumber;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class graphActivity extends FragmentActivity implements BottomNavigationView.OnNavigationItemSelectedListener , AdapterView.OnItemSelectedListener , OnMapReadyCallback {
    private BottomNavigationView bottomNavigationView;
    private dataStorage recordedVars;
    private GoogleMap mMap;

    private final float graphMinY = 0.05F;
    private final int graphNumXSamplesDisplayed = 100;

    /*Spinner menu items*/
    private final int noSelection = 0;
    private final int xAcceleration = 1;
    private final int yAcceleration = 2;
    private final int zAcceleration = 3;
    private final int magAcceleration = 4;
    private final int xRotation = 5;
    private final int yRotation = 6;
    private final int zRotation = 7;
    private final int magRotation = 8;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_graph_button);

        /*Array adapter and onclick listener for graph datatype selection spinner*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.graphDatatypesArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.graphDataSpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        recordedVars = new dataStorage();

        GraphView graph = (GraphView)findViewById(R.id.graphDisplay);

        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setMinY(-1* graphMinY); //tiny default min max
        graph.getViewport().setMaxY(graphMinY);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(graphNumXSamplesDisplayed);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        /*Start async map fragment*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /************ USER INTERFACE FUNCTIONS ************/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        boolean returnVal = true;

        switch (itemID)
        {
            case R.id.bottom_nav_record_button:
                startActivity(new Intent(graphActivity.this, MainActivity.class));
                break;
            case R.id.bottom_nav_graph_button:
                break;
            case R.id.bottom_nav_save_button:
                startActivity(new Intent(graphActivity.this, fileManageActivity.class));
                break;
            default:
                returnVal = false;
                break;
        }

        return returnVal;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);

        switch (position)
        {
            case noSelection:
                break; //do nothing
            case xAcceleration:
                addGraphSeries(dataStorage.Axis.X, dataStorage.RecordType.acceleration);
                break;
            case yAcceleration:
                addGraphSeries(dataStorage.Axis.Y, dataStorage.RecordType.acceleration);
                break;
            case zAcceleration:
                addGraphSeries(dataStorage.Axis.Z, dataStorage.RecordType.acceleration);
                break;
            case magAcceleration:
                addGraphSeries(dataStorage.Axis.Magnitude, dataStorage.RecordType.acceleration);
                break;
            case xRotation:
                addGraphSeries(dataStorage.Axis.X, dataStorage.RecordType.rotation);
                break;
            case yRotation:
                addGraphSeries(dataStorage.Axis.Y, dataStorage.RecordType.rotation);
                break;
            case zRotation:
                addGraphSeries(dataStorage.Axis.Z, dataStorage.RecordType.rotation);
                break;
            case magRotation:
                addGraphSeries(dataStorage.Axis.Magnitude, dataStorage.RecordType.rotation);
                break;

            default:
                break; //do nothing
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }

    /************ GRAPHING FUNCTIONS ************/

    private void addGraphSeries(dataStorage.Axis axis, dataStorage.RecordType recordType)
    {
        final int graphLineThickness = 10;

        LineGraphSeries<DataPoint> newSeries = new LineGraphSeries();
        float newVal;
        float currentMaxGraphY;
        float maxAbsValue;

        for (int counter = 0; counter < recordedVars.dataArrayLen; counter++)
        {
            if ((recordedVars.dataArrayLen - 1) < counter)
            {
                break;
            }

            newVal = recordedVars.getSensorValue(axis, recordType, counter);
            newSeries.appendData(new DataPoint(counter, newVal), false, recordedVars.dataArrayLen);
        }

        GraphView graph = (GraphView)findViewById(R.id.graphDisplay);

        /*Check if the new data has a maximum larger than existing data.  If so, update the maximum Y value displayed on the graph*/
        currentMaxGraphY = (float)(graph.getViewport().getMaxY(true));
        maxAbsValue = recordedVars.getMaxOfAbsValue(axis, recordType);

        if (maxAbsValue > currentMaxGraphY)
        {
            graph.getViewport().setMinY((-1.0)*maxAbsValue);
            graph.getViewport().setMaxY(maxAbsValue);
        }

        graph.addSeries(newSeries);

        /*Set series-specific graph elements*/

        switch (axis)
        {
            case X:
                switch (recordType)
                {
                    case acceleration:
                        newSeries.setTitle("X Acceleration");
                        newSeries.setColor(0xFFFF4500); //orangered, https://www.rapidtables.com/web/color/red-color.html
                        break;
                    case rotation:
                        newSeries.setTitle("X Rotation");
                        newSeries.setColor(0xFF4169E1); //royalblue
                        break;
                    case gravity:
                        newSeries.setTitle("X Gravity");
                        newSeries.setColor(0xFF7CFC00); //lawngreen
                        break;
                    default:
                        break;
                }
                break;

            case Y:
                switch (recordType)
                {
                    case acceleration:
                        newSeries.setTitle("Y Acceleration");
                        newSeries.setColor(0xFFFF6347); //tomato
                        break;
                    case rotation:
                        newSeries.setTitle("Y Rotation");
                        newSeries.setColor(0xFF6495ED); //cornflowerblue
                        break;
                    case gravity:
                        newSeries.setTitle("Y Gravity");
                        newSeries.setColor(0xFF90EE90); //lightgreen
                        break;
                    default:
                        break;
                }
                break;

            case Z:
                switch (recordType)
                {
                    case acceleration:
                        newSeries.setTitle("Z Acceleration");
                        newSeries.setColor(0xFFDC143C); //crimson
                        break;
                    case rotation:
                        newSeries.setTitle("Z Rotation");
                        newSeries.setColor(0xFF00008B); //darkblue
                        break;
                    case gravity:
                        newSeries.setTitle("Z Gravity");
                        newSeries.setColor(0xFF008000); //green
                        break;
                    default:
                        break;
                }
                break;

            case Magnitude:
                switch (recordType)
                {
                    case acceleration:
                        newSeries.setTitle("Acceleration Magnitude");
                        newSeries.setColor(0xFFFF0000); //red
                        break;
                    case rotation:
                        newSeries.setTitle("Rotation Magnitude");
                        newSeries.setColor(0xFF0000FF); //blue
                        break;
                    case gravity:
                        newSeries.setTitle("Gravity Magnitude");
                        newSeries.setColor(0xFF00FF00); //lime
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        newSeries.setThickness(graphLineThickness);

        if (!graph.getLegendRenderer().isVisible())
        {
            graph.getLegendRenderer().setVisible(true);
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        }
    }






}
