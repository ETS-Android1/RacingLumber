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
    private final int graphLineThickness = 10;

    /*Spinner menu items*/
    private final int noSelection = 0;
    private final int setOneLatAccel = 1;
    private final int setOneLongAccel = 2;
    private final int setTwoLatAccel = 3;
    private final int setTwoLongAccel = 4;

    /*Google map vars*/
    private float gpsDefaultZoom = 20.0F;

    /*graph lists*/
    private LineGraphSeries<DataPoint> latOneSeries = new LineGraphSeries();
    private boolean latOneOnGraph = false;
    private LineGraphSeries<DataPoint> longOneSeries = new LineGraphSeries();
    private boolean longOneOnGraph = false;
    private int setOneGraphOffset = 0;

    private LineGraphSeries<DataPoint> latTwoSeries = new LineGraphSeries();
    private boolean latTwoOnGraph = false;
    private LineGraphSeries<DataPoint> longTwoSeries = new LineGraphSeries();
    private boolean longTwoOnGraph = false;
    private int setTwoGraphOffset = 0;

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
            updateColor(R.id.left3Button);
            scrollSeries(-50);
        }
        else if (v.getId() == R.id.left2Button)
        {
            updateColor(R.id.left2Button);
            scrollSeries(-10);
        }
        else if (v.getId() == R.id.left1Button)
        {
            updateColor(R.id.left1Button);
            scrollSeries(-1);
        }
        else if (v.getId() == R.id.right1Button)
        {
            updateColor(R.id.right1Button);
            scrollSeries(1);
        }
        else if (v.getId() ==  R.id.right2Button)
        {
            updateColor(R.id.right2Button);
            scrollSeries(10);
        }
        else// v.getId() == R.id.right3Button
        {
            updateColor(R.id.right3Button);
            scrollSeries(50);
        }
    }

    private void updateColor(int buttonID)
    {
        /*Color selected button and clear other buttons*/
        Button left3 = (Button) findViewById(R.id.left3Button);
        Button left2 = (Button) findViewById(R.id.left2Button);
        Button left1 = (Button) findViewById(R.id.left1Button);
        Button right1 = (Button) findViewById(R.id.right1Button);
        Button right2 = (Button) findViewById(R.id.right2Button);
        Button right3 = (Button) findViewById(R.id.right3Button);

        left3.setBackgroundColor(Color.LTGRAY);
        left2.setBackgroundColor(Color.LTGRAY);
        left1.setBackgroundColor(Color.LTGRAY);
        right1.setBackgroundColor(Color.LTGRAY);
        right2.setBackgroundColor(Color.LTGRAY);
        right3.setBackgroundColor(Color.LTGRAY);

        switch (buttonID)
        {
            case R.id.left3Button:
                left3.setBackgroundColor(Color.GREEN);
                break;

            case R.id.left2Button:
                left2.setBackgroundColor(Color.GREEN);
                break;

            case R.id.left1Button:
                left1.setBackgroundColor(Color.GREEN);
                break;

            case R.id.right1Button:
                right1.setBackgroundColor(Color.GREEN);
                break;

            case R.id.right2Button:
                right2.setBackgroundColor(Color.GREEN);
                break;

            case R.id.right3Button:
                right3.setBackgroundColor(Color.GREEN);
                break;
        }
    }
    ////////////.>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    ////////////.>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    ////////////.>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private void scrollSeries(int offset){
        boolean updateSetOne = false;
        boolean updateSetTwo = false;

        boolean latOneSeriesNull = false;
        boolean longOneSeriesNull = false;
        boolean latTwoSeriesNull = false;
        boolean longTwoSeriesNull = false;

        float newValLat;
        float newValLong;

        if ((dataStorage.selectedSet == dataStorage.SelectedSet.setOne) || (dataStorage.selectedSet == dataStorage.SelectedSet.setOneTwo))
        {
            setOneGraphOffset += offset;
            updateSetOne = true;
        }

        if ((dataStorage.selectedSet == dataStorage.SelectedSet.setTwo) || (dataStorage.selectedSet == dataStorage.SelectedSet.setOneTwo))
        {
            setTwoGraphOffset += offset;
            updateSetTwo = true;
        }

        //////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //Todo this assumes both lat and long are already displayed

        GraphView graph = (GraphView)findViewById(R.id.graphDisplay);

        if (updateSetOne)
        {
            if (latOneOnGraph)
            {
                graph.getSeries().remove(latOneSeries);
                latOneSeries = new LineGraphSeries();
            }
            else
            {
                latOneSeriesNull = true;
            }

            if (longOneOnGraph)
            {
                graph.getSeries().remove(longOneSeries);
                longOneSeries = new LineGraphSeries();
            }
            else
            {
                longOneSeriesNull = true;
            }

            for (int counter = setOneGraphOffset; counter < dataStorage.dataArrayLen; counter++)
            {
                if ((dataStorage.dataArrayLen - 1) < counter)
                {
                    break;
                }

                if (counter >= 0)
                {
                    newValLat = dataStorage.getSensorValue(dataStorage.Axis.LatSetOne, dataStorage.RecordType.acceleration, counter);
                    newValLong = dataStorage.getSensorValue(dataStorage.Axis.LongSetOne, dataStorage.RecordType.acceleration, counter);
                }
                else
                {
                    newValLat = 0.0f;
                    newValLong = 0.0f;
                }

                if (!latOneSeriesNull)
                {
                    latOneSeries.appendData(new DataPoint((counter-setOneGraphOffset), newValLat), false, dataStorage.dataArrayLen);
                }

                if (!longOneSeriesNull)
                {
                    longOneSeries.appendData(new DataPoint((counter-setOneGraphOffset), newValLong), false, dataStorage.dataArrayLen);
                }
            }

            if (!latOneSeriesNull)
            {
                graph.addSeries(latOneSeries);

                latOneSeries.setTitle("Lateral Acceleration Set 1");
                latOneSeries.setColor(0xFFFF0000); //red
                latOneSeries.setThickness(graphLineThickness);
            }

            if (!longOneSeriesNull)
            {
                graph.addSeries(longOneSeries);

                longOneSeries.setTitle("Longitudal Acceleration Set 1");
                longOneSeries.setColor(0xFFFF6347); //tomato
                longOneSeries.setThickness(graphLineThickness);
            }
        }

        /////TODO NEXT fix bugs for above then make update set two work
        //if (updateSetTwo) {}
        //////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        //////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        //////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    }
    ///////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    ///////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    ///////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

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
                latOneOnGraph = true;//todo make this a toggle for both this boolean and the series
                break;
            case setOneLongAccel:
                addGraphSeries(dataStorage.Axis.LongSetOne, dataStorage.RecordType.acceleration);
                longOneOnGraph = true;//todo make this a toggle for both this boolean and the series
                break;
            case setTwoLatAccel:
                addGraphSeries(dataStorage.Axis.LatSetTwo, dataStorage.RecordType.acceleration);
                latTwoOnGraph = true;//todo make this a toggle for both this boolean and the series
                break;
            case setTwoLongAccel:
                addGraphSeries(dataStorage.Axis.LongSetTwo, dataStorage.RecordType.acceleration);
                longTwoOnGraph = true;//todo make this a toggle for both this boolean and the series
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
        LineGraphSeries<DataPoint> newSeries = new LineGraphSeries();
        float newVal;
        float currentMaxGraphY;
        float maxAbsValue;

        /////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        /////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        /////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        switch (axis)
        {
            case LatSetOne:
                latOneSeries = new LineGraphSeries();
                break;

            case LongSetOne:
                longOneSeries = new LineGraphSeries();
                break;

            case LatSetTwo:
                latTwoSeries = new LineGraphSeries();
                break;

            case LongSetTwo:
                longTwoSeries = new LineGraphSeries();
                break;

            default:
                break;
        }
        ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        for (int counter = 0; counter < dataStorage.dataArrayLen; counter++)
        {
            if ((dataStorage.dataArrayLen - 1) < counter)
            {
                break;
            }

            newVal = dataStorage.getSensorValue(axis, recordType, counter);
            /////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            /////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            /////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            switch (axis)
            {
                case LatSetOne:
                    latOneSeries.appendData(new DataPoint(counter, newVal), false, dataStorage.dataArrayLen);
                    break;

                case LongSetOne:
                    longOneSeries.appendData(new DataPoint(counter, newVal), false, dataStorage.dataArrayLen);
                    break;

                case LatSetTwo:
                    latTwoSeries.appendData(new DataPoint(counter, newVal), false, dataStorage.dataArrayLen);
                    break;

                case LongSetTwo:
                    longTwoSeries.appendData(new DataPoint(counter, newVal), false, dataStorage.dataArrayLen);
                    break;

                default:
                    break;
            }
            ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            //newSeries.appendData(new DataPoint(counter, newVal), false, dataStorage.dataArrayLen);
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

        switch (axis)
        {
            case LatSetOne:
                graph.addSeries(latOneSeries);
                break;

            case LongSetOne:
                graph.addSeries(longOneSeries);
                break;

            case LatSetTwo:
                graph.addSeries(latTwoSeries);
                break;

            case LongSetTwo:
                graph.addSeries(longTwoSeries);
                break;

            default:
                break;
        }

        /*Set series-specific graph elements, colors from https://www.rapidtables.com/web/color/red-color.html*/

        switch (axis)
        {
            case LatSetOne:
                latOneSeries.setTitle("Lateral Acceleration Set 1");
                latOneSeries.setColor(0xFFFF0000); //red
                latOneSeries.setThickness(graphLineThickness);
                break;

            case LongSetOne:
                longOneSeries.setTitle("Longitudal Acceleration Set 1");
                longOneSeries.setColor(0xFFFF6347); //tomato
                longOneSeries.setThickness(graphLineThickness);
                break;

            case LatSetTwo:
                latTwoSeries.setTitle("Lateral Acceleration Set 2");
                latTwoSeries.setColor(0xFF4169E1); //royalblue
                latTwoSeries.setThickness(graphLineThickness);
                break;

            case LongSetTwo:
                longTwoSeries.setTitle("Longitudal Acceleration Set 2");
                longTwoSeries.setColor(0xFF6495ED); //cornflowerblue
                longTwoSeries.setThickness(graphLineThickness);
                break;

            default:
                break;
        }

        if (!graph.getLegendRenderer().isVisible())
        {
            graph.getLegendRenderer().setVisible(true);
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        }
    }
}
