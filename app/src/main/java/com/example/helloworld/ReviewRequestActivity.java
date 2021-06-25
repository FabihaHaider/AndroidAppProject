package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class ReviewRequestActivity extends AppCompatActivity {
    TextView dateTime, purpose, guestNum, state, placeName, location, personDetails;
    Button accept, decline;
    Request request;
    DatabaseReference databaseReference, userRef;
    String fromActivity;
    View divider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_request);

        if (getIntent().getExtras() != null) {
            Object req = getIntent().getExtras().get("request");
            if (req != null) {
                this.request = (Request) req;
            }
            Bundle bundle = getIntent().getExtras();
            fromActivity = bundle.getString("fromActivity");

        }

        bindUI();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeAcceptance("1");
                accept.setEnabled(false);
                accept.setVisibility(View.INVISIBLE);
                decline.setEnabled(false);
                decline.setVisibility(View.INVISIBLE);
                state.setText("Accepted");
                state.setTextColor(Color.GREEN);

            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeAcceptance("2");
                accept.setEnabled(false);
                accept.setVisibility(View.INVISIBLE);
                decline.setEnabled(false);
                decline.setVisibility(View.INVISIBLE);
                state.setText("Declined");
                state.setTextColor(Color.RED);

            }
        });



    }

    public void storeAcceptance(String acceptance){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Request databaseRequest;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (snapshot.exists()) {
                        String ownerMail = dataSnapshot.child("ownerMail").getValue().toString();
                        String placeName = dataSnapshot.child("placeName").getValue().toString();
                        String location = dataSnapshot.child("location").getValue().toString();
                        String senderMail = dataSnapshot.child("senderMail").getValue().toString();
                        String startDate = dataSnapshot.child("startDate").getValue().toString();
                        String endDate = dataSnapshot.child("endDate").getValue().toString();
                        String startTime = dataSnapshot.child("startTime").getValue().toString();
                        String endTime = dataSnapshot.child("endTime").getValue().toString();
                        String purpose = dataSnapshot.child("bookingPurpose").getValue().toString();
                        String guestNum = dataSnapshot.child("guestNum").getValue().toString();
                        String state = dataSnapshot.child("state").getValue().toString();

                        databaseRequest= new Request(placeName,location, ownerMail, senderMail, startDate, endDate, startTime, endTime,purpose,guestNum, state);
                        if(request.equals(databaseRequest)){
                            Log.i("debug2", placeName);
                            dataSnapshot.getRef().child("state").setValue(acceptance);
                        }

                    }




                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void bindUI(){
        placeName = findViewById(R.id.tvReviewPlaceName);
        location = findViewById(R.id.tvReviewPlaceLocation);
        dateTime = findViewById(R.id.tvReviewDateTime);
        purpose = findViewById(R.id.tvReviewPurpose);
        guestNum = findViewById(R.id.tvReviewGuestNum);
        state = findViewById(R.id.tvReviewState);
        accept = findViewById(R.id.btReviewAccept);
        decline = findViewById(R.id.btReviewDecline);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Request");
        userRef = FirebaseDatabase.getInstance().getReference().child("UserAccount");
        divider = findViewById(R.id.viewDivider);
        personDetails = findViewById(R.id.tvPersonDetails);


        placeName.setText(request.getPlaceName());
        location.setText(request.getLocation());
        dateTime.setText("From "+request.getStartDate()+", "+request.getStartTime()+"\n"+"to "+request.getEndDate()+", "+request.getEndTime());
        purpose.setText("Purpose: "+ request.getBookingPurpose());
        guestNum.setText("Number of Guests:"+ request.getGuestNum());

        if(fromActivity.equals("SentRequest"))
            showDetail(request.getOwnerMail(), "Owner");
        else
            showDetail(request.getSenderMail(), "Sender");



        if(request.getState().equals("1") || request.getState().equals("2")){
            accept.setEnabled(false);
            accept.setVisibility(View.INVISIBLE);
            decline.setEnabled(false);
            decline.setVisibility(View.INVISIBLE);
            if(request.getState().equals("1")){
                state.setText("Accepted");
                state.setTextColor(Color.GREEN);
            }
            else{
                state.setText("Declined");
                state.setTextColor(Color.RED);

            }



        }

        else{// state 0;

            if(fromActivity.equals("SentRequest")){
                accept.setEnabled(false);
                accept.setVisibility(View.INVISIBLE);
                decline.setEnabled(false);
                decline.setVisibility(View.INVISIBLE);
                state.setText("Not Reviewed");
                state.setTextColor(Color.BLACK);

            }
            else if(fromActivity.equals("ReceivedRequest")){
                state.setText("");
            }
        }

    }

    private void showDetail(String mail, String person) {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (snapshot.exists()) {
                        String dbMail= dataSnapshot.child("email").getValue().toString().trim();
                        if(dbMail.equals(mail)){
                            String username= dataSnapshot.child("username").getValue().toString().trim();
                            String profession= dataSnapshot.child("profession").getValue().toString().trim();
                            String phoneNumber= dataSnapshot.child("phone_number").getValue().toString().trim();

                            personDetails.setText(person+" Details:\nUsername: "+username+"\n" +"Profession: "+ profession + "\nEmail: " + dbMail+ "\nPhone Number: "+ phoneNumber+"\n");
                            break;
                        }


                    }




                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}