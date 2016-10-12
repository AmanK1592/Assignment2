package com.example.amank.assignment2;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public static String tableName;

    EditText patientID;
    EditText patientAge;
    EditText patientName;


    //if no user input then default values for table name in database
    String patientIDText = "ID";
    String patientAgeText = "24";
    String patientSexText = "male";
    String patientNameText = "name";

    RadioGroup radioButtonGroup;
    RadioButton r;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);


        patientID = (EditText) findViewById(R.id.patientID);
        patientAge = (EditText) findViewById(R.id.patientAge);
        patientName = (EditText) findViewById(R.id.patientName);
        btnNext = (Button) findViewById(R.id.btnNext);
        radioButtonGroup = (RadioGroup) findViewById(R.id.sex);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //Getting data from the text fields

                    int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
                    View radioButton = radioButtonGroup.findViewById(radioButtonID);
                    int idx = radioButtonGroup.indexOfChild(radioButton);
                    r = (RadioButton) radioButtonGroup.getChildAt(idx);
                    patientSexText = r.getText().toString();
                    patientIDText = patientID.getText().toString();
                    patientAgeText = patientAge.getText().toString();
                    patientNameText = patientName.getText().toString();
                } catch (Exception e) {

                }
                tableName = patientNameText + "_" + patientIDText + "_" + patientAgeText + "_" + patientSexText;
                if (tableName.equals("___male")) {
                    tableName = "DefaultDatabase";
                }


                try {

                    Intent intent = new Intent(getApplicationContext(), Graph.class);
                    intent.putExtra("table_name", tableName);
                    startActivity(intent);
                } catch (SQLException e) {
                    Log.i("Error: ", e.getMessage());
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}

