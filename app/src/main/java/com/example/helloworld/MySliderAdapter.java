package com.example.helloworld;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.smarteist.autoimageslider.SliderViewAdapter;

public class MySliderAdapter extends SliderViewAdapter<MySliderAdapter.MyViewHolder> {

    int[] images;
    Context context;

    public MySliderAdapter(int[] images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        viewHolder.imageView.setImageResource(images[position]);
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, My_places_activity.class).putExtra("Category", Integer.toString( position));
                context.startActivity(intent);
//                Log.i("fabiha", "onClick: clickd");
            }
        });
    }


    @Override
    public int getCount() {
        return images.length;
    }

   class MyViewHolder extends SliderViewAdapter.ViewHolder {
        ImageView imageView;
        Button button;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slider_imageview);
            button = itemView.findViewById(R.id.button_slider_button);
        }
    }
}
