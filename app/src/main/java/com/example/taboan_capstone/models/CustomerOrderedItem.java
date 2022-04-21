package com.example.taboan_capstone.models;

public class CustomerOrderedItem {

    private String itemCartID, pID, foodName, foodDescription, priceEach, foodQuantity, totalPrice;

    public CustomerOrderedItem(){

    }

    public CustomerOrderedItem(String itemCartID, String pID, String foodName, String foodDescription, String priceEach, String foodQuantity, String totalPrice) {
        this.itemCartID = itemCartID;
        this.pID = pID;
        this.foodName = foodName;
        this.foodDescription = foodDescription;
        this.priceEach = priceEach;
        this.foodQuantity = foodQuantity;
        this.totalPrice = totalPrice;
    }

    public String getItemCartID() {
        return itemCartID;
    }

    public void setItemCartID(String itemCartID) {
        this.itemCartID = itemCartID;
    }

    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public String getPriceEach() {
        return priceEach;
    }

    public void setPriceEach(String priceEach) {
        this.priceEach = priceEach;
    }

    public String getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(String foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
