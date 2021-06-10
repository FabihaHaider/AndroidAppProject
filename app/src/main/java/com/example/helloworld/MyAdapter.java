package com.example.helloworld;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

    Context context;
    ArrayList<Model> models;

    public MyAdapter(Context context, ArrayList<Model> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @NotNull
    @Override

    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyHolder holder, int position) {
        holder.image.setImageResource(models.get(position).getImage());
        holder.place_name.setText(models.get(position).getPlace_name());
        holder.location.setText(models.get(position).getLocation());
        holder.charge.setText(models.get(position).getCharge());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
