package com.example.amank.assignment2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Graph extends AppCompatActivity implements SensorEventListener {
    //Database Initialization
    SQLiteDatabase db;
    private static final int REQUEST_WRITE_STORAGE = 112;
    public static final String  DATABASE_NAME = "Aman_Abhishek_Yogesh";

    private static String file_url = "https://impact.asu.edu/CSE535Fall16Folder/Aman_Abhishek_Yogesh";

    final String uploadFilePath = "/storage/emulated/0/";
    final String uploadFileName = "Aman_Abhishek_Yogesh";
    final String uploadServerUrl = "https://impact.asu.edu/CSE535Fall16Folder/";

    private Button btnRun;
    private Button btnStop;
    private Button btnUpload;
    private Button btnDownload;

    public static String  tableName;

    //Setting the axis labels
    private String[] vertical_labels = new String[]{"+10", "+8", "+6", "+4", "+2", "0", "-2", "-4", "-6", "-8", "-10"};
    private String[] horizontal_labels = new String[]{"0", "2", "4", "6", "8", "10"};

    //Initializing the graphview
    private GraphView graphView;
    private LinearLayout graph;

/////////////////Accelerometer data initialization////////////////////////////////////////////////////////////////////
    private float lastX =0, lastY=0, lastZ = 0;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    static int accelerometer_filter_time= 1000;
    long current_time= System.currentTimeMillis();

    float[] x  = new float[]{0,0,0,0,0,0,0,0,0,0}; //array to store x-axis data of accelerometer
    float[] y  = new float[]{0,0,0,0,0,0,0,0,0,0}; //array to store y-axis data of accelerometer
    float[] z  = new float[]{0,0,0,0,0,0,0,0,0,0}; //array to store z-axis data of accelerometer

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private TextView currentX, currentY, currentZ;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Checking the permissions of the application

        boolean hasPermission_EXTERNAL_STORAGE = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission_EXTERNAL_STORAGE) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
            while (!hasPermission_EXTERNAL_STORAGE) {
                hasPermission_EXTERNAL_STORAGE = (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            }
        }

//        boolean hasPermission_INTERNET = (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED);
//        if (!hasPermission_INTERNET) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.INTERNET},
//                    REQUEST_WRITE_STORAGE);
//            while (!hasPermission_INTERNET) {
//                hasPermission_EXTERNAL_STORAGE = (ContextCompat.checkSelfPermission(this,
//                        Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED);
//            }
//        }

        setContentView(R.layout.activity_graph);

        btnRun = (Button) findViewById(R.id.btnRun);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnDownload = (Button)findViewById(R.id.btnDownload);
/////////////////////////////////Initializing the Graphview///////////////////////////////////////////////////////////

        graph = (LinearLayout) findViewById(R.id.graph);
        graphView.flag=false;
        graphView = new GraphView(Graph.this, x, "Accelerometer Data", horizontal_labels, vertical_labels, GraphView.LINE);
        graphView.setValues(x,y,z);
        graph.addView(graphView);

/////////////////////////////Setting the accelerometer views and initialization///////////////////////////////////////

        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // fail! we don't have an accelerometer!
        }
////////////////////////////////////////////////////////////////////////////////////////////////


        try {
            //catch the table name sent by Main activity and store it in the string tableName
            tableName = getIntent().getStringExtra("table_name");

        }catch(Exception e) {
            Toast.makeText(Graph.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importdata();
                graph.removeView(graphView);

                for (int j=0;j<10;j++) {
                    System.out.print("  " + x[j]);
                    System.out.print("  " + y[j]);
                    System.out.println("  " + z[j]);
                }

                Toast.makeText(Graph.this, "Graph Updated", Toast.LENGTH_SHORT).show();
                graphView.flag = true;
                graphView.setValues(x,y,z);
                graph.addView(graphView);

            }

        });


        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(Graph.this, "Graph Cleared", Toast.LENGTH_SHORT).show();
                graph.removeView(graphView);
                graphView.flag = false;
                graph.addView(graphView);


            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UploadFiletoURL(Graph.this).execute(uploadFilePath, uploadFileName, uploadServerUrl);
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadFileFromURL(Graph.this).execute(file_url);
            }
        });

    }
///////////////////////////////Accelerometer Events//////////////////////////////////////////////////////////////////

    public void onSensorChanged(SensorEvent event) {

        if ((System.currentTimeMillis() - current_time) > accelerometer_filter_time) {
            current_time = System.currentTimeMillis();

            displayCleanValues();

            displayCurrentValues();

            // get the change of the x,y,z values of the accelerometer
            deltaX = Math.abs(lastX - event.values[0]);
            deltaY = Math.abs(lastY - event.values[1]);
            deltaZ = Math.abs(lastZ - event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
        //Log.d("LABEL","Time");

        String timeStamp = getCurrentTimeStamp();

/////////////////////////////////////////Inputting accelerometer values in the database////////////////////////////////////////////////
        try
        {
            db.beginTransaction();

            db.execSQL("INSERT INTO "+tableName+" (Xaxis, Yaxis, Zaxis, timestamp) VALUES ('" + Float.toString(deltaX) + "', '" + Float.toString(deltaY) + "', '" + Float.toString(deltaZ) + "', '" + timeStamp + "');");

            db.setTransactionSuccessful();
        }
        catch (SQLiteException e)
        {
            Log.i("Error: ",e.getMessage());

        }
        finally
        {
            db.endTransaction();
        }


    }


/////////////////////////////////////////Creating a database/////////////////////////////////////////////////////////////////////////////



    protected void onStart() {
        try {

            //create the database in external storage of smart phone
            db = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory() + File.separator + DATABASE_NAME, null);
            db.beginTransaction();

            try {

                db.execSQL("create table "+ tableName +"("
                                    + " recID integer PRIMARY KEY autoincrement, "
                                    + " Xaxis text, "
                                    + " Yaxis text, "
                                    + " Zaxis text, "
                                    + " timestamp text ); " );
                            db.setTransactionSuccessful();
            } catch (SQLException e) {
                Toast.makeText(Graph.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                //Log.i("DATABASE ERROR : ",e.getMessage());
            } finally {
                //Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG).show();
                db.endTransaction();
            }


        }catch (SQLException e){
            Log.i("DATABASE ERROR",e.getMessage());

            Toast.makeText(Graph.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        super.onStart();
    }

///////////////////////////Function to get the current time and date/////////////////////////////////////////////////
    public static String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find today's date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

////////////////////////////Getting the 10 recent readings of accelerometer from the database/////////////////////////

    public int importdata() {

        Cursor cursor = null;
        String selectQuery = "SELECT  * FROM " + tableName + " ORDER BY timestamp DESC";
        try {
            db.beginTransaction();
            cursor = db.rawQuery(selectQuery, null);
            db.setTransactionSuccessful(); //commit your changes
        } catch (Exception e) {
            System.out.print("Error is " + e);
            //report problem
        }finally {
            db.endTransaction();
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
            Toast.makeText(Graph.this, "Please wait 10 secs for database to Update!!", Toast.LENGTH_SHORT).show();

        }

        return 0;
    }
}

