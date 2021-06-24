package com.example.helloworld;

public class WishList {
    private String placeName;
    private  String userMail;

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public WishList(String placeName, String userMail) {
        this.placeName = placeName;
        this.userMail = userMail;
    }
}
