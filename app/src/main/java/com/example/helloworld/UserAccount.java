package com.example.helloworld;

import androidx.core.app.RemoteInput;

import com.google.firebase.database.Exclude;

public class UserAccount {
    private String username;
    private String password;
    private String email;
    private String profession;
    private String phone_number;
    private String key;

    public UserAccount() {
    }

    public UserAccount(String username, String password, String email, String profession, String phone_number) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.profession = profession;
        this.phone_number = phone_number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
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
