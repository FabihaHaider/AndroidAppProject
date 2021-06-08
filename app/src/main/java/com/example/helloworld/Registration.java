package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    private EditText username, password, phone_number, email, profession;
    private Button registar;
    private TextView back_to_signIn_screen;
    private FirebaseAuth firebase_auth;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        phone_number = (EditText) findViewById(R.id.et_user_phone_number);
        email = (EditText) findViewById(R.id.et_user_email);
        profession = (EditText) findViewById(R.id.et_user_profession);
        registar = (Button) findViewById(R.id.button_register);
        back_to_signIn_screen = findViewById(R.id.textView_back_to_signIn);

        registar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    createUserAccount();
                }
            }
        });

        back_to_signIn_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, SignInScreen.class);
                startActivity(intent);
            }
        });
    }

    private void createUserAccount() {

        firebase_auth = FirebaseAuth.getInstance();


        ref = FirebaseDatabase.getInstance().getReference().child("UserAccount");

        String name = username.getText().toString();
        String user_password = password.getText().toString();
        String user_email = email.getText().toString();
        String user_profession = profession.getText().toString();
        String user_phone_number = phone_number.getText().toString();

        UserAccount account = new UserAccount(name, user_password, user_email, user_profession,user_phone_number);
        ref.push().setValue(account);

        firebase_auth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(Registration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Registration.this, SignInScreen.class));
                }
                else{
                    Toast.makeText(Registration.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Boolean validate() {
        Boolean result = false;

        String name = username.getText().toString();
        String user_password = password.getText().toString();
        String user_email = email.getText().toString();
        String user_profession = profession.getText().toString();
        String user_phone_number = phone_number.getText().toString();

        if(name.isEmpty() || user_password.isEmpty() || user_email.isEmpty() || user_profession.isEmpty() || user_phone_number.isEmpty()){
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        }
        else{
            result = true;
        }

        return result;
    }
}