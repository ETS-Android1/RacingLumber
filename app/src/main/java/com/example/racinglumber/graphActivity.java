package com.example.racinglumber;

import android.app.Activity;
import android.os.Bundle;

//import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.series.DataPoint;
//import com.jjoe64.graphview.series.LineGraphSeries;

public class graphActivity extends Activity {
//    GraphView graph = (GraphView)findViewById(R.id.graph);
//    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
//            new DataPoint(0, 1),
//            new DataPoint(1, 5),
//            new DataPoint(2, 3),
//            new DataPoint(3, 2),
//            new DataPoint(4, 6)
//    });


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

//        graph.addSeries(series);

        setContentView(R.layout.activity_graph);


    }
}
