package com.example.inventoryapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InvoiceResponse {

    private boolean error;
    private String message;
    private List<Invoice> invoices;

    // Constructor
    public InvoiceResponse(boolean error, String message, List<Invoice> invoices) {
        this.error = error;
        this.message = message;
        this.invoices = invoices;
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

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    // Inner Invoice class
    public static class Invoice {
        private int si;

        @SerializedName("model_number")
        private String modelNumber;
        private int userId;
        private int quantity;

        @SerializedName("activity")
        @Expose(serialize = true, deserialize = true)
        private String activity;

        private String date;  // Add the date field




        // Constructor
        public Invoice(int si, String modelNumber, int userId, int quantity, String activity, String date) {
            this.si = si;
            this.modelNumber = modelNumber;
            this.userId = userId;
            this.quantity = quantity;
            this.activity = activity;
            this.date = date;  // Initialize date
        }

        // Getters and Setters
        public int getSi() {
            return si;
        }

        public void setSi(int si) {
            this.si = si;
        }

        public String getModelNumber() {
            return modelNumber;
        }

        public void setModelNumber(String modelNumber) {
            this.modelNumber = modelNumber;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getActivityType() {
            return activity;
        }

        public void setActivityType(String activityType) {
            this.activity= activityType;
        }

        // Getters and Setters for date
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
