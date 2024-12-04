package com.example.inventoryapp;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionData {
    private static SessionData instance;

    // Store user session data
    public UserResponse.User user;
    public Map<String, List<InvoiceResponse.Invoice>> invoicesByMonth;  // Store invoices grouped by month
    public String selectedMonth;  // Currently selected month
    public List<InvoiceResponse.Invoice> selectedInvoices;  // Invoices for the selected month

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

    // Method to set user session data after fetching it from the API
    public void setUserSession(UserResponse.User user) {
        this.user = user;
    }

    // Optional: Method to clear session if user logs out
    public void clearSession() {
        this.user = null;
        this.invoicesByMonth = null;
        this.selectedMonth = null;
        this.selectedInvoices = null;
    }

    // Method to fetch user session from the API (MySQL)
    public void fetchUserSession(Context context, String username) {
        Api apiService = RetrofitClient.getInstance(context).getApi();
        Call<UserResponse> call = apiService.getUserByUsername(username);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse.User user = response.body().getUser();
                    setUserSession(user); // Store the user session in the singleton
                } else {
                    Log.e("SessionData", "Failed to fetch user session.");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("SessionData", "Error fetching user session: " + t.getMessage());
            }
        });
    }
}
