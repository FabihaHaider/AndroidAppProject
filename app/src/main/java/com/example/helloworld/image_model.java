package com.example.helloworld;

public class image_model {
    private String ImgLink;

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
}