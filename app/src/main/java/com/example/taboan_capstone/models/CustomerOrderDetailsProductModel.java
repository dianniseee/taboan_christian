package com.example.taboan_capstone.models;

public class CustomerOrderDetailsProductModel {

    String id, productID, productSellerID, productName, product_Desc, product_category, price_each,quantity,subtotal;

    public CustomerOrderDetailsProductModel(){ }

    public CustomerOrderDetailsProductModel(String id, String productID, String productSellerID, String productName, String product_Desc, String product_category, String price_each, String quantity, String subtotal) {
        this.id = id;
        this.productID = productID;
        this.productSellerID = productSellerID;
        this.productName = productName;
        this.product_Desc = product_Desc;
        this.product_category = product_category;
        this.price_each = price_each;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductSellerID() {
        return productSellerID;
    }

    public void setProductSellerID(String productSellerID) {
        this.productSellerID = productSellerID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProduct_Desc() {
        return product_Desc;
    }

    public void setProduct_Desc(String product_Desc) {
        this.product_Desc = product_Desc;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public String getPrice_each() {
        return price_each;
    }

    public void setPrice_each(String price_each) {
        this.price_each = price_each;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }
}
