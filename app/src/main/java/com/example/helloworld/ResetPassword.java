package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class ResetPassword extends AppCompatActivity {

    private EditText email;
    private Button reset;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = findViewById(R.id.etResetMail);
        reset = findViewById(R.id.btResetPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.length() == 0){
                    Toast.makeText(ResetPassword.this, "Please enter your registration email", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendResetPasswordEmail();
                }
            }
        });
    }

    public void sendResetPasswordEmail(){
        String sEmail = email.getText().toString().trim();
        firebaseAuth.sendPasswordResetEmail(sEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ResetPassword.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(ResetPassword.this, SignInScreen.class));
                }
                else{
                    Toast.makeText(ResetPassword.this, "Couldn't send email", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

}