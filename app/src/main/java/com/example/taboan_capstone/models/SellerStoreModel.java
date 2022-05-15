package com.example.taboan_capstone.models;

public class SellerStoreModel {

    private String accountType,gender,address,cover_photo,email,first_name,last_name,
            online,phoneNum,store_email,store_location,store_market,store_category,
            store_name,store_password,timestamp,uid;

    public SellerStoreModel(){}

    public SellerStoreModel(String accountType, String gender, String address, String cover_photo, String email, String first_name, String last_name, String online, String phoneNum, String store_email, String store_location, String store_market, String store_category, String store_name, String store_password, String timestamp, String uid) {
        this.accountType = accountType;
        this.gender = gender;
        this.address = address;
        this.cover_photo = cover_photo;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.online = online;
        this.phoneNum = phoneNum;
        this.store_email = store_email;
        this.store_location = store_location;
        this.store_market = store_market;
        this.store_category = store_category;
        this.store_name = store_name;
        this.store_password = store_password;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCover_photo() {
        return cover_photo;
    }

    public void setCover_photo(String cover_photo) {
        this.cover_photo = cover_photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getStore_email() {
        return store_email;
    }

    public void setStore_email(String store_email) {
        this.store_email = store_email;
    }

    public String getStore_location() {
        return store_location;
    }

    public void setStore_location(String store_location) {
        this.store_location = store_location;
    }

    public String getStore_market() {
        return store_market;
    }

    public void setStore_market(String store_market) {
        this.store_market = store_market;
    }

    public String getStore_category() {
        return store_category;
    }

    public void setStore_category(String store_category) {
        this.store_category = store_category;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_password() {
        return store_password;
    }

    public void setStore_password(String store_password) {
        this.store_password = store_password;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
