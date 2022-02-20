package com.example.taboan_capstone;

import android.media.Image;
import android.widget.ImageView;

import java.io.Serializable;

public class product implements Serializable {

    private String prod_name;
    private String prod_desc;
    private String prod_category;
    private double prod_price;
    private double prod_quantity;
    private String image;
    private int quant;
    private int id;

    public product(int id, String prod_name, String prod_desc, String prod_category, double prod_price, double prod_quantity, String image) {
        this.id = id;
        this.prod_name = prod_name;
        this.prod_desc = prod_desc;
        this.prod_category = prod_category;
        this.prod_price = prod_price;
        this.prod_quantity = prod_quantity;
        this.image = image;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getProd_desc() {
        return prod_desc;
    }

    public void setProd_desc(String prod_desc) {
        this.prod_desc = prod_desc;
    }

    public String getProd_category() {
        return prod_category;
    }

    public void setProd_category(String prod_category) {
        this.prod_category = prod_category;
    }

    public double getProd_price() {
        return prod_price;
    }

    public void setProd_price(double prod_price) {
        this.prod_price = prod_price;
    }

    public double getProd_quantity() {
        return prod_quantity;
    }

    public void setProd_quantity(double prod_quantity) {
        this.prod_quantity = prod_quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuant() {
        return quant;
    }

    public void setQuant(int quant) {
        this.quant = quant;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
