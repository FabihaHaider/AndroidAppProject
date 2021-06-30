package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;


public class SignInScreen extends AppCompatActivity {
    private EditText email, password;
    private TextView  signup;
    private Button signin;
    private Button forgotPass;
    private int counter = 5;
    private FirebaseAuth firebase_auth;
    private ProgressDialog progressDialog;
    private FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    private double latitude, longitude;
    private boolean signed_in = false;

    RelativeLayout rellay1, rellay2;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);


        rellay1 = (RelativeLayout) findViewById(R.id.relative1);
        rellay2 = (RelativeLayout) findViewById(R.id.relative2);

        handler.postDelayed(runnable, 2000);

        email = findViewById(R.id.plainText_email);
        password = findViewById(R.id.plainText_password);
        signin = (Button) findViewById(R.id.button_signIn);
        signup = findViewById(R.id.textView_signup);
        forgotPass = findViewById(R.id.button_forgot_password);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        firebase_auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser user = firebase_auth.getCurrentUser();

        //if user is already signed in then direct them to homepage
        if(user != null){
            signed_in = true;
            getLastLocation();
        }

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signed_in = false;
                getLastLocation();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInScreen.this, Registration.class);
                startActivity(intent);
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInScreen.this, ResetPassword.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            FirebaseUser user = firebase_auth.getCurrentUser();
            if(user != null ) {
                signed_in = true;
                getLastLocation();
            }
        }
    }

    public void validate(String email_txt, String password_txt, LatLng latLng1){

        progressDialog.setMessage("Logging in");
        progressDialog.show();

        firebase_auth.signInWithEmailAndPassword(email_txt,password_txt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {
                    checkEmailVerification(latLng1);
                }
                else{
                    Toast.makeText(SignInScreen.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    counter--;

                    if(counter == 0){
                        Toast.makeText(SignInScreen.this, "Login Disabled after 5 attempts", Toast.LENGTH_LONG).show();
                        signin.setEnabled(false);
                    }
                }
            }
        });
    }


    public void checkEmailVerification(LatLng latLng1){
        FirebaseUser user = firebase_auth.getCurrentUser();
        if(user.isEmailVerified()){
            Toast.makeText(SignInScreen.this, "Login Successful", Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(SignInScreen.this, Launching_Activity.class);
            intent.putExtra("location", latLng1);
            startActivity(intent);
        }
        else{
            firebase_auth.signOut();
            Toast.makeText(SignInScreen.this, "Please verify your email", Toast.LENGTH_SHORT);
        }
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        LatLng latLng = null;
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            Log.i(TAG, "onClick: location null");
                            requestNewLocationData();
                        } else {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            LatLng latLng1 = new LatLng(latitude, longitude);
                            Log.i(TAG, "onComplete: latitude "+latitude+" longitude "+longitude + signed_in);

                            if(signed_in)
                            {
                                Toast.makeText(SignInScreen.this, "Already signed in", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(SignInScreen.this, Launching_Activity.class);
                                intent.putExtra("location", latLng1);
                                startActivity(intent);

                            }
                            else{
                                if(email.getText().toString().isEmpty()  || password.getText().toString().isEmpty()){
                                    if(getIntent().getExtras() == null){
                                        Toast.makeText(SignInScreen.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    validate(email.getText().toString(), password.getText().toString(), latLng1);
                                }
                            }
                        }
                    }
                });

            } else {

                if(signed_in)
                {
                    Toast.makeText(SignInScreen.this, "Already signed in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignInScreen.this, Launching_Activity.class);
                    startActivity(intent);
                    finish();

                }
                else{
                    if(email.getText().toString().isEmpty()  || password.getText().toString().isEmpty()){
                        if(getIntent().getExtras() == null){
                            Toast.makeText(SignInScreen.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        validate(email.getText().toString(), password.getText().toString(),null);
                    }
                }


               /* AlertDialog.Builder builder = new AlertDialog.Builder(SignInScreen.this);
                builder.setIcon(R.drawable.ic_baseline_location_on_24);
                builder.setTitle("Use location?");
                builder.setMessage("Turn your GPS on to suggest places near you");

                builder.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        getLastLocation();
                    }
                });
                // set the negative button to do some actions
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        return;
                    }
                });

                builder.setNeutralButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        return;
                    }
                });

                // show the alert dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getWindow().setGravity(Gravity.TOP);*/



            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }


    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        Log.i(TAG, "onClick: request new location data");
        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }



    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.i(TAG, "onLocationResult: "+mLastLocation.getLatitude());
            getLastLocation();
//            distance(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
    };
}