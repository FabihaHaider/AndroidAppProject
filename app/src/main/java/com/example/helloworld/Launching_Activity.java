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
import android.os.Build;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class Launching_Activity extends AppCompatActivity implements MyImageAdapter.OnItemClickListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private SliderView sliderView;

    private MyImageAdapter adapter;
    private Launching_Activity.MyPlacesAdapter adapter1;
    private RecyclerView recyclerView, recyclerView1;
    private ArrayList<image_model> arrayList_featured_place;
    private ArrayList<Place> arrayList_places_near_you, featured_places_array;
    private DatabaseReference ref, featured_places;

    private int dist;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private ProgressDialog progressBar;
    private ChipGroup chipGroup, chipGroup_category;
    private Chip chip_area, chip_name, chip_category, chip_view_all, getChip_category0, getChip_category1, getChip_category2, getChip_category3, getChip_category4;
    private ArrayList<String> selectedChipData, selectedCategory, chipgroup1, chipgroup2;
    private ImageView seacrh_icon;

    private EditText search_name;
    private LatLng latLng;
    private String email;
    private Button button_featured_places;
    private RelativeLayout tap_to_see_places_near_you;
    private DatabaseReference userlocation, placeRef;
    private ValueEventListener cacheListener,placeListener, userLocationListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        if(getIntent().getExtras() != null)
        {
            Object latlng = getIntent().getExtras().get("location");
            if (latlng != null) {
                this.latLng = (LatLng) latlng;
            }
        }

        bindUI();
        //checkCacheAndShowCache();


        if (latLng != null) {
            tap_to_see_places_near_you.setVisibility(View.VISIBLE);
        }

        search_name.requestFocus();
        inflateFeaturedplaces();

        readSearch((group1, group2) -> {
            chipgroup1 = new ArrayList<>(group1);
            chipgroup2 = new ArrayList<>(group2);
        });


        seacrh_icon.setOnClickListener(v -> searchPlaces());

        button_featured_places.setOnClickListener(v -> {
            Intent intent = new Intent(Launching_Activity.this, My_places_activity.class).putExtra("featured_places", "featured_places");
            startActivity(intent);
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        checkCacheAndShowCache();

    }

    private void checkCacheAndShowCache() {
        cacheListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(arrayList_places_near_you.size()!=0)
                    arrayList_places_near_you.clear();

                linearLayout.setVisibility(View.VISIBLE);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String email = dataSnapshot.child("owner_email").getValue().toString();
                    String place_name = dataSnapshot.child("name").getValue().toString().trim();
                    //////
                    retrievePlace(place_name);

                }
                adapter1.notifyDataSetChanged();

            }

            @Override
            public void onCancelled (@NonNull @NotNull DatabaseError error){

            }
        };
        userlocation.addListenerForSingleValueEvent(cacheListener);

    }

    private void retrievePlace(String placeName){
        //Log.i("tuba", placeName);
        placeRef=FirebaseDatabase.getInstance().getReference().child("Place");
        placeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int flag=0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Place place;
                    if (snapshot.exists()) {
                        String db_place_name = dataSnapshot.child("name").getValue().toString().trim();

                        if(db_place_name.equals(placeName)) {
                            Log.i("tuba", placeName+" "+db_place_name);
                            String email = dataSnapshot.child("owner_email").getValue().toString().trim();
                            String address = dataSnapshot.child("address").getValue().toString();
                            Integer charge_amount = Integer.parseInt(dataSnapshot.child("amount_of_charge").getValue().toString().trim());
                            String charge_rate = dataSnapshot.child("charge_unit").getValue().toString().trim();
                            Integer number_of_guests = Integer.parseInt(dataSnapshot.child("maxm_no_of_guests").getValue().toString().trim());
                            String category = dataSnapshot.child("category").getValue().toString().trim();
                            String description = dataSnapshot.child("description").getValue().toString().trim();
                            String image = dataSnapshot.child("image").getValue().toString().trim();
                            String house_number = dataSnapshot.child("house_no").getValue().toString().trim();
                            String area = dataSnapshot.child("area").getValue().toString().trim();
                            String postal_code = dataSnapshot.child("postal_code").getValue().toString().trim();


                            place = new Place(db_place_name, address, email, charge_amount, charge_rate, number_of_guests, description, category, image, house_number, area, postal_code);
                            place.setImage(image);
                            arrayList_places_near_you.add(place);
                            flag=1;

                        }
                    }
                }
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };

        placeRef.addListenerForSingleValueEvent(placeListener);

    }


    private void inflateFeaturedplaces() {
        featured_places.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Place place;
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    if(dataSnapshot.exists())
                    {
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

                        place = new Place(place_name, address, email, charge_amount, charge_rate, number_of_guests, description, category, image, house_number, area, postal_code);
                        place.setImage(image);
                        featured_places_array.add(place);
                        image_model imageModel = new image_model(image);
                        arrayList_featured_place.add(imageModel);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }





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
        arrayList_featured_place= new ArrayList<image_model>();
        adapter = new MyImageAdapter(this, arrayList_featured_place);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(Launching_Activity.this);

        featured_places_array = new ArrayList<>();

        featured_places = FirebaseDatabase.getInstance().getReference().child("Featured_places");



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            email = user.getEmail();
        }

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this, R.id.NavHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        TextView navEmail = (TextView) headerView.findViewById(R.id.header_email);
        navEmail.setText(email);

        recyclerView1 = findViewById(R.id.recyclerView_near_places);
        recyclerView1.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(linearLayoutManager1);
        arrayList_places_near_you = new ArrayList<Place>();
        adapter1 = new MyPlacesAdapter(Launching_Activity.this, arrayList_places_near_you);
        recyclerView1.setAdapter(adapter1);

        progressBar = new ProgressDialog(Launching_Activity.this);
        progressBar.setMessage("Loading places near you");

        chipgroup1 = new ArrayList<>();
        chipgroup2 = new ArrayList<>();

        seacrh_icon = findViewById(R.id.search_icon);

        chipGroup = findViewById(R.id.chip_group);

        chip_area = findViewById(R.id.chipArea);
        chip_name = findViewById(R.id.chipName);
        chip_category = findViewById(R.id.chipCategory);
        chip_view_all = findViewById(R.id.chipViewAll);



        chipGroup_category = findViewById(R.id.chip_group_category);

        selectedChipData = new ArrayList<>();

        getChip_category0 = findViewById(R.id.chipCategory0);
        getChip_category1 = findViewById(R.id.chipCategory1);
        getChip_category2 = findViewById(R.id.chipCategory2);
        getChip_category3 = findViewById(R.id.chipCategory3);
        getChip_category4 = findViewById(R.id.chipCategory4);

        selectedCategory = new ArrayList<>();

        search_name = findViewById(R.id.search_name);
        search_name.requestFocus();

        button_featured_places = findViewById(R.id.button_featured_places);

        tap_to_see_places_near_you = findViewById(R.id.tap_to_see_places_near_you);


        email = email.replace('.',' ');
        userlocation = FirebaseDatabase.getInstance().getReference().child("UserLocation").child(email);


    }



    private void searchPlaces()
    {

        Intent intent = new Intent(Launching_Activity.this, My_places_activity.class);

        boolean isThereName = false;
        boolean isThereArea = false;
        boolean isThereCategory = false;
        boolean isThereViewAll = false;

//        Log.i("fabiha", "searchPlaces: "+chipgroup1.isEmpty() + " " + chipgroup2.isEmpty() + " " + search_name.getText().toString().isEmpty());

        if(chipgroup1.isEmpty())
        {
            Toast.makeText(Launching_Activity.this, "Select what you are looking for", Toast.LENGTH_LONG).show();
            return;
        }
        if(chipgroup1.isEmpty() && !search_name.getText().toString().isEmpty())
        {
            Toast.makeText(Launching_Activity.this, "Select what you are looking for", Toast.LENGTH_LONG).show();
            return;
        }
        if(chipgroup1.size()>2)
        {
            Toast.makeText(Launching_Activity.this, "You can only look for a place by its name or by its area and/or category or you can view all places", Toast.LENGTH_LONG).show();
            return;
        }
        else if(!chipgroup1.isEmpty()) {
            Set<String> set = new HashSet<String>(chipgroup1);
            isThereName = set.contains("Name");
            isThereArea = set.contains("Area");
            isThereCategory = set.contains("Category");
            isThereViewAll = set.contains("View all");
        }
        if(!search_name.getText().toString().isEmpty() && isThereName && isThereArea)
        {
            Toast.makeText(Launching_Activity.this, "Name is unique. You can only look for a place by its name or by its area and/or category or you can view all places", Toast.LENGTH_LONG).show();
        }

        else if(!search_name.getText().toString().isEmpty() && !isThereArea && !isThereName)
        {
            Toast.makeText(Launching_Activity.this, "Select a tag to search by name or area", Toast.LENGTH_LONG).show();
            chipGroup.setVisibility(View.VISIBLE);
            return;
        }
        if(isThereName)
        {
            if (chipgroup1.size() > 1) {
                Toast.makeText(Launching_Activity.this, "Name is unique. You can only look for a place by its name or by its area and/or category or you can view all places", Toast.LENGTH_LONG).show();
                return;
            }
            else if (search_name.getText().toString().isEmpty()) {
                Toast.makeText(Launching_Activity.this, "Please enter a name or check off the Name tag", Toast.LENGTH_LONG).show();
                return;
            } else {
                String name = search_name.getText().toString();
                search_name.getText().clear();
                chipgroup1.clear();
                chipgroup2.clear();
                intent.putExtra("Name", name);
                startActivity(intent);
            }
        }

        else if (isThereArea || isThereCategory)
        {
            if (search_name.getText().toString().isEmpty() && isThereArea) {
                Toast.makeText(Launching_Activity.this, "Please enter area or check off the Area tag", Toast.LENGTH_LONG).show();
                isThereArea = false;
            }
            else if(!search_name.getText().toString().isEmpty() && isThereArea){
                String area = search_name.getText().toString();
                intent.putExtra("Area", area);
            }


            if (chipgroup2.size() == 0 && isThereCategory) {
                Toast.makeText(Launching_Activity.this, "Please select a category or check off the Category tag", Toast.LENGTH_LONG).show();
                isThereCategory = false;
            } else if (chipgroup2.size() == 1 && isThereCategory) {
                String category = chipgroup2.get(0);
                Integer position;
                if (category.equals("Social Events")) {
                    position = 0;
                } else {
                    String[] purpose = getResources().getStringArray(R.array.purpose);
                    position = Arrays.asList(purpose).indexOf(category) - 1;
                }
                intent.putExtra("Category", Integer.toString(position));
            }

            if(isThereArea || isThereCategory) {
                search_name.getText().clear();
                chipgroup1.clear();
                chipgroup2.clear();
                startActivity(intent);
            }
        }
        else if (isThereViewAll)
        {
            if (chipgroup1.size() > 1) {
                Toast.makeText(Launching_Activity.this, "You can only look for a place by its name or by its area and/or category or you can view all places", Toast.LENGTH_LONG).show();
            } else {
                String view_all = "";
                intent.putExtra("View all", view_all);
                startActivity(intent);
            }
        }

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


   private void distance(LatLng latLng1)
   {

        dist = (int) 1000000.0;

        ref = FirebaseDatabase.getInstance().getReference().child("Place");
        userlocation.removeValue();

        HashMap<String, LatLng> hashMap = new HashMap<>();
        hashMap.put("location", latLng1);

        double lon1 = latLng1.longitude;
        double lat1 = latLng1.latitude;


       ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                arrayList_places_near_you.clear();
                userlocation.removeValue();
                linearLayout.setVisibility(View.VISIBLE);

                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String address = dataSnapshot.child("address").getValue().toString();
                        LatLng latLng = getLocationFromAddress(Launching_Activity.this, address);

                        dist = (int) 1000000.0;

                        if (latLng != null) {
                            float lat2 = (float) latLng.latitude;
                            float lon2 = (float) latLng.longitude;


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

                            dist = (int) (6366000 * tt);

//                        dist = SphericalUtil.computeDistanceBetween(new LatLng(lat1, lon1), latLng);

                        }

                        Place place;
                        String email = dataSnapshot.child("owner_email").getValue().toString();
                        String place_name = dataSnapshot.child("name").getValue().toString();
                        int charge_amount = Integer.parseInt(dataSnapshot.child("amount_of_charge").getValue().toString());
                        String charge_rate = dataSnapshot.child("charge_unit").getValue().toString();
                        int number_of_guests = Integer.parseInt(dataSnapshot.child("maxm_no_of_guests").getValue().toString());
                        String category = dataSnapshot.child("category").getValue().toString();
                        String description = dataSnapshot.child("description").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();
                        String area = dataSnapshot.child("area").getValue().toString().trim();
                        String house_no = dataSnapshot.child("house_no").getValue().toString();
                        String postal_code = dataSnapshot.child("postal_code").getValue().toString();


                        if (dist <= 3000.0) {
                            place = new Place(place_name, address, email, charge_amount, charge_rate, number_of_guests, description, category, image, house_no, area, postal_code);
                            place.setImage(image);
                            arrayList_places_near_you.add(place);
                            userlocation.push().setValue(place);
                        }

                    }
                   adapter1.notifyDataSetChanged();
                    progressBar.dismiss();
                }
            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.i(TAG, "onCancelled: "+error.getMessage());
            }
        });

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
        if(p1==null)
            Log.i("tuba4", "location null");

        return p1;

    }



    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(Launching_Activity.this, Place_Details_activity.class).putExtra("place", featured_places_array.get(position));
        Bundle extras = new Bundle();
        extras.putString("source", "notMyPlacesList");
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {

    }

    @Override
    public void onLabelImageClick(int position) {

    }

    @Override
    public void onViewImageClick(int position) {

    }

    public void onNearPlacesButtonClick(View view) {
        userlocation.removeEventListener(cacheListener);
        placeRef.removeEventListener(placeListener);

        progressBar.show();

        distance(latLng);
        //tap_to_see_places_near_you.setVisibility(View.GONE);
        //Intent intent = new Intent(Launching_Activity.this,Launching_Activity.class);
        //startActivity(intent);
        //finish();
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

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_for_near_places, parent, false);
            return new Launching_Activity.MyPlacesHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull Launching_Activity.MyPlacesHolder holder, int position) {
            final Place place = models.get(position);

                Glide.with(context).load(place.getImage()).into(holder.image);


                holder.place_name.setText(place.getName());
                holder.location.setText(place.getAddress());
                Integer amount = place.getAmount_of_charge();
                holder.charge.setText("Price rate: TK " + Integer.toString(amount) + " "+ place.getCharge_unit());


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
            this.image = itemView.findViewById(R.id.imageview_near_places);
            this.place_name = itemView.findViewById(R.id.textview_near_places_name);
            this.location = itemView.findViewById(R.id.textview_near_places_address);
            this.charge = itemView.findViewById(R.id.textview_near_places_price);

        }
    }



    public void onChipViewClick(View view) {
        chipGroup.setVisibility(View.VISIBLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

       InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

    }

    private void readSearch(MySearchCallback myCallback) {

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


                }
                else {
                    selectedChipData.remove(buttonView.getText().toString());


                    if(buttonView.getText().toString().equals("Category"))
                    {
                        chipGroup_category.setVisibility(View.GONE);
                        chipGroup_category.clearCheck();
                    }

                }

                chipgroup1 = new ArrayList<>(selectedChipData);
                chipgroup2 = new ArrayList<>();
                myCallback.onCallback(chipgroup1, chipgroup2);

                Log.i(TAG, "onCheckedChanged: chipgroup1" + chipgroup1.size());
            }
        };

        chip_area.setOnCheckedChangeListener(onCheckedChangeListener);
        chip_name.setOnCheckedChangeListener(onCheckedChangeListener);
        chip_category.setOnCheckedChangeListener(onCheckedChangeListener);
        chip_view_all.setOnCheckedChangeListener(onCheckedChangeListener);



        CompoundButton.OnCheckedChangeListener onCheckedChangeListener1 = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    selectedCategory.add(buttonView.getText().toString());
                }
                else {
                    selectedCategory.remove(buttonView.getText().toString());

                }
                chipgroup2.clear();
                chipgroup2 = new ArrayList<>(selectedCategory);
                myCallback.onCallback(chipgroup1, chipgroup2);

                Log.i(TAG, "onCheckedChanged: chipgroup2 "+chipgroup2.size());
            }
        };

        getChip_category0.setOnCheckedChangeListener(onCheckedChangeListener1);
        getChip_category1.setOnCheckedChangeListener(onCheckedChangeListener1);
        getChip_category2.setOnCheckedChangeListener(onCheckedChangeListener1);
        getChip_category3.setOnCheckedChangeListener(onCheckedChangeListener1);
        getChip_category4.setOnCheckedChangeListener(onCheckedChangeListener1);
    }



    private interface MySearchCallback {
        void onCallback(ArrayList<String>group1, ArrayList<String> group2 );
    }

}


