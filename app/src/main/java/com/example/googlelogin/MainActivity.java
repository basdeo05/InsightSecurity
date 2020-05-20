package com.example.googlelogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.Serializable;


// Sign in with google authentication configuration. Grabbing user information after sign in to pass to different views.
//Tutorial Helps
//https://www.youtube.com/watch?v=2PIaGpJMCNs&t=1305s
//https://www.youtube.com/watch?v=uPg1ydmnzpk
//https://www.youtube.com/watch?v=lQChsNFeAMc
//https://www.youtube.com/watch?v=E1eqRNTZqDM

public class MainActivity extends AppCompatActivity {

    //Variables
     private SignInButton signin;
     private GoogleSignInClient mGoogleSignInClient;
     private String TAG = "MainActivity";
     private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 0;

    //database object
    DatabaseReference databaseUsers;
    //Database ID to signout
    String theDataBaseIDSignOUt;
    String universal;



    //On create method
    //Do this once app is created
    //First thing will be done
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    //database reference
        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");

        //sign in button
        //when sign in button is clicked
        signin= findViewById(R.id.sign_in_button);

        //Fire Base Authenticaiton
        mAuth = FirebaseAuth.getInstance();


        // Configure Google Sign In
        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //when clicked open new view
        //run sign in function
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


    }


    //Functions that will be called



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                int statusCode = result.getStatus().getStatusCode();
                //Toast.makeText(MainActivity.this, String.valueOf(statusCode), Toast.LENGTH_SHORT).show();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task );
            }
        }
        else {
            Toast.makeText(MainActivity.this, "Code did not match", Toast.LENGTH_SHORT).show();
        }
    }



    //when sign in go into new screen
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Toast.makeText(MainActivity.this, "You Signed In", Toast.LENGTH_SHORT).show();
            firebaseAuthWithGoogle (account);
        }
        catch (ApiException e) {
            Toast.makeText(MainActivity.this, "You Did Not Sign In", Toast.LENGTH_SHORT).show();
            //firebaseAuthWithGoogle (null);
        }
    }



    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Toast.makeText(MainActivity.this, "You Signed In", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            //add to database
                            final String databaseID = databaseUsers.push().getKey();
                            theDataBaseIDSignOUt = databaseID;
                            final String userEmail = user.getEmail();
                            universal = userEmail;
                            final Boolean NoiseEvent = false;
                            final Boolean notified = false;
                            final String name = user.getDisplayName();

                            //get notification token
                            FirebaseInstanceId.getInstance().getInstanceId()
                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                            if (!task.isSuccessful()) {
                                                Log.w(TAG, "getInstanceId failed", task.getException());
                                                return;
                                            }

                                            // Get new Instance ID token
                                            String notificationToken = task.getResult().getToken();
                                            Users newUser = new Users (userEmail, NoiseEvent, notificationToken, notified, name);
                                            databaseUsers.child(databaseID).setValue(newUser);
                                        }
                                    });

                            updateUI(user);
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Failed",Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }



    //update UI function
    protected void updateUI(FirebaseUser account) {

        if (account != null) {
            Intent afterLogin = new Intent(this, Main2Activity.class);
            afterLogin.putExtra("key",theDataBaseIDSignOUt);
            afterLogin.putExtra("email", universal);
            startActivity(afterLogin);
        }
    }


}
