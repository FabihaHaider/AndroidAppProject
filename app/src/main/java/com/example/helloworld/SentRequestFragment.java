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
import java.util.List;


public class SentRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference reqRef, placeRef;
    private ArrayList<Place> arrayList;
    private ArrayList<Request> requestList;
    //private ReqPlacesAdapter reqAdapter;
    private PlacesAdapter adapter;
    //private String state;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root =(ViewGroup) inflater.inflate(R.layout.fragment_sent_request, container, false);

        arrayList = new ArrayList<>();
        requestList = new ArrayList<>();
        reqRef = FirebaseDatabase.getInstance().getReference().child("Request");
        placeRef= FirebaseDatabase.getInstance().getReference().child("Place");




        // Add the following lines to create RecyclerView
        recyclerView = root.findViewById(R.id.sentRequestRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        adapter =new PlacesAdapter(getContext(), arrayList, requestList, "SentRequestFragment");
        recyclerView.setAdapter(adapter);
        return root;

        //Log.i("sent_req_size1", requestList.size()+" "+ arrayList.size());




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
                if(arrayList.size() != 0){
                    arrayList.clear();
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

    private interface FirebaseCallback{
        void onCallback(ArrayList<Request> list1, ArrayList<Place> list2);
    }


    /*public class ReqPlacesAdapter extends RecyclerView.Adapter<SentRequestFragment.ReqPlaceHolder> {

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


    }*/





}