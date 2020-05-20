package com.example.googlelogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


//User chooses to be a camera or viewer. Passes email to viewer view. Passes database childId and email to camera view.

public class Main2Activity extends AppCompatActivity {

    Button viewButton, camButton, signOut, picButton;
    GoogleSignInClient mGoogleSignInClient;
    //child ID to delete user from database once they sign out
    String theChildID;
    String universal;
    Boolean timer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        viewButton = findViewById(R.id.theViewer);
        camButton = findViewById(R.id.theCamera);
        signOut = findViewById(R.id.signOut);
        picButton = findViewById(R.id.pictureButton);
        timer = false;

        //get child id to delete user from database once signed out
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            theChildID = extras.getString("key");
            universal = extras.getString("email");
        }



        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //agreement to user app correctly for sercurity purposes alert
        AlertDialog willUseForGood = new AlertDialog.Builder(Main2Activity.this)
                .setTitle("I will use this app for security purposes")
                .setMessage("Please agree that you will not use Insight Security for any illegal purpose. You are responsible for obeying the laws and privacy protection requirements of your region.")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        Toast.makeText(Main2Activity.this, "Welcome "+ universal ,Toast.LENGTH_SHORT).show();
                        Toast.makeText(Main2Activity.this, "Viewer -> View Your Security Images" ,Toast.LENGTH_LONG).show();
                        Toast.makeText(Main2Activity.this, "Camera -> Turn your device into a security system.", Toast.LENGTH_LONG).show();
                        timer = false;
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                    }
                })
                .show();


        //When user wants to be A viewer Will open Viewer Activity
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent viewerIntent = new Intent(Main2Activity.this, ImagesActivity.class);
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
                viewerIntent.putExtra("key3",universal);
                viewerIntent.putExtra("theTimer",timer);
                startActivity(viewerIntent);



            }
        });

        //take picture button with a trigger
        //for testing on emulator purposes
        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntet = new Intent(Main2Activity.this, takePictureActivity.class);
                newIntet.putExtra("key3",universal);
                newIntet.putExtra("key2", theChildID);
                newIntet.putExtra("theTimer",timer);
                startActivity(newIntet);
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

