package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.denzcoskun.imageslider.constants.ScaleTypes;


import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {
    private Button Landlord;
    private Button Tenant;
    private ClipData.Item add_a_place;
    private TextView textView;
    private ImageSlider imageSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        imageSlider = findViewById(R.id.image_slider);
        List<SlideModel> slidingImagesArray = new ArrayList<>();

        findViewById(R.id.MenuImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();
                textView = findViewById(R.id.header_email);
                textView.setText(email);

            }
        });


        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this, R.id.NavHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        FirebaseDatabase.getInstance().getReference().child("Images").child("red")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {


                        for(DataSnapshot data: snapshot.getChildren())
                        {
                            String s =  data.getValue().toString();
                            String d = data.getValue().toString().substring(data.getValue().toString().indexOf('=')+1, data.getValue().toString().length()-1);


                            slidingImagesArray.add(new SlideModel(d, "image1", ScaleTypes.FIT));

                        }
                        imageSlider.setImageList(slidingImagesArray, ScaleTypes.FIT);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }

}


