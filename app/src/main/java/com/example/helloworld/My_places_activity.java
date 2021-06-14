package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    private DatabaseReference databaseReference;
    private MyPlacesAdapter myadapter;
    private ArrayList<Place> arrayList;
    private String owner_email;
    private ImageView add_image;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);

        setTitle("My Places");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String owner_email = user.getEmail();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Place");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();
        myadapter = new MyPlacesAdapter(this, arrayList);
        recyclerView.setAdapter(myadapter);



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Place place;
                    if(snapshot.exists()) {
                        String email = dataSnapshot.child("owner_email").getValue().toString();
                        String place_name = dataSnapshot.child("name").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        Integer charge_amount = Integer.parseInt(dataSnapshot.child("amount_of_charge").getValue().toString());
                        String charge_rate = dataSnapshot.child("charge_unit").getValue().toString();
                        Integer number_of_guests = Integer.parseInt(dataSnapshot.child("maxm_no_of_guests").getValue().toString());
                        String category = dataSnapshot.child("category").getValue().toString();

                        if (email.equals(owner_email)) {
                            place = new Place(place_name, address, email, charge_amount, charge_rate, number_of_guests, category);

                            place.setImage(R.drawable.logo);
                            arrayList.add(place);
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

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null);
            return new MyPlacesHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MyPlacesHolder holder, int position) {
            final Place place = models.get(position);

            holder.image.setImageResource(models.get(position).getImage());
            holder.place_name.setText("Name: " + models.get(position).getName());
            holder.location.setText("Location: " + models.get(position).getAddress());
            Integer amount = models.get(position).getAmount_of_charge();
            holder.charge.setText("Price rate: Tk " + Integer.toString(amount));
            holder.rate.setText(" " + models.get(position).getCharge_unit());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(My_places_activity.this, Place_Details_activity.class).putExtra("place", place);
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
}