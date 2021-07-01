package com.example.helloworld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public class Add_Place_activity extends AppCompatActivity {


    private EditText name, house_no, area, postal_code, amount_of_charge, guests_no, description, map_address;
    private TextView show_extra_image, imageText;
    private Button add_place, upload_btn;
    private ImageView image, place_first_pic;
    private ImageView added_image1, added_image2;
    private Spinner spinner_charge_rate, spinner_purpose;
    private Uri imageUri;
    private final ArrayList<Uri> imageList = new ArrayList<Uri>();
    private DatabaseReference ref;
    private int upload_count = 0, img_cnt;
    private String charge_rate, purpose;
    private final HashMap<String, String> hashMap = new HashMap<>();
    private String url;
    private FirebaseUser user;
    private Place getPlace;
    private boolean updatePlaceDetails = false, uniqueName = true;
    private ScrollView scrollView;
    private ProgressDialog progressBar;
    private LinearLayout manualAddressField;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        bindUI();



        if (getIntent().getExtras() != null) {
            Object place = getIntent().getExtras().get("place");
            if (place != null) {
                this.getPlace = (Place) place;
                updatePlaceDetails = true;
            }
        }
        setTitle(updatePlaceDetails? "Update Place Details" : "Add New Place");
        bindValues();
        onClickingSpinner();


        add_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(updatePlaceDetails)
                {
                    progressBar.show();
                }

//                readName(new MyCallback() {
//                    @Override
//                    public void onCallback(boolean unique_place_name) {
//                        uniqueName = unique_place_name;
//                        insertHouseData(uniqueName);
//                    }
//                });
                insertHouseData();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertImage();
            }
        });

        place_first_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Add_Place_activity.this, ImageFolder.class).putExtra("place", getPlace);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        readName(new MyCallback() {
            @Override
            public void onCallback(boolean unique_place_name) {
                uniqueName = unique_place_name;
            }
        });
    }

    private void onClickingSpinner() {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(Add_Place_activity.this,R.array.charge_rate, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_charge_rate.setAdapter(arrayAdapter);

        ArrayAdapter<CharSequence> arrayAdapter1 = ArrayAdapter.createFromResource(Add_Place_activity.this, R.array.purpose, android.R.layout.simple_spinner_item);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_purpose.setAdapter(arrayAdapter1);

        spinner_charge_rate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                charge_rate =parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_purpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                purpose = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void bindUI() {
        name = findViewById(R.id.plainText_name);
        house_no = findViewById(R.id.plainText_house_no);
        area = findViewById(R.id.plainText_area);
        postal_code = findViewById(R.id.plainText_postal_code);
        amount_of_charge = findViewById(R.id.plainText_charge);
        guests_no = findViewById(R.id.plainText_number_of_guests);
        map_address = findViewById(R.id.plaintext_map_address);
        add_place = findViewById(R.id.button_add_place);
        spinner_charge_rate = findViewById(R.id.spinner_charge_rate);
        image = findViewById(R.id.add_image);
        added_image1 = findViewById(R.id.image_added1);
        added_image2 = findViewById(R.id.image_added2);
        show_extra_image = findViewById(R.id.show_extra_image);
        upload_btn = findViewById(R.id.button_upload);
        description = findViewById(R.id.description);
        spinner_purpose = findViewById(R.id.spinner_purpose);
        user = FirebaseAuth.getInstance().getCurrentUser();
        imageText = findViewById(R.id.textView_uploadPicture);
        place_first_pic = findViewById(R.id.place_first_pic);
        scrollView = findViewById(R.id.scrollView_add_place);
        scrollView.smoothScrollTo(0,0);
        progressBar = new ProgressDialog(Add_Place_activity.this);
        progressBar.setMessage("Updating place details");
        manualAddressField = findViewById(R.id.linearlayout_manual_address);


    }

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("unchecked")
    private void bindValues() {
        if (getPlace != null) {
            name.setText(getPlace.getName());
            map_address.setText(getPlace.getAddress());

            if(!getPlace.getHouse_no().isEmpty() && !getPlace.getArea().isEmpty() && !getPlace.getPostal_code().isEmpty()){
                house_no.setText(getPlace.getHouse_no());
                area.setText(getPlace.getArea());
                postal_code.setText(getPlace.getPostal_code());
            }
            amount_of_charge.setText(Integer.toString(getPlace.getAmount_of_charge()));
            guests_no.setText(Integer.toString(getPlace.getMaxm_no_of_guests()));
            description.setText(getPlace.getDescription());
            name.setEnabled(false);
            image.setVisibility(View.GONE);
            add_place.setText("Update Place Details");
            place_first_pic.setVisibility(View.VISIBLE);
            Picasso.get().load(getPlace.getImage()).into(place_first_pic);

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


    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult
    (
    new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                if(result.getData().getClipData() != null) {
                    Intent data = result.getData();

                    if (data.getClipData().getItemCount() < 3) {
                        Toast.makeText(Add_Place_activity.this, "Please select at least 3 images", Toast.LENGTH_LONG).show();
                    }

                    if (data != null && data.getClipData().getItemCount() >= 3) {
                        int count_data = data.getClipData().getItemCount();
                        int currentImage = 0;

                        while (currentImage < count_data) {
                            imageUri = data.getClipData().getItemAt(currentImage).getUri();
                            imageList.add(imageUri);
                            currentImage++;
                        }
                    }
                }
                else{
                    Toast.makeText(Add_Place_activity.this,"No file selected. Long press to select a file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });

    private void insertHouseData() {
        if(!validateInput()){
            Toast.makeText(this, "Enter valid inputs",Toast.LENGTH_LONG).show();
        }

        else {

            if(updatePlaceDetails) {
                final Place place;
                place = createPlace();
                updateDatabase(place);
            }

            else {

                if(uniqueName)
                {
                    storeToDatabase(createPlace());
                    finish();
                }
                else
                {
                    Toast.makeText(Add_Place_activity.this, "Choose a unique name", Toast.LENGTH_LONG).show();
                    add_place.setEnabled(false);
                }




                /*String place_name = name.getText().toString();
                ref = FirebaseDatabase.getInstance().getReference().child("Place");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            String name = dataSnapshot.child("name").getValue().toString();
                            if(place_name.equals(name)){
                                uniqueName = false;
                                break;
                            }
                        }
                        if(uniqueName)
                        {
                            storeToDatabase(createPlace());
                            Toast.makeText(Add_Place_activity.this, "Place inserted successfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(Add_Place_activity.this, "Choose a unique name", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });*/

            }
        }




    }


    private void updateDatabase(Place place) {
        ref = FirebaseDatabase.getInstance().getReference().child("Place");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    String name = dataSnapshot.child("name").getValue().toString();
                    if(place.getName().equals(name))
                    {
                        Map<String, Object> map = new HashMap<>();
                        map.clear();
                        map.put("address", place.getAddress());
                        map.put("house_no", place.getHouse_no());
                        map.put("area", place.getArea());
                        map.put("postal_code", place.getPostal_code());
                        map.put("amount_of_charge", place.getAmount_of_charge());
                        map.put("category", place.getCategory());
                        map.put("charge_unit", place.getCharge_unit());
                        map.put("description", place.getDescription());
                        map.put("maxm_no_of_guests", place.getMaxm_no_of_guests());

                        dataSnapshot.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                map.clear();
                                Toast.makeText(Add_Place_activity.this, "Place has been updated successfully", Toast.LENGTH_LONG).show();
                                progressBar.dismiss();
                                finish();
                                Intent intent = new Intent(Add_Place_activity.this, My_places_activity.class);
                                startActivity(intent);
                            }
                        });

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private boolean validateInput() {
        boolean allInputsValid = true;
        String address_from_map = map_address.getText().toString();

        if(address_from_map.isEmpty() || address_from_map.equals("address")){
            for(EditText address_input : new EditText[]{house_no, area, postal_code}){
                if(address_input.getText().toString().isEmpty()){
                    address_input.setText("Please enter a value");
                    allInputsValid = false;
                }
            }

            String address = house_no.getText().toString() + ", "+area.getText().toString() + ", "+postal_code.getText().toString();
            if(!checkAddressValidity(address.trim())) {
                allInputsValid = false;
                Toast.makeText(Add_Place_activity.this, "Enter a valid address. Select address from map if necessary", Toast.LENGTH_LONG).show();
            }
        }
        else{
            if(!checkAddressValidity(address_from_map)){
                Toast.makeText(Add_Place_activity.this, "Enter a valid address. Select address from map if necessary", Toast.LENGTH_LONG).show();
                allInputsValid = false;
            }
        }


        for(EditText input
                : new EditText[]{name, amount_of_charge, guests_no}) {
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

        if(imageList.size()<3 && !updatePlaceDetails ){
            Toast.makeText(Add_Place_activity.this,"Please select at least 3 images", Toast.LENGTH_LONG).show();
            allInputsValid = false;
        }
        return allInputsValid;
    }

    private boolean checkAddressValidity(String strAddress) {
            Geocoder coder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> address;
            LatLng p1 = null;

            try {
                // May throw an IOException
                address = coder.getFromLocationName(strAddress, 15);
                if (address == null) {
                    return false;
                }
                if (address.size() < 1) {
                    return false;
                } else {
                    Address location = address.get(0);
                    p1 = new LatLng(location.getLatitude(), location.getLongitude());

                }

            } catch (IOException ex) {

                ex.printStackTrace();
            }
            if(p1 == null)
                return false;
            else
                return true;

    }

    private void storeToDatabase(Place place) {


        StorageReference imageFolderName = FirebaseStorage.getInstance().getReference().child(user.getEmail()).child(place.getName());
        place.setKey("");
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
                            StorePicUri(ref, url, place, upload_count);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.i("fabiha", "onFailure: '"+e.getMessage());
                        }
                    });
                }
            });
        }
    }

    private Place createPlace() {

        String place_name = name.getText().toString().trim();
        String address = "";
        String user_house_no = "";
        String user_area = "";
        String user_postal_code = "";

        String address_from_map = map_address.getText().toString();

        Log.i("fabiha", "createPlace: "+address_from_map);

        if(address_from_map.isEmpty()){
            Log.i("fabiha", "createPlace: inside " +address_from_map);
            user_house_no = house_no.getText().toString().trim();
            user_area = area.getText().toString().trim();
            user_postal_code = postal_code.getText().toString().trim();

            address = user_house_no + ", " + user_area + ", " + user_postal_code;
        }

        else
            address = address_from_map;


        String price = amount_of_charge.getText().toString().trim();
        String crowd = guests_no.getText().toString().trim();
        String description_text = description.getText().toString();
        ref = FirebaseDatabase.getInstance().getReference().child("Place");


        int charge_amount = 0;
        int number_of_guests = 0;

        charge_amount = Integer.parseInt(price);
        number_of_guests = Integer.parseInt(crowd);

        String email = user.getEmail();

        Place place;

        if (description_text.isEmpty()) {
            place = new Place(place_name, address, email, charge_amount, charge_rate, number_of_guests, "none", purpose, "none", user_house_no, user_area, user_postal_code);
        } else {
            place = new Place(place_name, address, email, charge_amount, charge_rate, number_of_guests, description_text, purpose, "none", user_house_no, user_area, user_postal_code);
        }

        place.setKey("");

        return place;
    }


    private void StorePicUri(DatabaseReference ref, String url, Place place, int upload_count) {
        DatabaseReference Images = FirebaseDatabase.getInstance().getReference().child("Images").child(name.getText().toString().trim());
        hashMap.put("ImgLink", url);
        Images.push().setValue(hashMap);


        if(!place.getKey().equals("done")) {
            Log.i("fabiha", "StorePicUri: "+place.getKey());
            place.setImage(url);
            ref.push().setValue(place).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    place.setKey("done");
                    Toast.makeText(Add_Place_activity.this, "Place inserted successfully", Toast.LENGTH_LONG).show();
                }
            });
            place.setKey("done");
        }

    }
    private void readName(MyCallback myCallback) {
        {

            EditText editText = findViewById(R.id.plainText_name);
            String place_name = editText.getText().toString();

            ref = FirebaseDatabase.getInstance().getReference().child("Place");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String name = dataSnapshot.child("name").getValue().toString();

                        if(place_name.toLowerCase().trim().equals(name.toLowerCase().trim())){
                            uniqueName = false;
                            myCallback.onCallback(uniqueName);
                            break;
                        }
                    }

                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                }
            });

        }



    }

    public void onAddressPickerClick(View view) {
        Intent intent = new Intent(Add_Place_activity.this, AddressPicker.class);
        startActivity(intent);

    }


    public void onManualAddressButtonClick(View view) {
        manualAddressField.setVisibility(View.VISIBLE);
    }


    private interface MyCallback {
        void onCallback(boolean unique_place_name);
    }



}

