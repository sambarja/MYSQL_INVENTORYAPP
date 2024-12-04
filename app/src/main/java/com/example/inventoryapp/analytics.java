package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class analytics extends AppCompatActivity implements productAdapter.OnEditClickListener {

    RecyclerView recyclerView;
    private analyticsAdapter adapter;
    SearchView searchView;
    ImageView menu;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    int userId;
    String username, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_analytics);

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        menu = findViewById(R.id.menuImage);

        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_inventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        username = sharedPreferences.getString("username", "Guest");
        name = sharedPreferences.getString("name", "Unknown User");

        // Update navigation header with user info
        View headerView = navigationView.getHeaderView(0);
        TextView usernameText = headerView.findViewById(R.id.username);
        TextView nameText = headerView.findViewById(R.id.name);

        usernameText.setText(username);
        nameText.setText(name);

        // Fetch invoices from the database using Retrofit
        fetchInvoicesFromDatabase(userId);

        menu.setOnClickListener(view -> drawerLayout.open());

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(analytics.this, home.class));
            } else if (itemId == R.id.nav_inventory) {
                startActivity(new Intent(analytics.this, inventory.class));
            } else if (itemId == R.id.nav_inbound) {
                startActivity(new Intent(analytics.this, inbound.class));
            } else if (itemId == R.id.nav_outbound) {
                startActivity(new Intent(analytics.this, outbound.class));
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(analytics.this, addproduct.class));
            } else if (itemId == R.id.nav_delete) {
                startActivity(new Intent(analytics.this, deleteproduct.class));
            } else if (itemId == R.id.nav_analytics) {

            } else if (itemId == R.id.logout) {
                logout.logout(analytics.this);
            }
            drawerLayout.close();
            return false;
        });
    }

    private void fetchInvoicesFromDatabase(int userId) {
        Api api = RetrofitClient.getInstance(getApplicationContext()).getApi();
        Call<InvoiceResponse> call = api.getAllInvoicesByUser(userId);

        call.enqueue(new Callback<InvoiceResponse>() {
            @Override
            public void onResponse(Call<InvoiceResponse> call, Response<InvoiceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<InvoiceResponse.Invoice> invoiceList = response.body().getInvoices();
                    Log.d("invoices", "Fetched invoices: " + invoiceList.size());

                    Map<String, List<InvoiceResponse.Invoice>> invoicesByMonth = groupInvoicesByMonth(invoiceList);
                    SessionData.getInstance().invoicesByMonth = invoicesByMonth;

                    // Log grouped invoices
                    for (Map.Entry<String, List<InvoiceResponse.Invoice>> entry : invoicesByMonth.entrySet()) {
                        Log.d("invoices_monthly", "Month: " + entry.getKey());
                    }

                    // Update the RecyclerView
                    updateRecyclerView(invoicesByMonth);
                } else {
                    Log.e("invoices", "Failed to fetch invoices. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<InvoiceResponse> call, Throwable t) {
                Log.e("invoices", "Error fetching invoices: ", t);
            }
        });
    }

    private void updateRecyclerView(Map<String, List<InvoiceResponse.Invoice>> invoicesByMonth) {
        List<String> monthNames = new ArrayList<>(invoicesByMonth.keySet());
        if (adapter == null) {
            adapter = new analyticsAdapter(monthNames, analytics.this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(monthNames);
        }
    }

    private void handleNavigation(int itemId) {
        if (itemId == R.id.nav_home) {
            startActivity(new Intent(analytics.this, home.class));
        } else if (itemId == R.id.nav_inbound) {
            startActivity(new Intent(analytics.this, inbound.class));
        } else if (itemId == R.id.nav_outbound) {
            startActivity(new Intent(analytics.this, outbound.class));
        } else if (itemId == R.id.nav_add) {
            startActivity(new Intent(analytics.this, addproduct.class));
        } else if (itemId == R.id.nav_delete) {
            startActivity(new Intent(analytics.this, deleteproduct.class));
        } else if (itemId == R.id.nav_analytics) {
            startActivity(new Intent(analytics.this, analytics.class));
        } else if (itemId == R.id.logout) {
            startActivity(new Intent(analytics.this, logout.class));
        }
    }

    @Override
    public void onEditClick(int position) {
        // Handle edit action here
    }

    public static Map<String, List<InvoiceResponse.Invoice>> groupInvoicesByMonth(List<InvoiceResponse.Invoice> invoices) {
        Map<String, List<InvoiceResponse.Invoice>> invoicesByMonth = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (InvoiceResponse.Invoice invoice : invoices) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(invoice.getDate(), formatter);
                String monthName = dateTime.getMonth().toString(); // Get month name

                invoicesByMonth.computeIfAbsent(monthName, k -> new ArrayList<>()).add(invoice);
            } catch (Exception e) {
                Log.e("Date Parsing", "Failed to parse date: " + invoice.getDate(), e);
            }
        }

        return invoicesByMonth;
    }
}
