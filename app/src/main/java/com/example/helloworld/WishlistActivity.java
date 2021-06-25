package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference placeRef, imageRef, wishlistRef;
    private PlacesAdapter adapter;
    private ArrayList<Place> arrayList;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);

        bindUI();


    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveWishlist();

    }

    private void retrieveWishlist(){

        wishlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(arrayList.size() != 0){
                    arrayList.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String dbPlaceName = dataSnapshot.child("placeName").getValue().toString().trim();
                    String dbUserMail = dataSnapshot.child("userMail").getValue().toString().trim();
                    if(dbUserMail.equals(userEmail)){
                        ////place
                        retrievePlace(dbPlaceName);

                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void retrievePlace(String placeName){
        placeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Place place;
                    if (snapshot.exists()) {
                        String db_place_name = dataSnapshot.child("name").getValue().toString().trim();
                        if(db_place_name.equals(placeName)) {

                            String email = dataSnapshot.child("owner_email").getValue().toString().trim();
                            String address = dataSnapshot.child("address").getValue().toString();
                            Integer charge_amount = Integer.parseInt(dataSnapshot.child("amount_of_charge").getValue().toString().trim());
                            String charge_rate = dataSnapshot.child("charge_unit").getValue().toString().trim();
                            Integer number_of_guests = Integer.parseInt(dataSnapshot.child("maxm_no_of_guests").getValue().toString().trim());
                            String category = dataSnapshot.child("category").getValue().toString().trim();
                            String description = dataSnapshot.child("description").getValue().toString().trim();
                            String image = dataSnapshot.child("image").getValue().toString().trim();
                            String house_number = dataSnapshot.child("house_no").getValue().toString().trim();
                            String area = dataSnapshot.child("area").getValue().toString().trim();
                            String postal_code = dataSnapshot.child("postal_code").getValue().toString().trim();


                            place = new Place(db_place_name, address, email, charge_amount, charge_rate, number_of_guests, description, category, image, house_number, area, postal_code);
                            place.setImage(image);
                            arrayList.add(place);


                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


    private void bindUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = user.getEmail();
        placeRef = FirebaseDatabase.getInstance().getReference().child("Place");
        imageRef = FirebaseDatabase.getInstance().getReference().child("Images");
        wishlistRef= FirebaseDatabase.getInstance().getReference().child("Wishlist");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();
        adapter = new PlacesAdapter(this, arrayList, "WishlistActivity");
        recyclerView.setAdapter(adapter);
    }

}