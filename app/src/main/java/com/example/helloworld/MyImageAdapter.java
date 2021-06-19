package com.example.helloworld;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
    private OnItemClickListener listener;

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

    class MyImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        ImageView imageView;

        public MyImageHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.my_place_imageView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listener != null)
            {
                int position = getBindingAdapterPosition();
                if(position != RecyclerView.NO_POSITION)
                {
                    listener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select option");
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete");
            MenuItem selectIconImage = menu.add(Menu.NONE, 2, 2, "Select as your label image");
            delete.setOnMenuItemClickListener(this);
            selectIconImage.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(listener != null)
            {
                int position = getBindingAdapterPosition();
                if(position != RecyclerView.NO_POSITION)
                {
                    switch (item.getItemId())
                    {
                        case 1:
                            listener.onDeleteClick(position);
                            return true;

                        case 2:
                            listener.onLabelImageClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onLabelImageClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
