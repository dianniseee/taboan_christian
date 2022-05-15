package com.example.taboan_capstone.models;

public class DriverOrderModel {

    private String orderID , userID, orderBy, orderTo,  orderMarket,orderDevFee,
            orderDateTime, orderDriverID, orderStatus, orderSubTotal, orderTotal;

    public DriverOrderModel(){

    }

    public DriverOrderModel(String orderID, String userID, String orderBy, String orderTo, String orderMarket, String orderDevFee, String orderDateTime, String orderDriverID, String orderStatus, String orderSubTotal, String orderTotal) {
        this.orderID = orderID;
        this.userID = userID;
        this.orderBy = orderBy;
        this.orderTo = orderTo;
        this.orderMarket = orderMarket;
        this.orderDevFee = orderDevFee;
        this.orderDateTime = orderDateTime;
        this.orderDriverID = orderDriverID;
        this.orderStatus = orderStatus;
        this.orderSubTotal = orderSubTotal;
        this.orderTotal = orderTotal;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderTo() {
        return orderTo;
    }

    public void setOrderTo(String orderTo) {
        this.orderTo = orderTo;
    }

    public String getOrderMarket() {
        return orderMarket;
    }

    public void setOrderMarket(String orderMarket) {
        this.orderMarket = orderMarket;
    }

    public String getOrderDevFee() {
        return orderDevFee;
    }

    public void setOrderDevFee(String orderDevFee) {
        this.orderDevFee = orderDevFee;
    }

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getOrderDriverID() {
        return orderDriverID;
    }

    public void setOrderDriverID(String orderDriverID) {
        this.orderDriverID = orderDriverID;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderSubTotal() {
        return orderSubTotal;
    }

    public void setOrderSubTotal(String orderSubTotal) {
        this.orderSubTotal = orderSubTotal;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }
}
