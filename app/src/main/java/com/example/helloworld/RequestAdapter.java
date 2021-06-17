package com.example.helloworld;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

public class RequestAdapter extends FragmentPagerAdapter {
    private Context context;
    int totalTabs;
    public RequestAdapter(@NonNull @NotNull FragmentManager fm, Context context, int totalTabs) {
        super(fm);
        this.context=context;
        this.totalTabs=totalTabs;
    }

    // tab titles
    private String[] tabTitles = new String[]{"Sent Requests", "Received Requests"};

    // overriding getPageTitle()
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new SentRequestFragment();
        }
        else if(position==1)
            return new ReceivedRequestFragment();
        else return null;

    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
