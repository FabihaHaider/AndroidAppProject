package com.example.helloworld;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdatePicture extends AppCompatActivity {
    private Place place;
    private FirebaseUser user;
    private ImageView imageView;
    private Button upload_image, choose_image, image_folder;
    private int img_cnt = 0;
    private ProgressBar progressBar;

    private Uri imageUri;
    private final ArrayList<Uri> imageList = new ArrayList<Uri>();
    private final HashMap<String, String> hashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_picture);

        setTitle("Add New Pictures");

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            Object place = getIntent().getExtras().get("place");
            if (place != null) {
                this.place = (Place) place;
            }
        }

        bindUI();

        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_file();
            }
        });

        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_image();
            }
        });

        image_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back_to_image_folder();
            }
        });
    }

    private void bindUI() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        imageView = findViewById(R.id.update_picture_imageView);
        upload_image = findViewById(R.id.button_upload_picture);
        choose_image = findViewById(R.id.button_choose_image);
        image_folder = findViewById(R.id.button_image_folder);
        progressBar = findViewById(R.id.progress_bar);

    }

    private void open_file() {
        resultLauncher.launch("image/*");
    }

    private void upload_image() {
        if(imageList.size() == 0){
            Toast.makeText(UpdatePicture.this, "No image selected", Toast.LENGTH_LONG).show();
        }
        else {
            StorageReference imageFolderName = FirebaseStorage.getInstance().getReference().child(user.getEmail()).child(place.getName());

            Uri IndividualImage = imageList.get(0);
            String imageNumber = Integer.toString(0);
            StorageReference imageName = imageFolderName.child(imageNumber + IndividualImage.getLastPathSegment());

            imageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 3000);

                    imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = String.valueOf(uri);
                            StorePicUri(url);
                            Toast.makeText(UpdatePicture.this, "Upload successful", Toast.LENGTH_LONG).show();
                            choose_image.setEnabled(false);
                            upload_image.setEnabled(false);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(UpdatePicture.this, "Couldn't upload image. Please try again later", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressBar.setProgress((int)progress);
                }
            });

        }
    }

    private void back_to_image_folder() {
        finish();
        Intent intent = new Intent(UpdatePicture.this, ImageFolder.class);
        intent.putExtra("place", place);
        startActivity(intent);
    }


    ActivityResultLauncher<String> resultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
                if(result!=null)
                {
                    Log.i("fabiha", "onActivityResult: "+result);
                    Picasso.get().load(result).into(imageView);
                    imageList.add(result);
                }
        }
    });


    private void StorePicUri(String url) {
        DatabaseReference Images = FirebaseDatabase.getInstance().getReference().child("Images").child(place.getName());

        hashMap.put("ImgLink", url);
        Images.push().setValue(hashMap);


    }
}