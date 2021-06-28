package com.example.helloworld;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Collections;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesHolder>{
    Context context;
    ArrayList<Place> models;
    ArrayList<Request> reqModels;
    String source;


    public PlacesAdapter(Context context, ArrayList<Place> models, String source) {
        this.context = context;
        this.models = models;
        this.source = source;
    }

    public PlacesAdapter(Context context, ArrayList<Place> models, ArrayList<Request> reqModels,String source) {
       // Collections.reverse(models);
       // Collections.reverse(reqModels);

        Log.i("tuba", models.size()+" "+reqModels.size());
        this.context = context;
        this.models = models;
        this.reqModels = reqModels;
        this.source = source;



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
        final Place place = models.get(models.size()-1-position);


        Glide.with(context).load(place.getImage()).into(holder.image);
        holder.place_name.setText("Name: " + place.getName());
        holder.location.setText("Location: " + place.getAddress());
        Integer amount = place.getAmount_of_charge();
        holder.charge.setText("Price rate: Tk " + Integer.toString(amount));
        holder.rate.setText(" " + place.getCharge_unit());

        if(source.equals("SentRequestFragment") || source.equals("ReceivedRequestFragment")){
           final Request request = reqModels.get(models.size()-1-position);
            holder.charge.setText("");
            holder.rate.setText("");

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

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(source.equals("WishlistActivity")) {
                    Intent intent = new Intent(context, Place_Details_activity.class).putExtra("place", place);
                    Bundle extras = new Bundle();
                    extras.putString("source", "notMyPlacesList");
                    intent.putExtras(extras);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                else if(source.equals("SentRequestFragment" ) ){
                    final Request request = reqModels.get(models.size()-1-position);
                    Intent intent = new Intent(context, ReviewRequestActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fromActivity", "SentRequest");
                    intent.putExtra("request", request);
                    intent.putExtras(bundle);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                else if(source.equals("ReceivedRequestFragment" ) ){
                    final Request request = reqModels.get(models.size()-1-position);
                    Intent intent = new Intent(context, ReviewRequestActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fromActivity", "ReceivedRequest");
                    intent.putExtra("request", request);
                    intent.putExtras(bundle);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}

