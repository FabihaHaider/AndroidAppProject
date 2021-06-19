package com.example.helloworld;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;


public class SentRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ArrayList<Place> arrayList;
    ReqPlacesAdapter reqAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root =(ViewGroup) inflater.inflate(R.layout.fragment_sent_request, container, false);

        arrayList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("request");

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

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Request request;
                    if (snapshot.exists()) {
                        String senderMail = dataSnapshot.child("senderMail").getValue().toString();
                        String reqPlaceName = dataSnapshot.child("placeName").getValue().toString();
                        String state = dataSnapshot.child("placeName").getValue().toString();

                        if(senderMail.equals(currentUserMail)){
                            Place place = new Place(reqPlaceName, " ", " ", 0, " ", 0, " ", "");
                            arrayList.add(place);
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
        ArrayList<Place> models;

        public ReqPlacesAdapter(Context context, ArrayList<Place> models) {
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

            final Place place = models.get(position);

            //holder.image.setImageResource(models.get(position).getImage());
            holder.place_name.setText("Name: "+place.getName() );
            holder.location.setText("Location: " );
            //Integer amount = models.get(position).getAmount_of_charge();
            holder.charge.setText("Price rate: Tk ");
            //holder.rate.setText(" " + models.get(position).getCharge_unit());


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