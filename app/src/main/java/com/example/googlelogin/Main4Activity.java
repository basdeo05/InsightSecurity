package com.example.googlelogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.os.Bundle;
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


public class Main4Activity extends AppCompatActivity {

    //variables
    private Button theButton;
    private TextView theTitle;
    private TextView mStatusView;
    private MediaRecorder mRecorder;
    private Thread runner;
    private TextView dBAverage;
    private TextView spike;
    private final int SIZE_OF_DB_ARRAY = 300; //300 is to test for a minute //1500; // For recording 5 times a second * 60 seconds * 5 minutes
    private double[] dbArray = new double[SIZE_OF_DB_ARRAY];
    private double avgOfDbArray = 0.0;
    private double referenceAmp = 10.0;
    private int dbArrayIterator;

    public boolean noiseSpike = false;


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
                    onResume();


                }
                else {
                theTitle.setText("Camera");
                theButton.setText("Enter Camera Mode");
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
            checkForNoiseSpike();
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
        dBAverage.setText("Average:" + String.format("%.1f", avgOfDbArray)+ " dB");
        if (noiseSpike == true){
            spike.setText("true");
        }
        else spike.setText("false");
    }

  /*  public double soundDb(double ampl){
        return  20 * Math.log10(getAmplitude() / ampl);
    }*/

    public double soundDb(){
        return  20 * Math.log10(getAmplitude() / referenceAmp);
    }

    public int setDbAverage(){
        int itr = 0;
        double total = 0.0;

        if(soundDb() > 0) { //since the app says the dB level is -infinity when it first runs
            for (/*itr = 0*/; itr < dbArray.length; itr++) {
                dbArray[itr] = soundDb();
                total += dbArray[itr];
            }
        }

        if (itr == dbArray.length-1) {
            avgOfDbArray = total / dbArray.length;
        }

        return itr;
    }

/*    public void checkForNoiseSpike(){
        dbArrayIterator= setDbAverage();
        if ( (dbArrayIterator == dbArray.length-1) && (soundDb() > avgOfDbArray)){
            //trigger noise event
            noiseSpike = true;
            //reset the boolean after sending a notification
        }
    }*/

    public void checkForNoiseSpike(){
       if(soundDb() > 60.0){
           noiseSpike = true;
       }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;

    }

}































