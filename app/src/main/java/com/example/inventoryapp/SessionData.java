package com.example.inventoryapp;

import java.util.List;
import java.util.Map;

public class SessionData {
    private static SessionData instance;

    public User user;
    public Map<String, List<Invoice>> invoicesByMonth;
    public String selectedMonth;

    // Private constructor to prevent instantiation from outside
    private SessionData() {
        // Initialization code, if any
    }

    // Public static method to get the single instance of the class
    public static SessionData getInstance() {
        // Create the instance if it doesn't exist
        if (instance == null) {
            instance = new SessionData();
        }
        return instance;
    }
}
