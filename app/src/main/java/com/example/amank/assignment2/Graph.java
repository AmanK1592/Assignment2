package com.example.amank.assignment2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Graph extends AppCompatActivity {

    private Button btnRun;
    private Button btnStop;
    private LineGraphSeries<DataPoint> Series;
    private static final Random RANDOM = new Random();
    private int lastX=0;
    private Spinner PatientList_dropdown;
    private List<String> PatientList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        btnRun = (Button) findViewById(R.id.btnRun);
        btnStop = (Button) findViewById(R.id.btnStop);
        final GraphView graph = (GraphView) findViewById(R.id.graph);
        Series = new LineGraphSeries<DataPoint>();

        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time");
        gridLabel.setVerticalAxisTitle("Amplitude");


        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(10);
        viewport.setScrollable(true);

        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(10);
        viewport.setScrollable(true);





        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                graph.addSeries(Series);
                //Plotting a continuous real time graph on clicking the Run button

            }

        });



        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                graph.removeAllSeries();
                // Clearing the running graph on clicking the Stop button

            }
        });

        super.onBackPressed();
    }

    @Override
    protected void onResume(){
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)

                {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // manage error ...
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    // adding random data to graph
    private void addEntry() {
        //com.jjoe64.graphview.series.Series(new DataPoint(lastX++, RANDOM.nextDouble() * 10d), true, 10);
    }

}
