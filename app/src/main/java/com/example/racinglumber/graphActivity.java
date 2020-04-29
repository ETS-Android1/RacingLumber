package com.example.racinglumber;

import android.app.Activity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class graphActivity extends Activity {
//    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
//            new DataPoint(0, dataStorage.xDataArray[0]),
//            new DataPoint(1, dataStorage.xDataArray[1]),
//            new DataPoint(2, dataStorage.xDataArray[2]),
//            new DataPoint(3, dataStorage.xDataArray[3]),
//            new DataPoint(4, dataStorage.xDataArray[4])
//    });
    LineGraphSeries<DataPoint> series = new LineGraphSeries();


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        GraphView graph = (GraphView)findViewById(R.id.graph);

        //add series of data
        for (int counter = 0; counter < 15; counter++)
        {
            series.appendData(new DataPoint(counter, dataStorage.xDataArray[counter]), false, 15);
        }
        //DataPoint newData2 = new DataPoint(1, 2);;
        //series.appendData(newData2, false, 10);

        graph.addSeries(series);
    }
}
