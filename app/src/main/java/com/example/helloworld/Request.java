package com.example.helloworld;

public class Request {
    String placeName;
    String ownerMail;
    String senderMail;
    String startDate;
    String endDate;
    String startTime;
    String endTime;
    String bookingPurpose;
    String guestNum;
    int state;



    public Request(String placeName, String ownerMail, String senderMail, String startDate, String endDate, String startTime, String endTime, String bookingPurpose, String guestNum, int state) {
        this.placeName = placeName;
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

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


}
