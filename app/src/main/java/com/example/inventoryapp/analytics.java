package com.example.inventoryapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class analytics extends AppCompatActivity implements productAdapter.OnEditClickListener {

    RecyclerView recyclerView;
    private analyticsAdapter adapter;
    private InventoryDataSource dataSource;
    SearchView searchView;
    ImageView menu;

    DrawerLayout drawerLayout;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_analytics);


        dataSource = new InventoryDataSource(this);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        menu = findViewById(R.id.menuImage);



        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_inventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        View headerView = navigationView.getHeaderView(0);
        TextView usernameText = headerView.findViewById(R.id.username);
        TextView nameText = headerView.findViewById(R.id.name);

        User user = SessionData.getInstance().user;
        usernameText.setText(user.getUsername());
        nameText.setText(user.getName());

        List<Invoice> invoiceList = dataSource.fetchInvoices();
        Log.d("invoices", String.valueOf(invoiceList.size()));
        Map<String, List<Invoice>> invoicesByMonth = groupInvoicesByMonth(invoiceList);
        SessionData.getInstance().invoicesByMonth = invoicesByMonth;
        // Print the result
        for (Map.Entry<String, List<Invoice>> entry : invoicesByMonth.entrySet()) {
            Log.d("invoices_monthly", "Month: " + entry.getKey());
        }
        updateRecyclerView(invoicesByMonth);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.nav_home){

                    startActivity(new Intent(analytics.this, home.class));
                }
                if (itemId == R.id.nav_inventory){

                }
                if (itemId == R.id.nav_inbound){
                    startActivity(new Intent(analytics.this, inbound.class));

                }
                if (itemId == R.id.nav_outbound){
                    startActivity(new Intent(analytics.this, outbound.class));

                }
                if (itemId == R.id.nav_add){
                    startActivity(new Intent(analytics.this, addproduct.class));

                }
                if (itemId == R.id.nav_delete){
                    startActivity(new Intent(analytics.this, deleteproduct.class));

                }
                if (itemId == R.id.nav_analytics){
                    startActivity(new Intent(analytics.this, analytics.class));

                }
                if (itemId == R.id.logout){
                    startActivity(new Intent(analytics.this, logout.class));

                }

                drawerLayout.close();

                return false;
            }
        });


    }

    private void updateRecyclerView (Map<String, List<Invoice>> invoicesByMonth) {
        List<String> monthNames = new ArrayList<>(invoicesByMonth.keySet());
        if (adapter == null) {
            adapter = new analyticsAdapter(monthNames, analytics.this);
            recyclerView.setAdapter(adapter);
        }
    }



    @Override
    public void onEditClick(int position) {
        // Handle edit action here
    }


    public static Map<String, List<Invoice>> groupInvoicesByMonth(List<Invoice> invoices) {
        Map<String, List<Invoice>> invoicesByMonth = new HashMap<>();

        // Iterate through the invoices
        for (Invoice invoice : invoices) {
            // Parse the date string into a LocalDate object
            LocalDate date = LocalDate.parse(invoice.getDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            // Get the month name from the date
            String monthName = date.getMonth().toString();

            // Check if the month already exists in the map
            if (invoicesByMonth.containsKey(monthName)) {
                // If it exists, add the current invoice to the list of invoices for that month
                invoicesByMonth.get(monthName).add(invoice);
            } else {
                // If it doesn't exist, create a new list and add the current invoice to it
                List<Invoice> invoicesForMonth = new ArrayList<>();
                invoicesForMonth.add(invoice);
                invoicesByMonth.put(monthName, invoicesForMonth);
            }
        }

        return invoicesByMonth;
    }



}


