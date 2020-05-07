package com.example.googlelogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class Main2Activity extends AppCompatActivity {

    Button viewButton, camButton, signOut;
    GoogleSignInClient mGoogleSignInClient;
    //child ID to delete user from database once they sign out
    String theChildID;
    String universal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        viewButton = findViewById(R.id.theViewer);
        camButton = findViewById(R.id.theCamera);
        signOut = findViewById(R.id.signOut);

        //get child id to delete user from database once signed out
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            theChildID = extras.getString("key");
            universal = extras.getString("email");
        }

        Toast.makeText(Main2Activity.this, universal ,Toast.LENGTH_SHORT).show();
        Toast.makeText(Main2Activity.this, theChildID ,Toast.LENGTH_SHORT).show();









        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);





        //When user wants to be A viewer Will open Viewer Activity
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent viewerIntent = new Intent(Main2Activity.this, Main3Activity.class);
                viewerIntent.putExtra("email",universal);
                startActivity(viewerIntent);

            }

        });






        //When User wants to be a camera
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent viewerIntent = new Intent(Main2Activity.this, Main4Activity.class);
                // pass the user id key in to camera page
                viewerIntent.putExtra("key2",theChildID);
                startActivity(viewerIntent);



            }
        });




        //sign out button
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                switch (v.getId()) {
                    // ...
                    case R.id.signOut:
                        signOut();
                        break;
                    // ...
                }


            }
        });


    }


    //sign out function
    private void signOut() {
        //get data base refrence using child id
        DatabaseReference theUser = FirebaseDatabase.getInstance().getReference("Users").child(theChildID);
        //delete child from database
        theUser.removeValue();
        
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut();
        Intent viewerIntent = new Intent(Main2Activity.this, MainActivity.class);
        startActivity(viewerIntent);

    }




}

