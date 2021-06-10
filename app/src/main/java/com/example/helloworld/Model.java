package com.example.helloworld;

public class Model {
    private String place_name, location, charge;
    private int image;

    public Model(String place_name, String location, String charge) {
        this.place_name = place_name;
        this.location = location;
        this.charge = charge;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
