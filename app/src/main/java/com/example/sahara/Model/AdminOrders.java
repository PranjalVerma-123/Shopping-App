package com.example.sahara.Model;

public class AdminOrders {
    private String name,address,city,date,time,pincode,status,totalAmount,phone;

    public AdminOrders() {
    }

    public AdminOrders(String name, String address, String city, String date, String time, String pincode, String status, String totalAmount, String phone) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.date = date;
        this.time = time;
        this.pincode = pincode;
        this.status = status;
        this.totalAmount = totalAmount;
        this.phone = phone;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
