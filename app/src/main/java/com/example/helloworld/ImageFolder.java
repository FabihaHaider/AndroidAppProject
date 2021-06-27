package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ImageFolder extends AppCompatActivity implements MyImageAdapter.OnItemClickListener {
    private Place place;
    private RecyclerView recyclerView;
    private MyImageAdapter adapter;
    private ArrayList<image_model> image_model_list;
    private DatabaseReference databaseReference, databaseReference_place;
    private int img_cnt;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_folder);


        setTitle("Image Folder");

        if (getIntent().getExtras() != null) {
            Object place = getIntent().getExtras().get("place");
            if (place != null) {
                this.place = (Place) place;
            }
        }
        bindUI();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onStart() {
        super.onStart();
        retrieveDataFromFirebase();
    }

    @SuppressWarnings("ConstantConditions")
    private void retrieveDataFromFirebase() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(image_model_list.size()!=0)
                    image_model_list.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    String ImgUrl = dataSnapshot.child("ImgLink").getValue().toString();
                    image_model imageModel = new image_model(ImgUrl);
                    imageModel.setKey(dataSnapshot.getKey());
                    image_model_list.add(imageModel);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.d(TAG, error.getMessage());
            }
        });

        readData(new MyCallback() {
            @Override
            public void onCallback(int count) {
                img_cnt = count;
            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void bindUI() {


        recyclerView = findViewById(R.id.recyclerView_SHOW_IMAGES);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        image_model_list = new ArrayList<image_model>();
        adapter = new MyImageAdapter(this, image_model_list);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(ImageFolder.this);

        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Images").child(place.getName());
        databaseReference_place = FirebaseDatabase.getInstance().getReference().child("Place");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_folder_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.my_picture_save:
                Intent intent_0 = new Intent(ImageFolder.this, My_places_activity.class);
                startActivity(intent_0);
                return true;

            case R.id.my_picture_add:
                Intent intent_1 = new Intent(ImageFolder.this, UpdatePicture.class).putExtra("place", place);
                startActivity(intent_1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        image_model image_model = image_model_list.get(position);
        final String selectedKey = image_model.getKey();


        if(place.getImage().equals(image_model.getImageUrl()))
        {
            Toast.makeText(ImageFolder.this, "This is your label image. Please select your label image first", Toast.LENGTH_LONG).show();
        }

        else if (img_cnt <= 3) {
            Toast.makeText(ImageFolder.this, "You must have at least 3 images. Add another image to delete this one", Toast.LENGTH_LONG).show();
        }

        else {
            StorageReference storage = firebaseStorage.getReferenceFromUrl(image_model.getImageUrl());
            storage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    databaseReference.child(selectedKey).removeValue();
                    Toast.makeText(ImageFolder.this, "Deleted", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(ImageFolder.this, "Deleted", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    @Override
    public void onLabelImageClick(int position) {
        image_model image_model = image_model_list.get(position);
        final String selected_img_url = image_model.getImageUrl();
        final String key = place.getKey();

        HashMap<String, Object> map = new HashMap<>();
        map.put("image", selected_img_url);

        databaseReference_place.child(key).updateChildren(map);
        place.setImage(selected_img_url);



    }

    @Override
    public void onViewImageClick(int position) {
        Log.i(TAG, "onViewImageClick: clicked");
    }



    private void readData(MyCallback myCallback) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                img_cnt = (int) snapshot.getChildrenCount();
                myCallback.onCallback(img_cnt);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }



    private interface MyCallback {
        void onCallback(int count);
    }

}