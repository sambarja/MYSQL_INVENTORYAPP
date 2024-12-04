package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class outbound extends AppCompatActivity {

    EditText modelNumber, si,  quantity;
    Button done;
    CardView add, subtract;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu;
    int userId;
    String username, name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_outbound);

        // Initialize UI elements
        modelNumber = findViewById(R.id.editTextModelNumber);
        si = findViewById(R.id.editTextSi);
        quantity = findViewById(R.id.quantityEditText);
        done = findViewById(R.id.doneBtn);
        subtract = findViewById(R.id.SubtractBtn);
        add = findViewById(R.id.AddBtn);
        menu = findViewById(R.id.menuImage);

        drawerLayout = findViewById(R.id.drawer_outbound);
        navigationView = findViewById(R.id.nav_view);

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

        menu.setOnClickListener(view -> drawerLayout.open());


        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(outbound.this, home.class));
            } else if (itemId == R.id.nav_inventory) {
                startActivity(new Intent(outbound.this, inventory.class));
            } else if (itemId == R.id.nav_inbound) {
                // Already in the inbound activity
            } else if (itemId == R.id.nav_outbound) {
                startActivity(new Intent(outbound.this, outbound.class));
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(outbound.this, addproduct.class));
            } else if (itemId == R.id.nav_delete) {
                startActivity(new Intent(outbound.this, deleteproduct.class));
            } else if (itemId == R.id.nav_analytics) {
                startActivity(new Intent(outbound.this, analytics.class));
            } else if (itemId == R.id.logout) {
                startActivity(new Intent(outbound.this, logout.class));
            }
            drawerLayout.close();
            return false;
        });

        // Quantity Input Filter: Allow only numbers
        quantity.setFilters(new InputFilter[]{new NumberInputFilter()});

        // Add button to increment quantity
        add.setOnClickListener(view -> {
            String quantityText = quantity.getText().toString();
            if (!quantityText.isEmpty()) {
                int initialQuantity = Integer.parseInt(quantityText);
                quantity.setText(String.valueOf(initialQuantity + 1));
            }
        });

        // Subtract button to decrement quantity
        subtract.setOnClickListener(view -> {
            String quantityText = quantity.getText().toString();
            if (!quantityText.isEmpty()) {
                int initialQuantity = Integer.parseInt(quantityText);
                quantity.setText(String.valueOf(Math.max(initialQuantity - 1, 0)));
            }
        });

        // Done button to submit outbound transaction
        done.setOnClickListener(view -> {
            String modelNumberText = modelNumber.getText().toString();
            String quantityText = quantity.getText().toString();
            String siText = si.getText().toString();

            // Validate input fields
            if (modelNumberText.isEmpty() || quantityText.isEmpty() || siText.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create invoice with outbound activity type
            createInvoice(Integer.parseInt(siText), modelNumberText, userId,
                    Integer.parseInt(quantityText), "outbound");
        });
    }

    private void createInvoice(int siInput, String modelNumberInput, int userIdInput, int quantityInput, String activityType) {
        Api apiService = RetrofitClient.getInstance(getApplicationContext()).getApi();
        Call<DefaultResponse> call = apiService.createInvoice(siInput, modelNumberInput, userIdInput, quantityInput, activityType);

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    Toast.makeText(outbound.this, "Invoice created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Unknown error";
                    Toast.makeText(outbound.this, "Failed to create invoice: " + message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(outbound.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        clearFields();
    }

    private void clearFields() {
        modelNumber.setText("");
        si.setText("");
        quantity.setText("0");
    }

    // Input Filter for allowing only numbers
    private static class NumberInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    }
}
