package com.example.googlelogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Main2Activity extends AppCompatActivity {

    Button viewButton, camButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        viewButton = findViewById(R.id.theViewer);
        camButton = findViewById(R.id.theCamera);








        //When user wants to be A viewer Will open Viewer Activity
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent viewerIntent = new Intent(Main2Activity.this, Main3Activity.class);
                startActivity(viewerIntent);

            }

        });





        //When User wants to be a camera
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent viewerIntent = new Intent(Main2Activity.this, Main4Activity.class);
                startActivity(viewerIntent);




            }
        });


    }

    }

