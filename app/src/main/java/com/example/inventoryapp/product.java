package com.example.inventoryapp;

public class product {
    private double price;
    private int si,quantity;
    private String modelNumber,brand,productName;

    public product(int si, double price, int quantity,String modelNumber, String brand,String productName){
        this.si = si;
        this.price = price;
        this.quantity = quantity;
        this.modelNumber = modelNumber;
        this.brand = brand;
        this.productName = productName;
    }

    public product(String productName,String modelNumber,int quantity){
        this.quantity = quantity;
        this.modelNumber = modelNumber;
        this.productName = productName;
    }

    public product(){

    }

    @Override
    public String toString() {
        return "product{" +
                "si=" + si +
                ", price=" + price +
                ", quantity=" + quantity +
                ", modelNumber='" + modelNumber + '\'' +
                ", brand='" + brand + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }

    public int getSi() {
        return si;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getBrand() {
        return brand;
    }


    public String getModelNumber() {
        return modelNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }


    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setSi(int si) {
        this.si = si;
    }


}

