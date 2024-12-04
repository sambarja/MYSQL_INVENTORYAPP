package com.example.inventoryapp;

public class DefaultResponse {
    private boolean error;
    private String message;
    private String status;

    // Constructor
    public DefaultResponse(boolean error, String message, String status) {
        this.error = error;
        this.message = message;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
