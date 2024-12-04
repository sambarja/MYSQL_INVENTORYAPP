package com.example.inventoryapp;

import com.google.gson.annotations.SerializedName;

public class UserResponse {

    private boolean error;
    private String message;
    private User user;

    // Constructor
    public UserResponse(boolean error, String message, User user) {
        this.error = error;
        this.message = message;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Inner User class
    public static class User {

        @SerializedName("username")
        private String username;
        private String name;
        private int id;  // Add an ID field

        // Constructor
        public User(String username, String name, int id) {
            this.username = username;
            this.name = name;
            this.id = id;  // Initialize the ID field
        }

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;  // Return the ID value
        }

        public void setId(int id) {
            this.id = id;  // Set the ID value
        }
    }
}
