package com.example.amank.assignment2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Graph extends AppCompatActivity {

    SQLiteDatabase db;

    private Button btnRun;
    private Button btnStop;

    float[] x  = new float[10]; //array to store x-axis data of accelerometer
    float[] y  = new float[10]; //array to store y-axis data of accelerometer
    float[] z  = new float[10]; //array to store z-axis data of accelerometer


    //    private LineGraphSeries<DataPoint> Series;
//    private static final Random RANDOM = new Random();
//    private int lastX=0;
    private Spinner PatientList_dropdown;
    private List<String> PatientList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        btnRun = (Button) findViewById(R.id.btnRun);
        btnStop = (Button) findViewById(R.id.btnStop);
//        final GraphView graph = (GraphView) findViewById(R.id.graph);
//        Series = new LineGraphSeries<DataPoint>();

        db = SQLiteDatabase.openOrCreateDatabase( Environment.getExternalStorageDirectory()+ File.separator+ "myDB", null);
        db.beginTransaction();

        final GraphView graph = (GraphView) findViewById(R.id.graph);
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

                //graph.addSeries(Series);
                //Plotting a continuous real time graph on clicking the Run button
                Toast.makeText(Graph.this, "Graph Updated", Toast.LENGTH_SHORT).show();

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


    //method to obtain the current date and time information
    public static String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    //method to obtain the recent 10 readings of accelerometer from table inside the database
    public int inputData() {

        Cursor cursor = null;
        //String selectQuery = "SELECT  * FROM " + tableName + " ORDER BY timestamp DESC";
        try {
            //cursor = db.rawQuery(selectQuery, null);
            db.setTransactionSuccessful(); //commit your changes
        } catch (Exception e) {
            System.out.print("Error is " + e);
            //report problem
        }

        int i = 0;

        try {
            //put the data from database into x, y and z arrays
            if (cursor.moveToFirst()) {
                do {
                    // get the data into array, or class variable
                    x[i] = cursor.getFloat(cursor.getColumnIndex("Xaxis"));
                    y[i] = cursor.getFloat(cursor.getColumnIndex("Yaxis"));
                    z[i] = cursor.getFloat(cursor.getColumnIndex("Zaxis"));
                    i++;
                    cursor.moveToNext();
                    //count++;
                } while (i < 10);
            }
            cursor.close();
        } catch (Exception e) {
            x = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            y = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            z = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            Toast.makeText(Graph.this, "Please wait 10 secs for database to Update!!", Toast.LENGTH_LONG).show();

        }

        return 0;
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            float datapassedx = arg1.getFloatExtra("DATAPASSED1", 0);
            float datapassedy = arg1.getFloatExtra("DATAPASSED2", 0);
            float datapassedz = arg1.getFloatExtra("DATAPASSED3", 0);
//            Log.d("msg", "hi");
            String time = getCurrentTimeStamp();

            try {
                //insert the data into database
                //db.execSQL("insert into "+tableName+"(Xaxis, Yaxis, Zaxis, timestamp) values ('" + Float.toString(datapassedx) + "', '" + Float.toString(datapassedy) + "', '" + Float.toString(datapassedz) + "', '" + time + "' );");
            } catch (SQLiteException e) {
                //report problem
            } finally {
            }
        }
    }
}
