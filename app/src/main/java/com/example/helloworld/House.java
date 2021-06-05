package com.example.helloworld;

public class House {
    private int house_number;
    private String road_number;
    private String area;
    private String postal_code;
//    private String size;
//    private int price;


    public House(int house_number, String road_number, String area, String postal_code) {
        this.house_number = house_number;
        this.road_number = road_number;
        this.area = area;
        this.postal_code = postal_code;
//        this.size = size;
//        this.price = price;
    }

    public int getHouse_number() {
        return house_number;
    }

    public void setHouse_number(int house_number) {
        this.house_number = house_number;
    }

    public String getRoad_number() {
        return road_number;
    }

    public void setRoad_number(String road_number) {
        this.road_number = road_number;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

//    public String getSize() {
//        return size;
//    }
//
//    public void setSize(String size) {
//        this.size = size;
//    }
//
//    public int getPrice() {
//        return price;
//    }
//
//    public void setPrice(int price) {
//        this.price = price;
//    }
}
