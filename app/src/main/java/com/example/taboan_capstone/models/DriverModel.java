package com.example.taboan_capstone.models;

public class DriverModel {

    private String uid,email,avatar,first_name,last_name,phoneNum,address,password,latitude,longitude,accountType,timestamp,online,availStat;

    public  DriverModel(){

    }

    public DriverModel(String uid, String email, String avatar, String first_name, String last_name, String phoneNum, String address, String password, String latitude, String longitude, String accountType, String timestamp, String online, String availStat) {
        this.uid = uid;
        this.email = email;
        this.avatar = avatar;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phoneNum = phoneNum;
        this.address = address;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.online = online;
        this.availStat = availStat;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getAvailStat() {
        return availStat;
    }

    public void setAvailStat(String availStat) {
        this.availStat = availStat;
    }
}
