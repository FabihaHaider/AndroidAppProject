package com.example.helloworld;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Request implements Serializable {
    String placeName;
    String location;
    String ownerMail;
    String senderMail;
    String startDate;
    String endDate;
    String startTime;
    String endTime;
    String bookingPurpose;
    String guestNum;
    String state;



    public Request(String placeName, String location, String ownerMail, String senderMail, String startDate, String endDate, String startTime, String endTime, String bookingPurpose, String guestNum, String state) {
        this.placeName = placeName;
        this.location = location;
        this.ownerMail = ownerMail;
        this.senderMail = senderMail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingPurpose = bookingPurpose;
        this.guestNum = guestNum;
        this.state = state;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {

        if (this == obj)
            return true;


        if (obj == null || this.getClass() != obj.getClass())
            return false;

        Request r1 = (Request) obj;
        // checking if the two
        // objects share all the same values
        return this.placeName.equals(r1.placeName)
                && this.location.equals(r1.location)
                && this.ownerMail.equals(r1.ownerMail)
                && this.senderMail.equals(r1.senderMail)
                && this.startDate.equals(r1.startDate)
                && this.endDate.equals(r1.endDate)
                && this.startTime.equals(r1.startTime)
                && this.endTime.equals(r1.endTime)
                && this.state.equals(r1.state);

        //return super.equals(obj);
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerMail() {
        return ownerMail;
    }

    public void setOwnerMail(String ownerMail) {
        this.ownerMail = ownerMail;
    }

    public String getSenderMail() {
        return senderMail;
    }

    public void setSenderMail(String senderMail) {
        this.senderMail = senderMail;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBookingPurpose() {
        return bookingPurpose;
    }

    public void setBookingPurpose(String bookingPurpose) {
        this.bookingPurpose = bookingPurpose;
    }

    public String getGuestNum() {
        return guestNum;
    }

    public void setGuestNum(String guestNum){ this.guestNum= guestNum; }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


}
