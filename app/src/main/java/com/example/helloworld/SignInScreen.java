package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignInScreen extends AppCompatActivity {
    private EditText email, password;
    private TextView  signup;
    private Button signin;
    private Button forgotPass;
    private int counter = 5;
    private FirebaseAuth firebase_auth;
    private ProgressDialog progressDialog;

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



        firebase_auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser user = firebase_auth.getCurrentUser();

        //if user is already signed in then direct them to homepage
        if(user != null){
            finish();
            Toast.makeText(SignInScreen.this, "Already signed in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignInScreen.this, Launching_Activity.class));
        }

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty()  || password.getText().toString().isEmpty()){
                    Toast.makeText(SignInScreen.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                }
                else {
                    validate(email.getText().toString(), password.getText().toString());
                }
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

    public void validate(String email_txt, String password_txt){

        progressDialog.setMessage("Logging in");
        progressDialog.show();

        firebase_auth.signInWithEmailAndPassword(email_txt,password_txt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {
                    checkEmailVerification();
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


    public void checkEmailVerification(){
        FirebaseUser user = firebase_auth.getCurrentUser();
        if(user.isEmailVerified()){
            Toast.makeText(SignInScreen.this, "Login Successful", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(SignInScreen.this, Launching_Activity.class));

        }
        else{
            firebase_auth.signOut();
            Toast.makeText(SignInScreen.this, "Please verify your email", Toast.LENGTH_SHORT);
        }
    }
}