package com.example.googlelogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.media.MediaRecorder;
import android.widget.Toast;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Camera mode activity. As soon you get to this view start listeing for decibels.
// User can choose timer mode as well. Whichever event happens first will be sent to take picture activity.
// Trigger noiseEvent to true with child ID passed to this view. So notification can be sent to all phones.

public class Main4Activity extends AppCompatActivity {

    //variables
    private Button theButton;
    private TextView theTitle;
    private TextView mStatusView;
    private MediaRecorder mRecorder;
    private Thread runner;
    private TextView dBAverage;
    private TextView spike;
    private double referenceAmp = 10.0;
    private double lastNoiseLevel = 64.0;
    Button exit;
    View thisView;
    Boolean timer;
    Button startTheTimer;
    public int counter = 0;

    public boolean noiseSpike = false;
    String theChildID;
    String universal;

    final Runnable updater = new Runnable(){

        public void run(){
            updateTv();
        };
    };
    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        //connect to variables
        theButton = findViewById(R.id.theButton);
        theTitle = findViewById(R.id.title);
        mStatusView = (TextView)findViewById(R.id.status);
        dBAverage = (TextView)findViewById(R.id.average);
        spike = (TextView)findViewById(R.id.spike);
        exit = findViewById(R.id.goHome);
        thisView = findViewById(R.id.theView);
        startTheTimer = findViewById(R.id.startTimer);

        //get child id to notify user once noise event
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            theChildID = extras.getString("key2");
            universal = extras.getString("key3");
            timer = extras.getBoolean("theTimer");
/*
            make sure child id was grabbed and not null
            Toast.makeText(Main4Activity.this, universal ,Toast.LENGTH_SHORT).show();
            Toast.makeText(Main4Activity.this, theChildID ,Toast.LENGTH_SHORT).show();
*/
        }


        if (timer == true){
            startTheTimer.setText("Stop Timer");
            changeColorRed();

        }
        else {
            startTheTimer.setText("Start Timer");
            changeColorBlack();
        }



        startTheTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer == true){
                    timer = false;
                    startTheTimer.setText("Start Timer");
                    changeColorBlack();

                }
                else {
                    timer = true;
                    startTheTimer.setText("Stop Timer");
                    changeColorRed();
                }

                timerConfig();
            }
        });

        timerConfig();



        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent afterLogin = new Intent(Main4Activity.this, Main2Activity.class);
                afterLogin.putExtra("key",theChildID);
                afterLogin.putExtra("email", universal);
                startActivity(afterLogin);
            }
        });




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
                    changeColorRed();
                    //call the on start method
                    onResume();


                }
                else {
                theTitle.setText("Camera");
                theButton.setText("Enter Camera Mode");
                changeColorBlack();
                //call the stop method
                    stopRecorder();
                }
            }


        });

        if (runner == null)
        {
            runner = new Thread(){
                public void run()
                {
                    while (runner != null)
                    {
                        try
                        {
                            Thread.sleep(1000);
                            Log.i("Noise", "Tock");
                        } catch (InterruptedException e) { };
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }
    }

    public void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }

        else {
            startRecorder();

        }

    }

    public void onPause()
    {
        super.onPause();
        stopRecorder();
    }

    public void startRecorder(){
        if (mRecorder == null)
        {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try
            {
                mRecorder.prepare();
            }catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " +
                        android.util.Log.getStackTraceString(ioe));

            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
            try
            {
                mRecorder.start();
            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }

        }

    }
    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void updateTv(){
        //mStatusView.setText(Double.toString((soundDb(referenceAmp))) + " dB");
        mStatusView.setText( "Current Level: " + String.format("%.1f", soundDb())+ " dB");
        dBAverage.setText("");
        // dBAverage.setText("Average:" + String.format("%.1f", avgOfDbArray)+ " dB");
        checkForNoiseSpike();
        if (noiseSpike == true){
            spike.setText("Noise spike: true");
        }
        else spike.setText("Noise spike: false");
    }


    public double soundDb(){
        return  20 * Math.log10(getAmplitude() / referenceAmp);
    }


    public void checkForNoiseSpike(){
       if(soundDb() > 65.0 && soundDb() > lastNoiseLevel*1.65){
           noiseSpike = true;
           //get data base reference using child id
           DatabaseReference theUserNoiseEventValue = FirebaseDatabase.getInstance().getReference("Users").child(theChildID).child("noiseEvent");
           //update child from database
           theUserNoiseEventValue.setValue(true);

           Intent viewerIntent = new Intent(Main4Activity.this, takePictureActivity.class);
           // pass the user id key in to camera page
           viewerIntent.putExtra("key2",theChildID);
           viewerIntent.putExtra("key3",universal);
           viewerIntent.putExtra("theTimer",timer);
           startActivity(viewerIntent);


       } else {
           noiseSpike = false;
       }
       // grab current level of noise for use calculating later noise events
       lastNoiseLevel = soundDb();
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;

    }

    void timerConfig (){
        if(timer == true){

            new CountDownTimer(30000, 1000){
                public void onTick(long millisUntilFinished){
                    mStatusView.setText(String.valueOf(counter));
                    counter++;
                }
                public  void onFinish(){

                    Intent viewerIntent = new Intent(Main4Activity.this, takePictureActivity.class);
                    // pass the user id key in to camera page
                    viewerIntent.putExtra("key2",theChildID);
                    viewerIntent.putExtra("key3",universal);
                    viewerIntent.putExtra("theTimer",timer);
                    startActivity(viewerIntent);

                }
            }.start();

        }
    }



    void changeColorBlack (){

        //thisView.setBackgroundColor(Color.WHITE);
        //theTitle.setTextColor(Color.BLACK);
        theButton.setTextColor(Color.BLACK);
        //mStatusView.setTextColor(Color.BLACK);
        //dBAverage.setTextColor(Color.BLACK);
        //spike.setTextColor(Color.BLACK);
        //exit.setTextColor(Color.BLACK);
        //startTheTimer.setTextColor(Color.BLACK);

    }

    void changeColorRed (){

        thisView.setBackgroundColor(Color.BLACK);
        theTitle.setTextColor(Color.RED);
        theButton.setTextColor(Color.RED);
        mStatusView.setTextColor(Color.RED);
        dBAverage.setTextColor(Color.RED);
        spike.setTextColor(Color.RED);
        exit.setTextColor(Color.RED);
        startTheTimer.setTextColor(Color.RED);

    }


}































