package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;




public class Place_Details_activity extends AppCompatActivity implements MyImageAdapter.OnItemClickListener {
    private TextView place_name, address, price_rate, number_of_guests, description, category, phone_number;
    private Button bookNow, wishlist;
    private Place place;
    private DatabaseReference ref, databaseReference_user, wishlistRef;
    private ArrayList<image_model> image_models;
    private RecyclerView recyclerView;
    private MyImageAdapter myImageAdapter;
    private FirebaseUser user;
    private String owner_email, number, email;
    private LinearLayout layout;
    private boolean isMyplace = true;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Double latitude, longitude;
    private Geocoder geocoder;
    private String source;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        setTitle("Place Details");

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        source = extras.getString("source");


        if (getIntent().getExtras() != null) {
            Object place = getIntent().getExtras().get("place");
            if (place != null) {
                this.place = (Place) place;
            }
        }

        bindUI();
        if(!place.getOwner_email().equals(owner_email) || source.equals("notMyPlacesList"))
        {
            isMyplace = false;
            invalidateOptionsMenu();
            layout.setVisibility(View.VISIBLE);

        }


        bindLabels();


        bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Place_Details_activity.this, BookingActivity.class).putExtra("place", place);
                startActivity(intent);
            }
        });
        
        readData();


    }


    private void readData(){
        wishlistRef= FirebaseDatabase.getInstance().getReference().child("Wishlist");

        wishlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                boolean added= false;
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String dbPlaceName = dataSnapshot.child("placeName").getValue().toString().trim();
                    String dbUserMail = dataSnapshot.child("userMail").getValue().toString().trim();
                    if(dbPlaceName.equals(place.getName().trim()) && dbUserMail.equals(user.getEmail().trim())){
                        added=true;
                        break;
                    }
                }

                if(!added){
                    wishlist.setText("Add to wishlist");
                    wishlist.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WishList wishList = new WishList(place.getName(), user.getEmail());
                            String key = wishlistRef.push().getKey();
                            wishlistRef.child(key).setValue(wishList);

                            Toast.makeText(Place_Details_activity.this, "Added to wishlist successfully",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Place_Details_activity.this, Place_Details_activity.class).putExtra("place", place);
                            Bundle extras = new Bundle();
                            extras.putString("source", source);
                            intent.putExtras(extras);
                            startActivity(intent);
                            finish();

                        }
                    });

                }
                else{  //added
                    wishlist.setText("Remove from wishlist");
                    wishlist.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                String dbPlaceName = dataSnapshot.child("placeName").getValue().toString().trim();
                                String dbUserMail = dataSnapshot.child("userMail").getValue().toString().trim();
                                if(dbPlaceName.equals(place.getName().trim()) && dbUserMail.equals(user.getEmail().trim())){

                                    dataSnapshot.getRef().removeValue();
                                }
                            }


                            Toast.makeText(Place_Details_activity.this, "Removed from wishlist successfully",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Place_Details_activity.this, Place_Details_activity.class).putExtra("place", place);
                            Bundle extras = new Bundle();
                            extras.putString("source", source);
                            //else extras.putString("source", "notMyPlacesList");
                            intent.putExtras(extras);
                            startActivity(intent);
                            finish();

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }



    private void bindUI() {
        place_name = findViewById(R.id.place_name);
        address = findViewById(R.id.address);
        number_of_guests = findViewById(R.id.guests_number);
        price_rate = findViewById(R.id.price_rate);
        description = findViewById(R.id.description);
        category = findViewById(R.id.textview_category);
        phone_number = findViewById(R.id.textview_phone_number);
        recyclerView = findViewById(R.id.recyclerView_SHOW_IMAGES);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        image_models = new ArrayList<>();
        myImageAdapter = new MyImageAdapter(Place_Details_activity.this, image_models);
        recyclerView.setAdapter(myImageAdapter);
        myImageAdapter.setOnItemClickListener(Place_Details_activity.this);
        ref = FirebaseDatabase.getInstance().getReference().child("Images").child(place.getName());
        databaseReference_user = FirebaseDatabase.getInstance().getReference().child("UserAccount");
        user = FirebaseAuth.getInstance().getCurrentUser();
        owner_email = user.getEmail();
        layout = findViewById(R.id.layout_requestsButton);

        bookNow=findViewById(R.id.btn_bookNow);
        wishlist = findViewById(R.id.btn_addToWishlist);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Place_Details_activity.this);

        geocoder = new Geocoder(Place_Details_activity.this, Locale.getDefault());

    }

    @SuppressLint("SetTextI18n")
    private void bindLabels() {

        databaseReference_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    email = dataSnapshot.child("email").getValue().toString();
                    number = dataSnapshot.child("phone_number").getValue().toString();

                    if(email.equals(place.getOwner_email()))
                    {
                        phone_number.setText(number);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


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
        if(!isMyplace)
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                onEdit();
                return true;
            case R.id.delete:
                onDelete();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onEdit() {
        Intent intent = new Intent(Place_Details_activity.this, Add_Place_activity.class).putExtra("place", place);
        startActivity(intent);
    }

    private void onDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete place record?")
                .setMessage("Are you sure you want to delete the record?")
                .setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePlace();
                            }
                        })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void deletePlace() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Place");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    if(name.equals(place.getName())){
//                        dataSnapshot.getRef().removeValue();
//                        Log.i("fabiha", "onDataChange: "+dataSnapshot.getRef().toString() + " "+ dataSnapshot.getRef());
//                        dataSnapshot.getRef().setValue(null);
                        databaseReference.child(dataSnapshot.getRef().getKey()).setValue(null);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Images").child(place.getName());

        databaseReference1.removeValue();

        Toast.makeText(this, "Place deleted", Toast.LENGTH_LONG).show();

        finish();
    }


    public void onLocationClick(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                //location here
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();

                            showLocation(latitude + "," + longitude);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(Place_Details_activity.this, "failed", Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            }
        }else {
            //permission granted
        }

    }

    private void showLocation(String completeAddressString) {

        try{

            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + completeAddressString + "/" + address.getText().toString().trim());
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



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 200){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(Place_Details_activity.this, "Permission granted", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(Place_Details_activity.this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onItemClick(int position) {

        Uri uri = Uri.parse(image_models.get(position).getImageUrl());
        Intent intent = new Intent(Place_Details_activity.this, FullScreenImageActivity.class).setData(uri);
        startActivity(intent);


    }

    @Override
    public void onDeleteClick(int position) {

    }

    @Override
    public void onLabelImageClick(int position) {

    }

    @Override
    public void onViewImageClick(int position) {

    }
}