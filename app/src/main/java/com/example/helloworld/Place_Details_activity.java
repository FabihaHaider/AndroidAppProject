package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class Place_Details_activity extends AppCompatActivity {
    private TextView place_name, address, price_rate, number_of_guests, description, category;
    private Place place;
    private DatabaseReference ref;
    private ArrayList<image_model> image_models;
    private RecyclerView recyclerView;
    private MyImageAdapter myImageAdapter;
    private FirebaseUser user;
    private String owner_email;
    private LinearLayout layout;
    private boolean myplace = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        setTitle("Place Details");


        if (getIntent().getExtras() != null) {
            Object place = getIntent().getExtras().get("place");
            if (place != null) {
                this.place = (Place) place;
            }
        }
        binnUI();
        if(!place.getOwner_email().equals(owner_email))
        {
            myplace = false;
            invalidateOptionsMenu();
            layout.setVisibility(View.VISIBLE);
        }


        bindLabels();
    }


    private void binnUI() {
        place_name = findViewById(R.id.place_name);
        address = findViewById(R.id.address);
        number_of_guests = findViewById(R.id.guests_number);
        price_rate = findViewById(R.id.price_rate);
        description = findViewById(R.id.description);
        category = findViewById(R.id.textview_category);
        recyclerView = findViewById(R.id.recyclerView_SHOW_IMAGES);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        image_models = new ArrayList<>();
        myImageAdapter = new MyImageAdapter(Place_Details_activity.this, image_models);
        recyclerView.setAdapter(myImageAdapter);
        ref = FirebaseDatabase.getInstance().getReference().child("Images").child(place.getName());
        user = FirebaseAuth.getInstance().getCurrentUser();
        owner_email = user.getEmail();
        layout = findViewById(R.id.layout_requestsButton);

    }

    @SuppressLint("SetTextI18n")
    private void bindLabels() {


        place_name.setText(place.getName());
        address.setText(place.getAddress());
        number_of_guests.setText(Integer.toString(place.getMaxm_no_of_guests()));
        price_rate.setText("Tk " + Integer.toString(place.getAmount_of_charge()) + " " + place.getCharge_unit());
        category.setText(place.getCategory());
        description.setText(place.getDescription());
        if(place.getDescription().toString() == "none"){
            description.setHintTextColor(Color.DKGRAY);
        }

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    String ImgUrl = dataSnapshot.child("ImgLink").getValue().toString();
                    image_model imageModel = new image_model(ImgUrl);
                    image_models.add(imageModel);
                }
                myImageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(!myplace)
        {
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.edit).setVisible(false);
        }
        else{
            menu.findItem(R.id.delete).setVisible(true);
            menu.findItem(R.id.edit).setVisible(true);
        }
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.edit:
////                onEdit();
//                return true;
//            case R.id.delete:
////                onDelete();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


}