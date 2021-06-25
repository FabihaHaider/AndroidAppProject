package com.example.helloworld;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class PlacesHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView place_name, location, charge, rate;

    public PlacesHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        this.image = itemView.findViewById(R.id.cardview_image);
        this.place_name = itemView.findViewById(R.id.cardview_place_name);
        this.location = itemView.findViewById(R.id.cardview_location);
        this.charge = itemView.findViewById(R.id.cardview_charge);
        this.rate = itemView.findViewById(R.id.rate);

    }
}
