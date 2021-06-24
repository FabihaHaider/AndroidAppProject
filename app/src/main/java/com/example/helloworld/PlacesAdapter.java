package com.example.helloworld;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesHolder>{
    Context context;
    ArrayList<Place> models;

    public PlacesAdapter(Context context, ArrayList<Place> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @NotNull
    @Override

    public PlacesHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new PlacesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PlacesHolder holder, int position) {
        final Place place = models.get(position);

        Glide.with(context).load(place.getImage()).into(holder.image);
        holder.place_name.setText("Name: " + place.getName());
        holder.location.setText("Location: " + place.getAddress());
        Integer amount = place.getAmount_of_charge();
        holder.charge.setText("Price rate: Tk " + Integer.toString(amount));
        holder.rate.setText(" " + place.getCharge_unit());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Place_Details_activity.class).putExtra("place", place);
                Bundle extras = new Bundle();
                extras.putString("source", "notMyPlacesList");
                intent.putExtras(extras);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}

