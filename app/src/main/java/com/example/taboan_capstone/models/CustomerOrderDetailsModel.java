package com.example.taboan_capstone.models;

import java.util.Comparator;

public class CustomerOrderDetailsModel {

    private String orderID , userID, orderBy, orderTo,  orderMarket,
            orderDateTime, orderDriverID, orderStatus, orderSubTotal, orderTotal,orderDelivered;

    public CustomerOrderDetailsModel(){

    }

    public CustomerOrderDetailsModel(String orderID, String userID, String orderBy, String orderTo, String orderMarket, String orderDateTime, String orderDriverID, String orderStatus, String orderSubTotal, String orderTotal, String orderDelivered) {
        this.orderID = orderID;
        this.userID = userID;
        this.orderBy = orderBy;
        this.orderTo = orderTo;
        this.orderMarket = orderMarket;
        this.orderDateTime = orderDateTime;
        this.orderDriverID = orderDriverID;
        this.orderStatus = orderStatus;
        this.orderSubTotal = orderSubTotal;
        this.orderTotal = orderTotal;
        this.orderDelivered = orderDelivered;
    }

    public static Comparator<CustomerOrderDetailsModel> CustomerOrderDetailsModelDate = new Comparator<CustomerOrderDetailsModel>() {
        @Override
        public int compare(CustomerOrderDetailsModel o1, CustomerOrderDetailsModel o2) {
            return o1.getOrderDateTime().compareTo(o2.getOrderDateTime());
        }
    };



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

    public String getOrderDelivered() {
        return orderDelivered;
    }

    public void setOrderDelivered(String orderDelivered) {
        this.orderDelivered = orderDelivered;
    }
}
