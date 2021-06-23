package com.example.helloworld;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Place implements Serializable {
    private String name;
    private String address;
    private String owner_email;
    private int amount_of_charge;
    private String charge_unit;
    private int maxm_no_of_guests;
    private String description;
    private String category;
    private String image;
    private String key;
    private String area;
    private String house_no;
    private String postal_code;


    public Place() {
    }

    public Place(String name, String address, String owner_email, int amount_of_charge, String charge_unit, int maxm_no_of_guests, String description, String category, String image, String house_no, String area, String postal_code) {
        this.name = name;
        this.address = address;
        this.owner_email = owner_email;
        this.amount_of_charge = amount_of_charge;
        this.charge_unit = charge_unit;
        this.maxm_no_of_guests = maxm_no_of_guests;
        this.description = description;
        this.category = category;
        this.image = image;
        this.area = area;
        this.house_no = house_no;
        this.postal_code = postal_code;
    }


    public Place(String name, String address, String owner_email, int amount_of_charge, String charge_unit, int maxm_no_of_guests, String description, String category, String image) {
        this.name = name;
        this.address = address;
        this.owner_email = owner_email;
        this.amount_of_charge = amount_of_charge;
        this.charge_unit = charge_unit;
        this.maxm_no_of_guests = maxm_no_of_guests;
        this.description = description;
        this.category = category;
        this.image = image;
    }

    public Place(String name, String address, String owner_email, int amount_of_charge, String charge_unit, int maxm_no_of_guests, String description, String category) {
        this.name = name;
        this.address = address;
        this.owner_email = owner_email;
        this.amount_of_charge = amount_of_charge;
        this.charge_unit = charge_unit;
        this.maxm_no_of_guests = maxm_no_of_guests;
        this.description = description;
        this.category = category;


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

    public String getCharge_unit() {
        return charge_unit;
    }

    public void setCharge_unit(String charge_unit) {
        this.charge_unit = charge_unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getHouse_no() {
        return house_no;
    }

    public void setHouse_no(String house_no) {
        this.house_no = house_no;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }
}
