package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class Launching_Activity<mLocationCallback> extends AppCompatActivity {
    private TextView textView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private SliderView sliderView;
    private int[] images;

    private MyImageAdapter adapter;
    private Launching_Activity.MyPlacesAdapter adapter1;
    private RecyclerView recyclerView, recyclerView1;
    private ArrayList<image_model> arrayList;
    private ArrayList<Place> arrayList1;
    private FusedLocationProviderClient mFusedLocationClient;
    private DatabaseReference ref;

    private Double latitude, longitude, dist;
    int PERMISSION_ID = 44;
    private boolean first = true;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private ProgressDialog progressBar;
    private ChipGroup chipGroup, chipGroup_category;
    private Chip chip_area, chip_name, chip_category;
    private ArrayList<String> selectedChipData;
    private FlexboxLayout flexbox_layout_area;



    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.i(TAG, "onLocationResult: "+mLastLocation.getLatitude());
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        bindUI();
//        getLastLocation();
        searchProcess();

        //not a user no near places

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (checkPermissions()) {
//            getLastLocation();
//        }
//    }

    private void bindUI() {
        scrollView = findViewById(R.id.scrollView_launcher_activity);
        scrollView.smoothScrollTo(0, 0);
        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        linearLayout = findViewById(R.id.places_near_you);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4};

        sliderView = findViewById(R.id.imageSlider);
        MySliderAdapter mySliderAdapter = new MySliderAdapter(images, Launching_Activity.this);
        sliderView.setSliderAdapter(mySliderAdapter);
        sliderView.setAutoCycle(true);


        recyclerView = findViewById(R.id.recyclerView_featured_places);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        arrayList = new ArrayList<image_model>();
        adapter = new MyImageAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);


        for (int i = 0; i < 3; i++) {
            image_model imageModel = new image_model("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg");
            arrayList.add(imageModel);
        }
        adapter.notifyDataSetChanged();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this, R.id.NavHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        TextView navEmail = (TextView) headerView.findViewById(R.id.header_email);
        navEmail.setText(email);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        recyclerView1 = findViewById(R.id.recyclerView_near_places);
        recyclerView1.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(linearLayoutManager1);
        arrayList1 = new ArrayList<Place>();
        adapter1 = new MyPlacesAdapter(Launching_Activity.this, arrayList1);
        recyclerView1.setAdapter(adapter1);

        progressBar = new ProgressDialog(Launching_Activity.this);
        progressBar.setMessage("Loading places near you");

        chipGroup = findViewById(R.id.chip_group);
        chip_area = findViewById(R.id.chipArea);
        chip_name = findViewById(R.id.chipName);
        chip_category = findViewById(R.id.chipCategory);
        flexbox_layout_area = findViewById(R.id.flexbox_layout_area);

        chipGroup_category = findViewById(R.id.chip_group_category);

        selectedChipData = new ArrayList<String>();

    }

    private void searchProcess() {
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    selectedChipData.add(buttonView.getText().toString());

                    if(buttonView.getText().toString().equals("Category"))
                    {
                        chipGroup_category.setVisibility(View.VISIBLE);
                    }
                    if(buttonView.getText().toString().equals("Area"))
                    {
                        flexbox_layout_area.setVisibility(View.VISIBLE);
                    }

//                    chipGroup.removeView(buttonView);
//                    chipView.addView(buttonView);
                }
                else {
                    selectedChipData.remove(buttonView.getText().toString());

                    if(buttonView.getText().toString().equals("Category"))
                    {
                        chipGroup_category.setVisibility(View.GONE);
                    }
                    if(buttonView.getText().toString().equals("Area"))
                    {
                        flexbox_layout_area.setVisibility(View.GONE);
                    }
//                    chipView.removeView(buttonView);
//                    chipGroup.addView(buttonView);

                }
            }
        };

        chip_area.setOnCheckedChangeListener(onCheckedChangeListener);
        chip_name.setOnCheckedChangeListener(onCheckedChangeListener);
        chip_category.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        Toast.makeText(this, "menuopened", Toast.LENGTH_SHORT).show();
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void distance(double lat1, double lon1) {
        dist = 1000000.0;
        ref = FirebaseDatabase.getInstance().getReference().child("Place");


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                arrayList1.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String area = dataSnapshot.child("area").getValue().toString().trim();
                    LatLng latLng = getLocationFromAddress(Launching_Activity.this, area);

                    if (latLng != null) {
                        float lat2 = (float) latLng.latitude;
                        float lon2 = (float) latLng.longitude;
                        double theta = lon1 - lon2;


                        Location loc1 = new Location("");
                        loc1.setLatitude(lat1);
                        loc1.setLongitude(lon1);
                        Location loc2 = new Location("");
                        loc2.setLatitude(lat2);
                        loc2.setLongitude(lon2);
//                        dist =  (loc1.distanceTo(loc2) / 1000.0);

                        float pk = (float) (180.f / Math.PI);

                        float a1 = (float) (lat1 / pk);
                        float a2 = (float) (lon1 / pk);
                        float b1 = lat2 / pk;
                        float b2 = lon2 / pk;

                        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
                        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
                        double t3 = Math.sin(a1) * Math.sin(b1);
                        double tt = Math.acos(t1 + t2 + t3);

                        dist = 6366000 * tt;

//                        dist = SphericalUtil.computeDistanceBetween(new LatLng(lat1, lon1), latLng);

                    }

                    Place place;
                    String email = dataSnapshot.child("owner_email").getValue().toString();
                    String place_name = dataSnapshot.child("name").getValue().toString();
                    Integer charge_amount = Integer.parseInt(dataSnapshot.child("amount_of_charge").getValue().toString());
                    String charge_rate = dataSnapshot.child("charge_unit").getValue().toString();
                    Integer number_of_guests = Integer.parseInt(dataSnapshot.child("maxm_no_of_guests").getValue().toString());
                    String category = dataSnapshot.child("category").getValue().toString();
                    String description = dataSnapshot.child("description").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    String address = dataSnapshot.child("address").getValue().toString();
                    String house_no = dataSnapshot.child("house_no").getValue().toString();
                    String postal_code = dataSnapshot.child("postal_code").getValue().toString();


                    if (dist <= 3000.0) {
                        place = new Place(place_name, address, email, charge_amount, charge_rate, number_of_guests, description, category, image, house_no, area, postal_code);
                        place.setImage(image);
                        arrayList1.add(place);
                    }

                }
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.i(TAG, "onCancelled: "+error.getMessage());
            }
        });
        progressBar.dismiss();
    }

    @SuppressWarnings("ConstantConditions")
    public LatLng getLocationFromAddress(Context context, String strAddress) {

//        public void getLocationFromAddress(){
        Geocoder coder = new Geocoder(context, Locale.getDefault());
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 15);
            if (address == null) {
                return null;
            }
            if (address.size() < 1) {
                //
            } else {
                Address location = address.get(0);
                p1 = new LatLng(location.getLatitude(), location.getLongitude());

            }

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        LatLng latLng = null;
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        linearLayout.setVisibility(View.VISIBLE);
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            LatLng latLng1 = new LatLng(latitude, longitude);
                            Log.i(TAG, "onComplete: latitude "+latitude+" longitude "+longitude);
                            distance(latitude, longitude);
                        }
                    }
                });

            } else {

                if (first) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Launching_Activity.this);
                    builder.setIcon(R.drawable.ic_baseline_location_on_24);
                    builder.setTitle("Use location?");
                    builder.setMessage("Turn your GPS on to suggest places near you");

                    builder.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            progressBar.show();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    // set the negative button to do some actions
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            linearLayout.setVisibility(View.GONE);
                            return;
                        }
                    });

                    builder.setNeutralButton("", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            linearLayout.setVisibility(View.GONE);
                            return;
                        }
                    });

                    // show the alert dialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setGravity(Gravity.TOP);
                    first = false;
                }


            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }



    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }


    public class MyPlacesAdapter extends RecyclerView.Adapter<Launching_Activity.MyPlacesHolder> {

        Context context;
        ArrayList<Place> models;
        boolean showShimmer = true;

        public MyPlacesAdapter(Context context, ArrayList<Place> models) {
            this.context = context;
            this.models = models;
        }

        @NonNull
        @NotNull
        @Override

        public Launching_Activity.MyPlacesHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
            return new Launching_Activity.MyPlacesHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull Launching_Activity.MyPlacesHolder holder, int position) {
            final Place place = models.get(position);



                Glide.with(context).load(place.getImage()).into(holder.image);

                holder.place_name.setBackground(null);
                holder.location.setBackground(null);
                holder.charge.setBackground(null);
                holder.rate.setBackground(null);

                holder.place_name.setText("Name: " + place.getName());
                holder.location.setText("Location: " + place.getAddress());
                Integer amount = place.getAmount_of_charge();
                holder.charge.setText("Price rate: Tk " + Integer.toString(amount));
                holder.rate.setText(" " + place.getCharge_unit());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Launching_Activity.this, Place_Details_activity.class).putExtra("place", place);
                        Bundle extras = new Bundle();
                        extras.putString("source", "notMyPlacesList");
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



    public void onChipViewClick(View view) {
        chipGroup.setVisibility(View.VISIBLE);
    }

}


