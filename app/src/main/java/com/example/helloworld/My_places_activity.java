package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class My_places_activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference, databaseReference_image;
    private MyPlacesAdapter myadapter;
    private ArrayList<Place> arrayList;
    private String owner_email,category_string, extras, imgURL;
    private Integer pos_in_purpose_array;
    private String[] purpose;
    private boolean myPlacesList = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);

        bindUI();

        if(getIntent().getExtras() != null) {
            extras = (String) getIntent().getExtras().get("Category");


            if (extras != null) {
                myPlacesList = false;
                invalidateOptionsMenu();

                Resources resources = getResources();
                purpose = resources.getStringArray(R.array.purpose);

                pos_in_purpose_array = Integer.parseInt(extras);

                category_string = purpose[pos_in_purpose_array + 1];

                setTitle(pos_in_purpose_array == 0 ? "Social Events archive" : category_string + " archive");

            }

            else {
                setTitle("My Places");
            }
        }



    }
    //
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onStart() {
        super.onStart();
        retrieveDataFromFirebase();

    }

    private void bindUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        owner_email = user.getEmail();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Place");
        databaseReference_image = FirebaseDatabase.getInstance().getReference().child("Images");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();
        myadapter = new MyPlacesAdapter(this, arrayList);
        recyclerView.setAdapter(myadapter);
    }

    @SuppressWarnings("ConstantConditions")
    private void retrieveDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if(arrayList.size() != 0){
                    arrayList.clear();
                }
                
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Place place;
                    if (snapshot.exists()) {
                        String email = dataSnapshot.child("owner_email").getValue().toString();
                        String place_name = dataSnapshot.child("name").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        Integer charge_amount = Integer.parseInt(dataSnapshot.child("amount_of_charge").getValue().toString());
                        String charge_rate = dataSnapshot.child("charge_unit").getValue().toString();
                        Integer number_of_guests = Integer.parseInt(dataSnapshot.child("maxm_no_of_guests").getValue().toString());
                        String category = dataSnapshot.child("category").getValue().toString();
                        String description = dataSnapshot.child("description").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();
                        String house_number = dataSnapshot.child("house_no").getValue().toString();
                        String area = dataSnapshot.child("area").getValue().toString();
                        String postal_code = dataSnapshot.child("postal_code").getValue().toString();



                        if(extras==null) {
                            if (email.equals(owner_email)) {
                                place = new Place(place_name, address, email, charge_amount, charge_rate, number_of_guests, description, category, image, house_number, area, postal_code);
                                place.setKey(dataSnapshot.getKey());
                                place.setImage(image);
                                arrayList.add(place);
                            }
                        }
                        else
                        {
                            if(category_string.equals(category)){
                                place = new Place(place_name, address, email, charge_amount, charge_rate, number_of_guests, description, category, image, house_number, area, postal_code);
                                place.setImage(image);
                                arrayList.add(place);
                            }
                        }

                    }
                }
                myadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public class MyPlacesAdapter extends RecyclerView.Adapter<MyPlacesHolder> {

        Context context;
        ArrayList<Place> models;

        public MyPlacesAdapter(Context context, ArrayList<Place> models) {
            this.context = context;
            this.models = models;
        }

        @NonNull
        @NotNull
        @Override

        public MyPlacesHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
            return new MyPlacesHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MyPlacesHolder holder, int position) {
            final Place place = models.get(position);

            Glide.with(context).load(place.getImage()).into(holder.image);
            holder.place_name.setText("Name: " + place.getName());
            holder.location.setText("Location: " + place.getAddress());
            Integer amount = place.getAmount_of_charge();
            holder.charge.setText("Price rate: Tk " + Integer.toString(amount));
            holder.rate.setText(" " + place.getCharge_unit());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(My_places_activity.this, Place_Details_activity.class).putExtra("place", place);
                    Bundle extras = new Bundle();
                    if(myPlacesList) extras.putString("source", "myPlacesList");
                    else extras.putString("source", "notMyPlacesList");
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return models.size();
        }
    }

    public class MyPlacesHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView place_name, location, charge, rate;

        public MyPlacesHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.cardview_image);
            this.place_name = itemView.findViewById(R.id.cardview_place_name);
            this.location = itemView.findViewById(R.id.cardview_location);
            this.charge = itemView.findViewById(R.id.cardview_charge);
            this.rate = itemView.findViewById(R.id.rate);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.my_places_menu, menu);
            return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_place_add:
                startActivity(new Intent(this, Add_Place_activity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(myPlacesList)
        {
            menu.findItem(R.id.my_place_add).setVisible(true);

        }
        else{
            menu.findItem(R.id.my_place_add).setVisible(false);

        }
        return true;
    }

}

