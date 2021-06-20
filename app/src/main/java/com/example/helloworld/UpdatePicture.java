package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class UpdatePicture extends AppCompatActivity {
    private Place place;
    private FirebaseUser user;
    private boolean add_picture = true;
    private String update_string, img_url;
    private ImageView imageView;
    private Button delete_image, choose_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_picture);


        Bundle extras = getIntent().getExtras();
        update_string = extras.getString("update_string");
        img_url = extras.getString("img_url");

        if(extras != null){
            Object place = getIntent().getExtras().get("place");
            if (place != null) {
                this.place = (Place) place;
            }
        }
        Log.i("fabiha", "onCreate: "+ img_url + " update string " + update_string);

        if (update_string.equals("delete")) {
            add_picture = false;
            img_url = extras.getString("img_url");
            imageView = findViewById(R.id.row_update_picture_imageView);
            Picasso.get().load(img_url).into(imageView);
            delete_image.setVisibility(View.VISIBLE);
        }

        else{
            choose_image = findViewById(R.id.button_choose_image);
            choose_image.setVisibility(View.VISIBLE);
        }

        setTitle( add_picture ? "Add picture" : "Delete picture");
//
//        delete_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletePicture();
//            }
//        });
    }


//    private void deletePicture() {
//        FirebaseDatabase.getInstance().getReference().child("Images").child(place.getName()).addValueEventListener(new ValueEventListener()
//        {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot: snapshot.getChildren())
//                {
//                    String url = dataSnapshot.child("ImgLink").getValue().toString();
//                    String key = dataSnapshot.getKey().toString();
//                    if(url.equals(img_url)){
//                        dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                Toast.makeText(UpdatePicture.this, "Picture removed", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
//    }

}