package com.example.taboan_capstone.models;

public class CustomerModel {

    private String uid,email,avatar,gender,first_name,last_name,phoneNum,address,gps_address,password,latitude,longitude,userCity,userCountry,
            userPostal,accountType,timestamp,online;

    public  CustomerModel(){}

    public CustomerModel(String uid, String email, String avatar, String gender, String first_name, String last_name, String phoneNum, String address, String gps_address, String password, String latitude, String longitude, String userCity, String userCountry, String userPostal, String accountType, String timestamp, String online) {
        this.uid = uid;
        this.email = email;
        this.avatar = avatar;
        this.gender = gender;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phoneNum = phoneNum;
        this.address = address;
        this.gps_address = gps_address;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userCity = userCity;
        this.userCountry = userCountry;
        this.userPostal = userPostal;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.online = online;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getGps_address() {
        return gps_address;
    }

    public void setGps_address(String gps_address) {
        this.gps_address = gps_address;
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

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public String getUserPostal() {
        return userPostal;
    }

    public void setUserPostal(String userPostal) {
        this.userPostal = userPostal;
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
}
