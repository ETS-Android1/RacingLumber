package com.example.racinglumber;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class graphActivity extends FragmentActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener , AdapterView.OnItemSelectedListener , OnMapReadyCallback {
    private BottomNavigationView bottomNavigationView;
    private GoogleMap mMap;

    private final float graphMinY = 0.05F; //start with very small minimum value, that is overwritten by first dataset
    private final int graphNumXSamplesDisplayed = 100;

    /*Spinner menu items*/
    private final int noSelection = 0;
    private final int setOneLatAccel = 1;
    private final int setOneLongAccel = 2;
    private final int setTwoLatAccel = 3;
    private final int setTwoLongAccel = 4;

    /*Google map vars*/
    private float gpsDefaultZoom = 20.0F;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_graph_button);

        /*Set up selected set buttons and selected set*/
        Button setOneButton = (Button) findViewById(R.id.setOneButton);
        setOneButton.setOnClickListener(this);

        Button setTwoButton = (Button) findViewById(R.id.setTwoButton);
        setTwoButton.setOnClickListener(this);

        Button bothSetsButton = (Button) findViewById(R.id.bothSetsButton);
        bothSetsButton.setOnClickListener(this);

        dataStorage.selectedSet = dataStorage.SelectedSet.setOne;

        /*Set up data scrolling button listeners*/
        Button left3Button = (Button) findViewById(R.id.left3Button);
        left3Button.setOnClickListener(this);
        Button left2Button = (Button) findViewById(R.id.left2Button);
        left2Button.setOnClickListener(this);
        Button left1Button = (Button) findViewById(R.id.left1Button);
        left1Button.setOnClickListener(this);
        Button right1Button = (Button) findViewById(R.id.right1Button);
        right1Button.setOnClickListener(this);
        Button right2Button = (Button) findViewById(R.id.right2Button);
        right2Button.setOnClickListener(this);
        Button right3Button = (Button) findViewById(R.id.right3Button);
        right3Button.setOnClickListener(this);

        /*Array adapter and onclick listener for graph datatype selection spinner*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.graphDatatypesArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.graphDataSpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        GraphView graph = (GraphView)findViewById(R.id.graphDisplay);

        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setMinY(-1* graphMinY);
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

        graph.getViewport().setOnXAxisBoundsChangedListener(new Viewport.OnXAxisBoundsChangedListener() {
            @Override
            public void onXAxisBoundsChanged(double minX, double maxX, Reason reason) {
                double displayedLat;
                double displayedLong;

                displayedLat = dataStorage.getGPSValueFromAccelDataIndex(true, (int)minX);
                displayedLong = dataStorage.getGPSValueFromAccelDataIndex(false, (int)minX);

                LatLng displayedLocation = new LatLng(displayedLat, displayedLong);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(displayedLocation).title("Current location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(displayedLocation));
            }
        });
    }

    /************ BUTTON FUNCTIONS ************/

    @Override
    public void onClick(View v) {
        Button setOne       = (Button) findViewById(R.id.setOneButton);
        Button setTwo       = (Button) findViewById(R.id.setTwoButton);
        Button setOneTwo    = (Button) findViewById(R.id.bothSetsButton);

        Button left3 = (Button) findViewById(R.id.left3Button);
        Button left2 = (Button) findViewById(R.id.left2Button);
        Button left1 = (Button) findViewById(R.id.left1Button);
        Button right1 = (Button) findViewById(R.id.right1Button);
        Button right2 = (Button) findViewById(R.id.right2Button);
        Button right3 = (Button) findViewById(R.id.right3Button);

        if (v.getId() == R.id.setOneButton)
        {
            dataStorage.selectedSet = dataStorage.SelectedSet.setOne;
            /*Color selected button and clear other buttons*/
            setOne.setBackgroundColor(Color.GREEN);
            setTwo.setBackgroundColor(Color.LTGRAY);
            setOneTwo.setBackgroundColor(Color.LTGRAY);
        }
        else if (v.getId() == R.id.setTwoButton)
        {
            dataStorage.selectedSet = dataStorage.SelectedSet.setTwo;
            /*Color selected button and clear other buttons*/
            setOne.setBackgroundColor(Color.LTGRAY);
            setTwo.setBackgroundColor(Color.GREEN);
            setOneTwo.setBackgroundColor(Color.LTGRAY);
        }
        else if (v.getId() == R.id.bothSetsButton)
        {
            dataStorage.selectedSet = dataStorage.SelectedSet.setOneTwo;
            /*Color selected button and clear other buttons*/
            setOne.setBackgroundColor(Color.LTGRAY);
            setTwo.setBackgroundColor(Color.LTGRAY);
            setOneTwo.setBackgroundColor(Color.GREEN);
        }
        /*Below buttons are mutually exclusive from above buttons and have no direct interaction*/
        else if (v.getId() == R.id.left3Button)
        {
            /*Color selected button and clear other buttons*/
            left3.setBackgroundColor(Color.GREEN);
            left2.setBackgroundColor(Color.LTGRAY);
            left1.setBackgroundColor(Color.LTGRAY);
            right1.setBackgroundColor(Color.LTGRAY);
            right2.setBackgroundColor(Color.LTGRAY);
            right3.setBackgroundColor(Color.LTGRAY);
        }
        else if (v.getId() == R.id.left2Button)
        {
            /*Color selected button and clear other buttons*/
            left3.setBackgroundColor(Color.LTGRAY);
            left2.setBackgroundColor(Color.GREEN);
            left1.setBackgroundColor(Color.LTGRAY);
            right1.setBackgroundColor(Color.LTGRAY);
            right2.setBackgroundColor(Color.LTGRAY);
            right3.setBackgroundColor(Color.LTGRAY);
        }
        else if (v.getId() == R.id.left1Button)
        {
            /*Color selected button and clear other buttons*/
            left3.setBackgroundColor(Color.LTGRAY);
            left2.setBackgroundColor(Color.LTGRAY);
            left1.setBackgroundColor(Color.GREEN);
            right1.setBackgroundColor(Color.LTGRAY);
            right2.setBackgroundColor(Color.LTGRAY);
            right3.setBackgroundColor(Color.LTGRAY);
        }
        else if (v.getId() == R.id.right1Button)
        {
            /*Color selected button and clear other buttons*/
            left3.setBackgroundColor(Color.LTGRAY);
            left2.setBackgroundColor(Color.LTGRAY);
            left1.setBackgroundColor(Color.LTGRAY);
            right1.setBackgroundColor(Color.GREEN);
            right2.setBackgroundColor(Color.LTGRAY);
            right3.setBackgroundColor(Color.LTGRAY);
        }
        else if (v.getId() ==  R.id.right2Button)
        {
            /*Color selected button and clear other buttons*/
            left3.setBackgroundColor(Color.LTGRAY);
            left2.setBackgroundColor(Color.LTGRAY);
            left1.setBackgroundColor(Color.LTGRAY);
            right1.setBackgroundColor(Color.LTGRAY);
            right2.setBackgroundColor(Color.GREEN);
            right3.setBackgroundColor(Color.LTGRAY);
        }
        else// v.getId() == R.id.right3Button
        {
            /*Color selected button and clear other buttons*/
            left3.setBackgroundColor(Color.LTGRAY);
            left2.setBackgroundColor(Color.LTGRAY);
            left1.setBackgroundColor(Color.LTGRAY);
            right1.setBackgroundColor(Color.LTGRAY);
            right2.setBackgroundColor(Color.LTGRAY);
            right3.setBackgroundColor(Color.GREEN);
        }
    }

    /************ MAP FUNCTIONS ************/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        double displayedLat;
        double displayedLong;

        mMap = googleMap;

        displayedLat = dataStorage.getGPSValueFromAccelDataIndex(true, 0);
        displayedLong = dataStorage.getGPSValueFromAccelDataIndex(false, 0);

        LatLng defaultMapLocation = new LatLng(displayedLat, displayedLong);

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultMapLocation, gpsDefaultZoom));
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
            case setOneLatAccel:
                addGraphSeries(dataStorage.Axis.LatSetOne, dataStorage.RecordType.acceleration);
                break;
            case setOneLongAccel:
                addGraphSeries(dataStorage.Axis.LongSetOne, dataStorage.RecordType.acceleration);
                break;
            case setTwoLatAccel:
                addGraphSeries(dataStorage.Axis.LatSetTwo, dataStorage.RecordType.acceleration);
                break;
            case setTwoLongAccel:
                addGraphSeries(dataStorage.Axis.LongSetTwo, dataStorage.RecordType.acceleration);
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

        for (int counter = 0; counter < dataStorage.dataArrayLen; counter++)
        {
            if ((dataStorage.dataArrayLen - 1) < counter)
            {
                break;
            }

            newVal = dataStorage.getSensorValue(axis, recordType, counter);
            newSeries.appendData(new DataPoint(counter, newVal), false, dataStorage.dataArrayLen);
        }

        GraphView graph = (GraphView)findViewById(R.id.graphDisplay);

        /*Check if the new data has a maximum larger than existing data.  If so, update the maximum Y value displayed on the graph*/
        currentMaxGraphY = (float)(graph.getViewport().getMaxY(true));
        maxAbsValue = dataStorage.getMaxOfAbsValue(axis, recordType);

        if (maxAbsValue > currentMaxGraphY)
        {
            graph.getViewport().setMinY((-1.0)*maxAbsValue);
            graph.getViewport().setMaxY(maxAbsValue);
        }

        graph.addSeries(newSeries);

        /*Set series-specific graph elements, colors from https://www.rapidtables.com/web/color/red-color.html*/

        switch (axis)
        {
            case X:
                switch (recordType)
                {
                    case acceleration:
                        newSeries.setTitle("X Acceleration");
                        newSeries.setColor(0xFFFFFF00); //yellow
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

            case LatSetOne:
                newSeries.setTitle("Lateral Acceleration Set 1");
                newSeries.setColor(0xFFFF0000); //red
                break;

            case LongSetOne:
                newSeries.setTitle("Longitudal Acceleration Set 1");
                newSeries.setColor(0xFFFF6347); //tomato
                break;

            case LatSetTwo:
                newSeries.setTitle("Lateral Acceleration Set 2");
                newSeries.setColor(0xFF4169E1); //royalblue
                break;

            case LongSetTwo:
                newSeries.setTitle("Longitudal Acceleration Set 2");
                newSeries.setColor(0xFF6495ED); //cornflowerblue
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
