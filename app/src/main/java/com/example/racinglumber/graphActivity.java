package com.example.racinglumber;

import android.app.Activity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class graphActivity extends Activity {
    LineGraphSeries<DataPoint> series = new LineGraphSeries();
    private dataStorage recordedVars;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        GraphView graph = (GraphView)findViewById(R.id.graph);

        recordedVars = new dataStorage();

        //add series of data
        for (int counter = 0; counter < 500; counter++)
        {
            series.appendData(new DataPoint(counter, recordedVars.xDataArray[counter]), false, 500);
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
    }
}
