package com.example.helloworld;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;


public class ReceivedRequestFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference reqRef, placeRef;
    private ArrayList<Place> placeList;
    private ArrayList<Request> requestList;
    private PlacesAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root =(ViewGroup) inflater.inflate(R.layout.fragment_received_request, container, false);

        placeList = new ArrayList<>();
        requestList = new ArrayList<>();
        reqRef= FirebaseDatabase.getInstance().getReference().child("Request");
        placeRef= FirebaseDatabase.getInstance().getReference().child("Place");


        recyclerView = root.findViewById(R.id.receivedRequestRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        adapter =new PlacesAdapter(getContext(), placeList, requestList, "ReceivedRequestFragment");
        recyclerView.setAdapter(adapter);

        return root;
    }
    ////////////////

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
                        String state = dataSnapshot.child("state").getValue().toString().trim().trim();

                        if(dbSenderMail.equals(currentUserMail)){

                            request= new Request(dbPlaceName,location, ownerMail, dbSenderMail, startDate, endDate, startTime, endTime,purpose,guestNum, state);
                            requestList.add(request);

                            retrievePlace(dbPlaceName);
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
                            String address = dataSnapshot.child("address").getValue().toString().trim();
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
                            placeList.add(place);


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


}



