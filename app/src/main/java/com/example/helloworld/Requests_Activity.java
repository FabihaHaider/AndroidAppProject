package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class Requests_Activity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        viewPager = findViewById(R.id.requestsViewPager);
        tabLayout = findViewById(R.id.requestsTabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Sent Requests"));
        tabLayout.addTab(tabLayout.newTab().setText("Received Requests"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final RequestAdapter adapter = new RequestAdapter(getSupportFragmentManager(),this,tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


    }
}