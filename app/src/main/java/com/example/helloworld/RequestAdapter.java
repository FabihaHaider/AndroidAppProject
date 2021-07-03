package com.example.helloworld;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class RequestAdapter extends FragmentStateAdapter {
    private Context context;
    private int totalTabs;
    /*private String[] tabTitles = new String[]{"Received Requests", "Sent Requests"};*/


    public RequestAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new SentRequestFragment();
        }
        return new ReceivedRequestFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
