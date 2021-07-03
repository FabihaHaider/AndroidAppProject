package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressPicker extends AppCompatActivity{


    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment supportMapFragment;
    private final static int REQUEST_CODE = 23;
    private String address;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private GoogleMap mMap;
    private Double selectedLat, selectedLng;
    private MarkerOptions selectedMarkerOptions;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_picker);

        setTitle("Select address from map");
        bindUI();
        checkPermission();


    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(AddressPicker.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }else{
            ActivityCompat.requestPermissions(AddressPicker.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                            mMap = googleMap;

                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(getAddress(location.getLatitude(), location.getLongitude())).draggable(true);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                            googleMap.addMarker(markerOptions).showInfoWindow();

                            textView = findViewById(R.id.textview_selected_address);
                            textView.setText(getAddress(location.getLatitude(), location.getLongitude()));


                            /*googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                                @Override
                                public void onMapLongClick(@NonNull @NotNull LatLng latLng) {
                                    checkConnection();
                                    if(networkInfo.isConnected() && networkInfo.isAvailable())
                                    {
                                        selectedLat = latLng.latitude;
                                        selectedLng = latLng.longitude;

                                        String selectedAddress = getAddress(selectedLat, selectedLng);


                                        if(selectedAddress != null){
                                            LatLng SelectedLatLng = new LatLng(selectedLat, selectedLng);
                                            selectedMarkerOptions.position(latLng).title(selectedAddress).draggable(true);
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SelectedLatLng,14));
                                            googleMap.addMarker(selectedMarkerOptions).showInfoWindow();
                                        }
                                        else{
                                            Toast.makeText(AddressPicker.this, "Address not found", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                    else{
                                        Toast.makeText(AddressPicker.this, "Please check connection", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });*/

                            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                @Override
                                public void onMarkerDragStart(@NonNull @NotNull Marker marker) {

                                }

                                @Override
                                public void onMarkerDrag(@NonNull @NotNull Marker marker) {

                                }

                                @Override
                                public void onMarkerDragEnd(@NonNull @NotNull Marker marker) {
                                    LatLng lng = marker.getPosition();
                                    String markerAddress = getAddress(lng.latitude, lng.longitude);
                                    marker.setTitle(markerAddress);

                                    Log.i("fabiha", "onMarkerDragEnd: "+ marker.getTitle());
                                    textView = findViewById(R.id.textview_selected_address);
                                    textView.setText(markerAddress);
                                }
                            });

                        }
                    });
                }
                else{
                    Toast.makeText(AddressPicker.this, "Turn your location on", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void bindUI() {
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AddressPicker.this);
        selectedMarkerOptions = new MarkerOptions();
        textView = findViewById(R.id.textview_selected_address);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
            else{
                Toast.makeText(AddressPicker.this, "Permission denied", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void checkConnection(){
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
    }

    private String getAddress(Double latitude, Double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(AddressPicker.this, Locale.getDefault());
        address = "";
        if(latitude != 0) {
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (address != null) {
                    address = addresses.get(0).getAddressLine(0);
                    Log.i("fabiha", "getAddress: "+address);
                }else {
                    Toast.makeText(AddressPicker.this, "Address not found", Toast.LENGTH_LONG).show();
                }


            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        else{
            Toast.makeText(AddressPicker.this, "Null latitude longitude", Toast.LENGTH_LONG).show();
        }

        return address;
    }

    public void onCopyTextClick(View view) {
        ClipboardManager cm = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cData = ClipData.newPlainText("text", textView.getText());
        cm.setPrimaryClip(cData);
        Toast.makeText(AddressPicker.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}