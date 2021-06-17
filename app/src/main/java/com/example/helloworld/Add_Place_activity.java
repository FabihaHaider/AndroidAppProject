package com.example.helloworld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class Add_Place_activity extends AppCompatActivity{


    private EditText name, location, amount_of_charge, guests_no, description;
    private TextView show_extra_image;
    private Button add_place, upload_btn;
    private ImageView image;
    private ImageView added_image1, added_image2;
    private Spinner spinner_charge_rate, spinner_purpose;
    private Uri imageUri;
    private ArrayList<Uri> imageList = new ArrayList<Uri>();
    private DatabaseReference ref;
    private int upload_count = 0, img_cnt = 0;
    private String charge_rate, purpose;
    private HashMap<String, String> hashMap = new HashMap<>();
    private String url;
    private FirebaseUser user;
    private Place getPlace;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        bindUI();

        if (getIntent().getExtras() != null) {
            Object place = getIntent().getExtras().get("place");
            if (place != null) {
                this.getPlace = (Place) place;
            }
        }
        
        bindValues();

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
        spinner_charge_rate = findViewById(R.id.spinner_charge_rate);
        image = findViewById(R.id.add_image);
        added_image1 = findViewById(R.id.image_added1);
        added_image2 = findViewById(R.id.image_added2);
        show_extra_image = findViewById(R.id.show_extra_image);
        upload_btn = findViewById(R.id.button_upload);
        description = findViewById(R.id.description);
        spinner_purpose = findViewById(R.id.spinner_purpose);
        user = FirebaseAuth.getInstance().getCurrentUser();

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(Add_Place_activity.this,R.array.charge_rate, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_charge_rate.setAdapter(arrayAdapter);

        ArrayAdapter<CharSequence> arrayAdapter1 = ArrayAdapter.createFromResource(Add_Place_activity.this, R.array.purpose, android.R.layout.simple_spinner_item);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_purpose.setAdapter(arrayAdapter1);
    }

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("unchecked")
    private void bindValues() {
        if (getPlace != null) {
            name.setText(getPlace.getName());
            location.setText(getPlace.getAddress());
            amount_of_charge.setText(Integer.toString(getPlace.getAmount_of_charge()));
            guests_no.setText(Integer.toString(getPlace.getMaxm_no_of_guests()));
            description.setText(getPlace.getDescription());
        }
    }

    private void insertImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        resultLauncher.launch(intent);

        upload_btn.setVisibility(View.VISIBLE);
        upload_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                addToImageList();
            }
        });


    }

    private void addToImageList() {
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

    private void insertHouseData() {

        if(!validateInput()){
            Toast.makeText(this, "Enter all details",Toast.LENGTH_LONG).show();
        }

        else {

            storeToDatabase(createPlace());

            Toast.makeText(Add_Place_activity.this, "Inserted place successfully", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, My_places_activity.class);
            startActivity(intent);
        }
    }

    private boolean validateInput() {
        boolean allInputsValid = true;

        for(EditText input
                : new EditText[]{name, location, amount_of_charge, guests_no}) {
            if (input.getText().toString().isEmpty()) {
                input.setText("Please enter a value");
                allInputsValid = false;
            }
        }

        if(charge_rate.equals("Choose the charging rate") || purpose.equals("Let the customers know what they can use your place for"))
        {
            Toast.makeText(Add_Place_activity.this,"Please enter all values", Toast.LENGTH_LONG).show();
            allInputsValid = false;
        }
        if(imageList.size()<3){
            Toast.makeText(Add_Place_activity.this,"Please select atleast 3 images", Toast.LENGTH_LONG).show();
            allInputsValid = false;
        }
        return allInputsValid;
    }

    private void storeToDatabase(Place place) {

        StorageReference imageFolderName = FirebaseStorage.getInstance().getReference().child(user.getEmail()).child(place.getName());

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
                            url = String.valueOf(uri);
                            StorePicUri(ref, url, place);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {

                        }
                    });
                }
            });
        }
    }

    private Place createPlace() {

        String place_name = name.getText().toString();
        String address = location.getText().toString();
        String price = amount_of_charge.getText().toString().trim();
        String crowd = guests_no.getText().toString().trim();
        String description_text = description.getText().toString();
        ref = FirebaseDatabase.getInstance().getReference().child("Place");

        charge_rate = spinner_charge_rate.getSelectedItem().toString();
        purpose = spinner_purpose.getSelectedItem().toString();

        int charge_amount = 0;
        int number_of_guests = 0;

        charge_amount = Integer.parseInt(price);
        number_of_guests = Integer.parseInt(crowd);

        String email = user.getEmail();

        Place place;

        if (description_text.isEmpty()) {
            place = new Place(place_name, address, email, charge_amount, charge_rate, number_of_guests, "none", purpose, "none");
        } else {
            place = new Place(place_name, address, email, charge_amount, charge_rate, number_of_guests, description_text, purpose, "none");
        }

        return place;
    }


    private void StorePicUri(DatabaseReference ref, String url, Place place) {
        DatabaseReference Images = FirebaseDatabase.getInstance().getReference().child("Images").child(name.getText().toString());
        Images.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    img_cnt = (int) snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        hashMap.put("ImgLink", url);
        Images.child(String.valueOf(img_cnt+1)).setValue(hashMap);

        if(img_cnt==0)
        {
            place.setImage(url);
            ref.push().setValue(place);
        }

    }

}

