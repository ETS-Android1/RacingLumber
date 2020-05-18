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
    LineGraphSeries<DataPoint> seriesTESTTEST = new LineGraphSeries();

    private BottomNavigationView bottomNavigationView;
    private dataStorage recordedVars;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
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
            if ((recordedVars.xDataArray.length - 1) < counter)
            {
                break;
            }

            series.appendData(new DataPoint(counter, recordedVars.xDataArray[counter]), false, recordedVars.dataArrayLen);
            seriesTESTTEST.appendData(new DataPoint(counter, recordedVars.yDataArray[counter]), false, recordedVars.dataArrayLen);
        }

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-30);
        graph.getViewport().setMaxY(30);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.addSeries(series);
        graph.addSeries(seriesTESTTEST);
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
