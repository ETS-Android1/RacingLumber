package com.example.racinglumber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
//import android.R;///////////debug

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class graphActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener , AdapterView.OnItemSelectedListener {
    LineGraphSeries<DataPoint> series = new LineGraphSeries();
    private BottomNavigationView bottomNavigationView;
    private dataStorage recordedVars;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_graph_button);

        //Array adapter and onclick listener for graph datatype selection spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.graphDatatypesArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.graphDataSpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        recordedVars = new dataStorage();

        GraphView graph = (GraphView)findViewById(R.id.graphDisplay);

        graph.getViewport().setYAxisBoundsManual(true);

//        float maxYValue = recordedVars.getMaxOfAbsValue(dataStorage.Axis.X, dataStorage.RecordType.acceleration);
        graph.getViewport().setMinY(-0.05); //tiny default min max
        graph.getViewport().setMaxY(0.05);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
    }

    //////////////////////////User Interface Functions//////////////////////////

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
            case 0: //<item>Choose dataset to display</item>
                break; //do nothing
            case 1: //<item>X Acceleration</item>
                addGraphSeries(dataStorage.Axis.X, dataStorage.RecordType.acceleration);
                break;
            case 2: //<item>Y Acceleration</item>
                addGraphSeries(dataStorage.Axis.Y, dataStorage.RecordType.acceleration);
                break;
            case 3: //<item>Z Acceleration</item>
                addGraphSeries(dataStorage.Axis.Z, dataStorage.RecordType.acceleration);
                break;
            case 4: //<item>Acceleration Magnitude</item>
                addGraphSeries(dataStorage.Axis.Magnitude, dataStorage.RecordType.acceleration);
                break;
            case 5: //<item>X Rotation</item>
                addGraphSeries(dataStorage.Axis.X, dataStorage.RecordType.rotation);
                break;
            case 6: //<item>Y Rotation</item>
                addGraphSeries(dataStorage.Axis.Y, dataStorage.RecordType.rotation);
                break;
            case 7: //<item>Z Rotation</item>
                addGraphSeries(dataStorage.Axis.Z, dataStorage.RecordType.rotation);
                break;
            case 8: //<item>Rotation Magnitude</item>
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

    //////////////////////////Graphing Functions//////////////////////////

    private void addGraphSeries(dataStorage.Axis axis, dataStorage.RecordType recordType)
    {
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

            newVal = recordedVars.getValue(axis, recordType, counter);
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
    }
}
