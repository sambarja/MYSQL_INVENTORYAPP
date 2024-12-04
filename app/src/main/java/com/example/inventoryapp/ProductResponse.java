package com.example.inventoryapp;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ProductResponse {

    private boolean error;
    private String message;
    private List<Product> products;

    // Constructor
    public ProductResponse(boolean error, String message, List<Product> products) {
        this.error = error;
        this.message = message;
        this.products = products;
    }

    // Getters and Setters
    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    // Inner Product class
    public static class Product {
        private int qr_ID;

        private double price;

        private String brand;
        private int quantity;

        @SerializedName("product_name")
        private String productName;

        @SerializedName("model_number")
        private String modelNumber;


        // Constructor
        public Product(int qr_ID,String productName, double price, String modelNumber, String brand, int quantity) {
            this.qr_ID = qr_ID;
            this.productName = productName;
            this.price = price;
            this.modelNumber = modelNumber;
            this.brand = brand;
            this.quantity = quantity;
        }

        // Getters and Setters
        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getModelNumber() {
            return modelNumber;
        }

        public void setModelNumber(String modelNumber) {
            this.modelNumber = modelNumber;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getQrId() {
            return qr_ID;
        }
    }
}
