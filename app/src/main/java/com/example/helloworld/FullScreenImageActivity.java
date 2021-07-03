package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FullScreenImageActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        bindUI();
        setTitle("Full Screen Image");

        Intent callingActivityIntent = getIntent();
        if(callingActivityIntent != null)
        {
            Uri imageUri = callingActivityIntent.getData();
            if(imageUri != null && imageView != null)
            {
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView);
            }
        }
    }

    private void bindUI() {
        imageView = findViewById(R.id.imageview_fullscreen);
    }
}