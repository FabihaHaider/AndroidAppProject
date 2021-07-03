package com.example.helloworld;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SentRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference reqRef, placeRef;
    private ArrayList<Place> placeList;
    private ArrayList<Request> requestList;
    private PlacesAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root =(ViewGroup) inflater.inflate(R.layout.fragment_sent_request, container, false);

        placeList = new ArrayList<>();
        requestList = new ArrayList<>();
        reqRef = FirebaseDatabase.getInstance().getReference().child("Request");
        placeRef= FirebaseDatabase.getInstance().getReference().child("Place");


        recyclerView = root.findViewById(R.id.sentRequestRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        adapter =new PlacesAdapter(getContext(), placeList, requestList, "SentRequestFragment");
        recyclerView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveData();


    }

    private void retrieveData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserMail = user.getEmail().trim();

        reqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(placeList.size() != 0){
                    placeList.clear();
                }
                if(requestList.size()!=0){
                    requestList.clear();
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Request request;
                    if (snapshot.exists()) {
                        String dbSenderMail = dataSnapshot.child("senderMail").getValue().toString().trim();
                        String dbPlaceName = dataSnapshot.child("placeName").getValue().toString().trim();
                        String location = dataSnapshot.child("location").getValue().toString().trim();
                        String ownerMail = dataSnapshot.child("ownerMail").getValue().toString().trim();
                        String startDate = dataSnapshot.child("startDate").getValue().toString().trim();
                        String endDate = dataSnapshot.child("endDate").getValue().toString().trim();
                        String startTime = dataSnapshot.child("startTime").getValue().toString().trim();
                        String endTime = dataSnapshot.child("endTime").getValue().toString().trim();
                        String purpose = dataSnapshot.child("bookingPurpose").getValue().toString().trim();
                        String guestNum = dataSnapshot.child("guestNum").getValue().toString().trim();
                        String state = dataSnapshot.child("state").getValue().toString().trim();

                        ////
                        String rate = dataSnapshot.child("rate").getValue().toString().trim();
                        String totalCost =dataSnapshot.child("totalCost").getValue().toString().trim();

                        if(dbSenderMail.equals(currentUserMail)){

                            request= new Request(dbPlaceName,location, ownerMail, dbSenderMail, startDate, endDate, startTime, endTime,purpose,guestNum, state, rate, totalCost);
                            requestList.add(request);

                            retrievePlace(request);
                            ////////////////////////////
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


    private void retrievePlace(Request request){
        String placeName = request.getPlaceName().trim();
        placeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                boolean placeFound= false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Place place;
                    if (snapshot.exists()) {
                        String db_place_name = dataSnapshot.child("name").getValue().toString().trim();
                        String db_address = dataSnapshot.child("address").getValue().toString().trim();
                        if(db_place_name.equals(placeName) && db_address.equals(request.getLocation().trim()) ) {
                            placeFound= true;

                            String email = dataSnapshot.child("owner_email").getValue().toString().trim();

                            Integer charge_amount = Integer.parseInt(dataSnapshot.child("amount_of_charge").getValue().toString().trim());
                            String charge_rate = dataSnapshot.child("charge_unit").getValue().toString().trim();
                            Integer number_of_guests = Integer.parseInt(dataSnapshot.child("maxm_no_of_guests").getValue().toString().trim());
                            String category = dataSnapshot.child("category").getValue().toString().trim();
                            String description = dataSnapshot.child("description").getValue().toString().trim();
                            String image = dataSnapshot.child("image").getValue().toString().trim();
                            String house_number = dataSnapshot.child("house_no").getValue().toString().trim();
                            String area = dataSnapshot.child("area").getValue().toString().trim();
                            String postal_code = dataSnapshot.child("postal_code").getValue().toString().trim();


                            place = new Place(db_place_name, db_address, email, charge_amount, charge_rate, number_of_guests, description, category, image, house_number, area, postal_code);
                            place.setImage(image);
                            placeList.add(place);
                            break;

                        }
                    }
                }
                if(!placeFound){
                    Place place = new Place(placeName, request.getLocation(), request.getOwnerMail(), 0, "", Integer.valueOf(request.getGuestNum()), "", "not found", "", "", "", "");
                    //place.setImage(image);
                    placeList.add(place);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


}