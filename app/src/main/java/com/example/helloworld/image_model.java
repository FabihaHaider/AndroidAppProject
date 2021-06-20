package com.example.helloworld;

import com.google.firebase.database.Exclude;

public class image_model {
    private String ImgLink;
    private String key;

    public image_model() {
    }

    public image_model(String imgLink) {
        ImgLink = imgLink;
    }

    public String getImageUrl() {
        return ImgLink;
    }

    public void setImageUrl(String imgLink) {
        ImgLink = imgLink;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}