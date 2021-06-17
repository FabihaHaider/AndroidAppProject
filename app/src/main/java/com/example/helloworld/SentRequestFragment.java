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

import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class SentRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ArrayList<Place> arrayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root =(ViewGroup) inflater.inflate(R.layout.fragment_sent_request, container, false);


        // Add the following lines to create RecyclerView
        recyclerView = root.findViewById(R.id.sentRequestRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(new ReqPlacesAdapter(getContext()));


        return root;
    }


    public class ReqPlacesAdapter extends RecyclerView.Adapter<SentRequestFragment.ReqPlaceHolder> {

        Context context;
        //ArrayList<Place> models;

        public ReqPlacesAdapter(Context context) {
            this.context = context;
            //this.models = models;
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

            //final Place place = models.get(position);

            //holder.image.setImageResource(models.get(position).getImage());
            holder.place_name.setText("Name: " );
            holder.location.setText("Location: " );
            //Integer amount = models.get(position).getAmount_of_charge();
            holder.charge.setText("Price rate: Tk ");
            //holder.rate.setText(" " + models.get(position).getCharge_unit());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent intent = new Intent(getContext(), DetailsActivity.class);
                    //startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 10;
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