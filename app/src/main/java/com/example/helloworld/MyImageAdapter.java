package com.example.helloworld;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyImageAdapter extends RecyclerView.Adapter<MyImageAdapter.MyImageHolder> {

    private ArrayList<image_model>List;
    private Context context;

    public MyImageAdapter(Context context, ArrayList<image_model> list) {
        List = list;
        this.context = context;
    }

    @NotNull
    @Override
    public MyImageHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_for_myplace_photos, parent, false);
        return new MyImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyImageAdapter.MyImageHolder holder, int position) {
        Glide.with(context).load(List.get(position).getImageUrl()).into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return List.size();
    }
    public static class MyImageHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public MyImageHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.my_place_imageView);
        }
    }
}
