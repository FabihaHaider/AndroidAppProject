package com.example.helloworld;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;


public class Add_Place_activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    private TextView name, location, amount_of_charge, guests_no, show_extra_image, description,category;
    private Button add_place, upload_btn;
    private ImageView image;
    private ImageView added_image1, added_image2;
    private Spinner spinner;
    private Uri imageUri;
    private int place_cnt = 0;
    private ArrayList<Uri> imageList = new ArrayList<Uri>();
    private DatabaseReference ref;
    private ProgressDialog progressDialog;
    private int upload_count = 0;
    private String charge_rate;
    private HashMap<String, String> hashMap = new HashMap<>();


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        bindUI();

        onClickingSpinner();

        add_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertHouseData();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertImage();
            }
        });

    }

    private void bindUI() {
        name = findViewById(R.id.plainText_name);
        location = findViewById(R.id.plainText_address);
        amount_of_charge = findViewById(R.id.plainText_charge);
        guests_no = findViewById(R.id.plainText_number_of_guests);
        add_place = (Button) findViewById(R.id.button_add_place);
        spinner = findViewById(R.id.spinner);
        image = findViewById(R.id.add_image);
        added_image1 = findViewById(R.id.image_added1);
        added_image2 = findViewById(R.id.image_added2);
        show_extra_image = findViewById(R.id.show_extra_image);
        upload_btn = findViewById(R.id.button_upload);
        description = findViewById(R.id.description);
        category = findViewById(R.id.plainText_category);
    }

    private void onClickingSpinner() {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(Add_Place_activity.this,R.array.charge_rate, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void insertImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        resultLauncher.launch(intent);

        upload_btn.setVisibility(View.VISIBLE);
        upload_btn.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {

                                              if(imageList.size() < 3 ){
                                                  upload_btn.setVisibility(View.INVISIBLE);
                                                  Toast.makeText(Add_Place_activity.this,"select at least three images to upload", Toast.LENGTH_LONG);
                                                  imageList.clear();
                                              }
                                              else {
                                                  added_image1.setVisibility(View.VISIBLE);
                                                  added_image2.setVisibility(View.VISIBLE);
                                                  show_extra_image.setVisibility(View.VISIBLE);
                                                  if(imageList.size() == 3) {
                                                      show_extra_image.setVisibility(View.INVISIBLE);
                                                  }
                                                  Picasso.get().load(imageList.get(0)).into(image);
                                                  Picasso.get().load(imageList.get(1)).into(added_image1);
                                                  Picasso.get().load(imageList.get(2)).into(added_image2);
                                                  show_extra_image.setText(imageList.size() - 3 + " more images");
                                                  upload_btn.setVisibility(View.INVISIBLE);
                                              }
                                          }
                                      });


    }
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();

                        if(data.getClipData().getItemCount() < 3){
                            Toast.makeText(Add_Place_activity.this,"Please select at least 3 images", Toast.LENGTH_LONG).show();
                        }

                        if(data != null && data.getClipData().getItemCount() >=3){
                            int count_data = data.getClipData().getItemCount();
                            int currentImage = 0;

                            while(currentImage < count_data){
                                imageUri = data.getClipData().getItemAt(currentImage).getUri();
                                imageList.add(imageUri);
                                currentImage++;
                            }
                        }
                        else{
                            Toast.makeText(Add_Place_activity.this,"No file selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


    public void insertHouseData() {
        String place_name = name.getText().toString();
        String address = location.getText().toString();
        String price = amount_of_charge.getText().toString().trim();
        String crowd = guests_no.getText().toString().trim();
        String description_text = description.getText().toString();
        String category_text = category.getText().toString();

        int charge_amount = 0;
        int number_of_guests = 0;
        if(price.length() !=0 && crowd.length() != 0) {
            charge_amount = Integer.parseInt(price);
            number_of_guests = Integer.parseInt(crowd);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();


        if(place_name.isEmpty() || address.isEmpty() || price.isEmpty() || crowd.isEmpty() || charge_rate.equals("Choose the charging rate") || category_text.isEmpty()){
            Toast.makeText(Add_Place_activity.this,"Enter all the data", Toast.LENGTH_LONG).show();
        }
        else if(imageList.size()<3){
            Toast.makeText(Add_Place_activity.this,"Please select 3 to 5 images only", Toast.LENGTH_LONG).show();
        }
        else {
            ref = FirebaseDatabase.getInstance().getReference().child("Place");
            Place place;

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        place_cnt = (int) snapshot.getChildrenCount();
                    }
                }

                @Override
                public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

                }
            });

            StorageReference imageFolderName = FirebaseStorage.getInstance().getReference().child(user.getEmail()).child(place_name);

            for (upload_count = 0; upload_count < imageList.size(); upload_count++) {
                Uri IndividualImage = imageList.get(upload_count);
                String imageNumber = Integer.toString(upload_count);
                StorageReference imageName = imageFolderName.child(imageNumber + IndividualImage.getLastPathSegment());

                imageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = String.valueOf(uri);
                                StorePicUri(url);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {

                            }
                        });
                    }
                });
            }

            if(description_text.isEmpty())
                place = new Place(place_name, address, email, charge_amount,charge_rate, number_of_guests, category_text);
            else
                place = new Place(place_name,address, email, charge_amount, charge_rate, number_of_guests, description_text, category_text);
            ref.child(String.valueOf(place_cnt + 1)).setValue(place);

            Toast.makeText(Add_Place_activity.this, "Inserted place successfully", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, My_places_activity.class);
            startActivity(intent);
        }
    }

    private void StorePicUri(String url) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Images").child(name.getText().toString());
        hashMap.put("ImgLink", url);
        ref.push().setValue(hashMap);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        charge_rate = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(Add_Place_activity.this, "Select an unit", Toast.LENGTH_SHORT).show();
    }


}

