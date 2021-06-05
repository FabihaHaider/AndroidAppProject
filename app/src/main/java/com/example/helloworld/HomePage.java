package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomePage extends AppCompatActivity {
    private Button Landlord;
    private Button Tenant;
    private ClipData.Item add_a_place;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        findViewById(R.id.MenuImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();
                textView = findViewById(R.id.header_email);
                textView.setText(email);

            }
        });


        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this, R.id.NavHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

//        Tenant = findViewById(R.id.button_Tenant);
//        Tenant.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openTenantView();
//            }
//        });
    }



//    public Boolean onCreateOptionsMenU (Menu menu){
//        getMenuInflater().inflate(R.menu.navigation_menu,menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if(id == R.id.add_a_place){
//            Log.i("navigationBar", "onOptionsItemSelected: clicked on add a place");
//            Intent intent = new Intent(HomePage.this, addHouseForRent.class);
//            startActivity(intent);
//            return true;
//        }
//        switch(item.getItemId()) {
//            case R.id.add_a_place:
//                Log.i("navigationBar", "onOptionsItemSelected: clicked on add a place");
//                Intent intent = new Intent(this, addHouseForRent.class);
//                this.startActivity(intent);
//                break;
//            case R.id.logout:
//                // another startActivity, this is for item with id "menu_item2"
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//        return true;
//    }

}


