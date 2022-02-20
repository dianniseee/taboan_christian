package com.example.taboan_capstone;

import java.io.Serializable;

public class basket_data implements Serializable {
    private String name;
    private double price;
    private int quan;
    private double total;
    private String image;

    public basket_data(String name, double price, int quan, Double total, String image){
        this.name = name;
        this.price = price;
        this.quan = quan;
        this.total = total;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuan() {
        return quan;
    }

    public void setQuan(int quan) {
        this.quan = quan;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
