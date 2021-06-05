package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;


import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

public class locationView extends AppCompatActivity {

    private TextView tv_src;
    private TextView tv_dest;
    private Button bt_track;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);

        tv_src = findViewById(R.id.plaintext_source);
        tv_dest = findViewById(R.id.plainText_dest);
        bt_track = findViewById(R.id.button_see_track);

        bt_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String src = tv_src.getText().toString().trim();
                String dest = tv_dest.getText().toString().trim();

                if(src=="" && dest==""){
                    Toast.makeText(getApplicationContext(), "Enter both location", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DisplayTrack(src, dest);
                }
            }
        });

    }

    private void DisplayTrack(String src, String dest) {
        //if the device doesn't have a play store then redirect it to play store
        try{
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + src + "/" + dest);
            //initialize intent with action view
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //set package
            intent.setPackage("com.google.android.apps.maps");
            //set flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //start activity
            startActivity(intent);
        }
        catch (ActivityNotFoundException e){
            //when google map is not installed initialize uri
            Uri uri = Uri.parse("https//play.google.com/store/apps/details?id=com.google.android.apps.maps");
            //initialise intent with action view
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //set package
            intent.setPackage("com.google.android.apps.maps");
            //set flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //start activity
            startActivity(intent);
        }
    }
}