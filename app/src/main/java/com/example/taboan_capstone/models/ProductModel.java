package com.example.taboan_capstone.models;

public class ProductModel {

    private String prod_id,prod_seller,prod_name,prod_desc,prod_image,prod_category,prod_price,prod_avail;


    public ProductModel(){

    }

    public ProductModel(String prod_id, String prod_seller, String prod_name, String prod_desc, String prod_image, String prod_category, String prod_price, String prod_avail) {
        this.prod_id = prod_id;
        this.prod_seller = prod_seller;
        this.prod_name = prod_name;
        this.prod_desc = prod_desc;
        this.prod_image = prod_image;
        this.prod_category = prod_category;
        this.prod_price = prod_price;
        this.prod_avail = prod_avail;
    }

    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_seller() {
        return prod_seller;
    }

    public void setProd_seller(String prod_seller) {
        this.prod_seller = prod_seller;
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

    public String getProd_image() {
        return prod_image;
    }

    public void setProd_image(String prod_image) {
        this.prod_image = prod_image;
    }

    public String getProd_category() {
        return prod_category;
    }

    public void setProd_category(String prod_category) {
        this.prod_category = prod_category;
    }

    public String getProd_price() {
        return prod_price;
    }

    public void setProd_price(String prod_price) {
        this.prod_price = prod_price;
    }

    public String getProd_avail() {
        return prod_avail;
    }

    public void setProd_avail(String prod_avail) {
        this.prod_avail = prod_avail;
    }
}
