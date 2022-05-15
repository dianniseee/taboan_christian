package com.example.taboan_capstone.models;

public class ProductModel {

    private String prod_id,prod_seller,prod_name,prod_desc,prod_image1,prod_image2,prod_category,prod_price,prod_avail;


    public ProductModel(){

    }

    public ProductModel(String prod_id, String prod_seller, String prod_name, String prod_desc, String prod_image1, String prod_image2, String prod_category, String prod_price, String prod_avail) {
        this.prod_id = prod_id;
        this.prod_seller = prod_seller;
        this.prod_name = prod_name;
        this.prod_desc = prod_desc;
        this.prod_image1 = prod_image1;
        this.prod_image2 = prod_image2;
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

    public String getProd_image1() {
        return prod_image1;
    }

    public void setProd_image1(String prod_image1) {
        this.prod_image1 = prod_image1;
    }

    public String getProd_image2() {
        return prod_image2;
    }

    public void setProd_image2(String prod_image2) {
        this.prod_image2 = prod_image2;
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
