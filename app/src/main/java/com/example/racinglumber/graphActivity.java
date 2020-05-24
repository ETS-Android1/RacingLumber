package com.example.racinglumber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class graphActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener {
    LineGraphSeries<DataPoint> series = new LineGraphSeries();
    private BottomNavigationView bottomNavigationView;
    private dataStorage recordedVars;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        float newVal;
        float maxYValue;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        GraphView graph = (GraphView)findViewById(R.id.graphTop);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_graph_button);

        recordedVars = new dataStorage();

        //add series of data
        for (int counter = 0; counter < recordedVars.dataArrayLen; counter++)
        {
            if ((recordedVars.dataArrayLen - 1) < counter)
            {
                break;
            }

            newVal = recordedVars.getValue(dataStorage.Axis.X, dataStorage.RecordType.acceleration, counter);
            series.appendData(new DataPoint(counter, newVal), false, recordedVars.dataArrayLen);
        }

        graph.getViewport().setYAxisBoundsManual(true);

        maxYValue = recordedVars.getMaxOfAbsValue(dataStorage.Axis.X, dataStorage.RecordType.acceleration);
        graph.getViewport().setMinY((-1.0)*maxYValue);
        graph.getViewport().setMaxY(maxYValue);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.addSeries(series);
    }

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
}
