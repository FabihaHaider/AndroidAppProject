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


public class SentRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ArrayList<Request> arrayList;
    private ReqPlacesAdapter reqAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root =(ViewGroup) inflater.inflate(R.layout.fragment_sent_request, container, false);

        arrayList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Request");

        // Add the following lines to create RecyclerView
        recyclerView = root.findViewById(R.id.sentRequestRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        reqAdapter =new ReqPlacesAdapter(getContext(), arrayList);
        recyclerView.setAdapter(reqAdapter);


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveData();
    }

    private void retrieveData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserMail = user.getEmail();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(arrayList.size() != 0){
                    arrayList.clear();
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Request request;
                    if (snapshot.exists()) {
                        String senderMail = dataSnapshot.child("senderMail").getValue().toString();

                        String placeName = dataSnapshot.child("placeName").getValue().toString();
                        String location = dataSnapshot.child("location").getValue().toString();
                        String ownerMail = dataSnapshot.child("ownerMail").getValue().toString();
                        String startDate = dataSnapshot.child("startDate").getValue().toString();
                        String endDate = dataSnapshot.child("endDate").getValue().toString();
                        String startTime = dataSnapshot.child("startTime").getValue().toString();
                        String endTime = dataSnapshot.child("endTime").getValue().toString();
                        String purpose = dataSnapshot.child("bookingPurpose").getValue().toString();
                        String guestNum = dataSnapshot.child("guestNum").getValue().toString();
                        String state = dataSnapshot.child("state").getValue().toString();

                        if(senderMail.equals(currentUserMail)){

                            request= new Request(placeName,location, ownerMail, senderMail, startDate, endDate, startTime, endTime,purpose,guestNum, state);
                            arrayList.add(request);

                        }

                    }
                }
                reqAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public class ReqPlacesAdapter extends RecyclerView.Adapter<SentRequestFragment.ReqPlaceHolder> {

        Context context;
        ArrayList<Request> models;

        public ReqPlacesAdapter(Context context, ArrayList<Request> models) {
            this.context = context;
            this.models = models;
        }

        @NonNull
        @NotNull
        @Override

        public SentRequestFragment.ReqPlaceHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
            return new SentRequestFragment.ReqPlaceHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull SentRequestFragment.ReqPlaceHolder holder, int position) {

            final Request request = models.get(position);

            //Glide.with(context).load(place.getImage()).into(holder.image);
            holder.place_name.setText("Name: "+request.getPlaceName() );
            holder.location.setText("Location: " + request.getLocation());
            //Integer amount = models.get(position).getAmount_of_charge();
            holder.charge.setText("");

            if(request.getState().equals("0")) {
                holder.charge.setText("Not reviewed");
                holder.charge.setTextColor(Color.BLACK);
            }
            else if(request.getState().equals("1")) {
                holder.charge.setText("Accepted");
                holder.charge.setTextColor(Color.GREEN);
            }
            else if(request.getState().equals("2")) {
                holder.charge.setText("Declined");
                holder.charge.setTextColor(Color.RED);
            }
            holder.rate.setText("");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ReviewRequestActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fromActivity", "SentRequest");
                    intent.putExtra("request", request);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });



        }

        @Override
        public int getItemCount() {
           return models.size();
        }
    }



    public class ReqPlaceHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView place_name, location, charge, rate, state;

        public ReqPlaceHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.cardview_image);
            this.place_name = itemView.findViewById(R.id.cardview_place_name);
            this.location = itemView.findViewById(R.id.cardview_location);
            this.charge = itemView.findViewById(R.id.cardview_charge);
            this.rate = itemView.findViewById(R.id.rate);
            //this.state = itemView.findViewById(R.id.cardview_state);
        }


    }





}