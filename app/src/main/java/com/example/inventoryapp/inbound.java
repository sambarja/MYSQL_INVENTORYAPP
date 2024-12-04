package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class inbound extends AppCompatActivity {

    EditText modelNumber, si,quantity;
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
        setContentView(R.layout.menu_inbound);

        // Initialize UI elements
        modelNumber = findViewById(R.id.editTextModelNumber);
        si = findViewById(R.id.editTextSi);
        quantity = findViewById(R.id.quantityEditText);
        done = findViewById(R.id.doneBtn);
        subtract = findViewById(R.id.SubtractBtn);
        add = findViewById(R.id.AddBtn);
        menu = findViewById(R.id.menuImage);

        drawerLayout = findViewById(R.id.drawer_inbound);
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

        // Drawer menu button
        menu.setOnClickListener(view -> drawerLayout.open());

        // Navigation menu item selection
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            Intent intent = null;

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(inbound.this, home.class));
            }
            if (itemId == R.id.nav_inventory) {
                startActivity(new Intent(inbound.this, inventory.class));
            }
            if (itemId == R.id.nav_outbound) {
                startActivity(new Intent(inbound.this, outbound.class));
            }
            if (itemId == R.id.nav_add) {
                startActivity(new Intent(inbound.this, addproduct.class));
            }
            if (itemId == R.id.nav_delete) {
                startActivity(new Intent(inbound.this, deleteproduct.class));
            }
            if (itemId == R.id.nav_analytics) {
                startActivity(new Intent(inbound.this, analytics.class));
            }
            if (itemId == R.id.logout) {
                logout.logout(inbound.this);
            }

            drawerLayout.close();
            return false;
        });

        // Input filter to allow only numbers
        quantity.setFilters(new InputFilter[]{new NumberInputFilter()});

        // Quantity increment button
        add.setOnClickListener(view -> {
            String quantityText = quantity.getText().toString();
            if (!quantityText.isEmpty()) {
                int initialQuantity = Integer.parseInt(quantityText);
                quantity.setText(String.valueOf(initialQuantity + 1));
            }
        });

        // Quantity decrement button
        subtract.setOnClickListener(view -> {
            String quantityText = quantity.getText().toString();
            if (!quantityText.isEmpty()) {
                int initialQuantity = Integer.parseInt(quantityText);
                quantity.setText(String.valueOf(Math.max(initialQuantity - 1, 0)));
            }
        });

        // Done button for creating invoice
        done.setOnClickListener(view -> {
            if (userId == -1) {
                Toast.makeText(this, "User not available, cannot create invoice", Toast.LENGTH_SHORT).show();
                return;
            }

            String modelNumberText = modelNumber.getText().toString();
            String quantityText = quantity.getText().toString();
            String siText = si.getText().toString();

            if (modelNumberText.isEmpty() || quantityText.isEmpty() || siText.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                return;
            }

            int siValue = Integer.parseInt(siText);
            int quantityValue = Integer.parseInt(quantityText);

            createInvoice(siValue, modelNumberText, userId, quantityValue, "inbound");
        });
    }

    private void createInvoice(int siInput, String modelNumberInput, int userIdInput, int quantityInput, String activityType) {
        Api apiService = RetrofitClient.getInstance(getApplicationContext()).getApi();
        Call<DefaultResponse> call = apiService.createInvoice(siInput, modelNumberInput, userIdInput, quantityInput, activityType);

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    Toast.makeText(inbound.this, "Invoice created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "Unknown error";
                    Toast.makeText(inbound.this, "Failed to create invoice: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(inbound.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        clearFields();
    }


    private void clearFields() {
        modelNumber.setText("");
        si.setText("");
        quantity.setText("0");
    }

    private class NumberInputFilter implements InputFilter {
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
