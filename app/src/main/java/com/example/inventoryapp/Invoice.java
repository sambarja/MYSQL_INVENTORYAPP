package com.example.inventoryapp;

public class Invoice {
    private String user,modelNumber,date,activityType;
    private int Si,quantity;

    public Invoice() {
    }

    public Invoice(String user, String modelNumber, String date, String activityType, int Si, int quantity) {
        this.user = user;
        this.modelNumber = modelNumber;
        this.date = date;
        this.activityType = activityType;
        this.Si = Si;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "user='" + user + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", date='" + date + '\'' +
                ", activityType='" + activityType + '\'' +
                ", Si=" + Si +
                ", quantity=" + quantity +
                '}';
    }

    public String getUser() {
        return user;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSi() {
        return Si;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getDate() {
        return date;
    }

    public void setSi(int si) {
        Si = si;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
