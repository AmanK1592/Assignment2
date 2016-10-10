package com.example.amank.assignment2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends AppCompatActivity
{
    SQLiteDatabase db;
    EditText patientID;
    EditText patientAge;
    EditText patientName;
    String patientIDText;
    String patientAgeText;
    String patientSexText;
    String patientNameText;
    RadioGroup radioButtonGroup;
    RadioButton r;
    Button btnRegister;
    Button btnAlreadyRegister;
    private static final int REQUEST_WRITE_STORAGE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
            while (!hasPermission)
            {
                hasPermission = (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            }
        }

        setContentView(R.layout.content_main);

        patientID = (EditText) findViewById(R.id.patientID);
        patientAge = (EditText) findViewById(R.id.patientAge);
        patientName = (EditText)findViewById(R.id.patientName);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnAlreadyRegister = (Button)findViewById(R.id.btnAlreadyRegister);
        radioButtonGroup = (RadioGroup) findViewById(R.id.sex);

        try{
            //create the database in external storage of smart phone
            db = SQLiteDatabase.openOrCreateDatabase( Environment.getExternalStorageDirectory()+ File.separator+ "myDB", null);
            db.beginTransaction();
        }
        catch (SQLException e)
        {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally
        {
            //Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG).show();
            db.endTransaction();
        }


        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
                View radioButton = radioButtonGroup.findViewById(radioButtonID);
                int idx = radioButtonGroup.indexOfChild(radioButton);
                r = (RadioButton)radioButtonGroup.getChildAt(idx);
                patientSexText = r.getText().toString();
                patientIDText = patientID.getText().toString();
                patientAgeText = patientAge.getText().toString();
                patientNameText = patientName.getText().toString();


                try
                {
                    String tableName = patientNameText + "_" + patientIDText +"_" + patientAgeText + "_" + patientSexText;
                    tableName = tableName.trim();
                    if(!isTableExists(db,tableName))
                    {
                        try
                        {
                            db.beginTransaction();
                            db.execSQL("create table "+ tableName +"("
                                    + " recID integer PRIMARY KEY autoincrement, "
                                    + " Xaxis text, "
                                    + " Yaxis text, "
                                    + " Zaxis text, "
                                    + " timestamp text ); " );
                            db.setTransactionSuccessful();
                        }
                        catch (SQLiteException e)
                        {
                            Log.i("Error: ",e.getMessage());
                            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                        finally
                        {
                            Toast.makeText(MainActivity.this, tableName, Toast.LENGTH_LONG).show();
                            db.endTransaction();
                        }
                    }
                    Intent intent = new Intent(MainActivity.this, Graph.class);
                    startActivity(intent);
                }
                catch (SQLException e)
                {
                    Log.i("Error: ",e.getMessage());
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        btnAlreadyRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, Graph.class);
                startActivity(intent);
            }
        });
    }

    protected void onStart() {

        super.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    try{
                        //create the database in external storage of smart phone
                        db = SQLiteDatabase.openOrCreateDatabase( Environment.getExternalStorageDirectory()+ File.separator+ "myDB", null);
                        db.beginTransaction();
                    }
                    catch (SQLException e)
                    {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else
                {
                    Toast.makeText(MainActivity.this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    boolean isTableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }
}
