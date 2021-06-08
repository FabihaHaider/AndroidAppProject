package com.example.helloworld;

import android.net.Uri;

import java.util.ArrayList;

public class Place {
    private String name;
    private String address;
    private String owner_email;
    private int amount_of_charge;
    private int maxm_no_of_guests;
    private ArrayList<String> arrayList = new ArrayList<String>();

    public Place(String name, String address, String owner_email, int amount_of_charge, int maxm_no_of_guests, ArrayList<String> arrayList) {
        this.name = name;
        this.address = address;
        this.owner_email = owner_email;
        this.amount_of_charge = amount_of_charge;
        this.maxm_no_of_guests = maxm_no_of_guests;
        this.arrayList = arrayList;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOwner_email() {
        return owner_email;
    }

    public void setOwner_email(String owner_email) {
        this.owner_email = owner_email;
    }

    public int getMaxm_no_of_guests() {
        return maxm_no_of_guests;
    }

    public void setMaxm_no_of_guests(int maxm_no_of_guests) {
        this.maxm_no_of_guests = maxm_no_of_guests;
    }

    public int getAmount_of_charge() {
        return amount_of_charge;
    }

    public void setAmount_of_charge(int amount_of_charge) {
        this.amount_of_charge = amount_of_charge;
    }
}
