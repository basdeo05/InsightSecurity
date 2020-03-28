package com.example.googlelogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main4Activity extends AppCompatActivity {

    //variables
    private Button theButton;
    private TextView theTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        //connect to variables
        theButton = findViewById(R.id.theButton);
        theTitle = findViewById(R.id.title);


        //when click change text view and start and stop recording
        theButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get the title content
                String value = theTitle.getText().toString();
                String camera = "Camera";




                if (camera.equals(value)){
                    theTitle.setText("Listening:");
                    theButton.setText("Stop Listening");
                    //call the on start method

                }
                else {
                theTitle.setText("Camera");
                theButton.setText("Enter Camera Mode");
                //call the stop method
                }
            }


        });

    }






























}
