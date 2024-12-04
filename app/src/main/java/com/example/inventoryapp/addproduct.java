package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class addproduct extends AppCompatActivity {

    EditText modelNumber, productName, si, brand, price, quantity;
    Button add;
    CardView subtractbtn, addbtn;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu;

    int userId;
    String username, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_addproduct);

        // Initialize UI elements
        modelNumber = findViewById(R.id.editTextModelNumber);
        si = findViewById(R.id.editTextSi);
        productName = findViewById(R.id.editTextProductName);
        quantity = findViewById(R.id.quantityEditText);
        brand = findViewById(R.id.editTextBrand);
        price = findViewById(R.id.editTextPrice);
        add = findViewById(R.id.doneBtn);
        subtractbtn = findViewById(R.id.SubtractBtn);
        addbtn = findViewById(R.id.AddBtn);
        menu = findViewById(R.id.menuImage);
        drawerLayout = findViewById(R.id.drawer_addproduct);
        navigationView = findViewById(R.id.nav_view);

        // Update navigation header with user info
        View headerView = navigationView.getHeaderView(0);
        TextView usernameText = headerView.findViewById(R.id.username);
        TextView nameText = headerView.findViewById(R.id.name);

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        username = sharedPreferences.getString("username", "Guest");
        name = sharedPreferences.getString("name", "Unknown User");

        usernameText.setText(username);
        nameText.setText(name);

        // Drawer menu button
        menu.setOnClickListener(view -> drawerLayout.open());

        // Navigation menu item selection
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            Intent intent = null;

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(addproduct.this, home.class));
            }
            if (itemId == R.id.nav_inventory) {
                startActivity(new Intent(addproduct.this, inventory.class));
            }
            if (itemId == R.id.nav_outbound) {
                startActivity(new Intent(addproduct.this, outbound.class));
            }
            if (itemId == R.id.nav_add) {
                // Handle Add Product Navigation (currently no action)
            }
            if (itemId == R.id.nav_delete) {
                startActivity(new Intent(addproduct.this, deleteproduct.class));
            }
            if (itemId == R.id.nav_analytics) {
                startActivity(new Intent(addproduct.this, analytics.class));
            }
            if (itemId == R.id.logout) {
                startActivity(new Intent(addproduct.this, logout.class));
            }

            drawerLayout.close();
            return false;
        });

        // Set input filter to allow only numbers
        quantity.setFilters(new InputFilter[]{new NumberInputFilter()});

        // Quantity increment button
        addbtn.setOnClickListener(view -> {
            String quantityText = quantity.getText().toString();
            if (!quantityText.isEmpty()) {
                int initialQuantity = Integer.parseInt(quantityText);
                quantity.setText(String.valueOf(initialQuantity + 1));
            }
        });

        // Quantity decrement button
        subtractbtn.setOnClickListener(view -> {
            String quantityText = quantity.getText().toString();
            if (!quantityText.isEmpty()) {
                int initialQuantity = Integer.parseInt(quantityText);
                quantity.setText(String.valueOf(Math.max(initialQuantity - 1, 0)));
            }
        });

        // Add product button logic
        add.setOnClickListener(view -> {
            if (username == null) {
                Toast.makeText(this, "User not available, cannot add product", Toast.LENGTH_SHORT).show();
                return;
            }

            String modelNumberText = modelNumber.getText().toString();
            String quantityText = quantity.getText().toString();
            String nameString = productName.getText().toString();
            String siText = si.getText().toString();
            String priceText = price.getText().toString();
            String brandText = brand.getText().toString();

            if (modelNumberText.isEmpty() || quantityText.isEmpty() || siText.isEmpty() || nameString.isEmpty() || priceText.isEmpty() || brandText.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the model number already exists
            checkModelNumberExists(modelNumberText, userId, exists -> {
                if (exists) {
                    Toast.makeText(getApplicationContext(), "Product Already Exists", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    addProductToDatabase(modelNumberText, nameString, priceText, brandText, quantityText, siText);
                }
            });
        });
    }

    private void checkModelNumberExists(String modelNumber, int userId, ModelNumberCheckCallback callback) {
        Api apiService = RetrofitClient.getInstance(getApplicationContext()).getApi();
        Call<ProductResponse> call = apiService.getUserProducts(userId);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean exists = false;
                    for (ProductResponse.Product product : response.body().getProducts()) {
                        if (product.getModelNumber() != null && product.getModelNumber().equals(modelNumber)) {
                            exists = true;
                            break;
                        }
                    }
                    callback.onResult(exists);
                } else {
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                callback.onResult(false);
            }
        });
    }

    private void addProductToDatabase(String modelNumber, String productName, String price, String brand, String quantity, String si) {
        Api apiService = RetrofitClient.getInstance(getApplicationContext()).getApi();
        Call<DefaultResponse> call = apiService.createProduct(
                productName,
                Double.parseDouble(price),
                modelNumber,
                brand,
                Integer.parseInt(quantity),
                userId
        );

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplicationContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
                    createInvoice(si, modelNumber, userId, quantity, "inbound");
                } else {
                    Log.e("AddProduct", "Failed to add product. Response code: " + response.code());
                    Log.e("AddProduct", "Response body: " + response.message());
                    Toast.makeText(getApplicationContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Log.e("AddProduct", "API request failed: " + t.getMessage(), t);
                Toast.makeText(getApplicationContext(), "Failed to add product: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        clearFields();
    }


    private void createInvoice(String si, String model_number, int user_id, String quantity, String activity_type) {
        // Check if SI is valid
        if (si == null || si.isEmpty() || !si.matches("\\d+")) {
            Toast.makeText(getApplicationContext(), "Invalid SI number", Toast.LENGTH_SHORT).show();
            return;
        }

        Api apiService = RetrofitClient.getInstance(getApplicationContext()).getApi();
        Call<DefaultResponse> call = apiService.createInvoice(
                Integer.parseInt(si),
                model_number,
                user_id,
                Integer.parseInt(quantity),
                activity_type
        );

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplicationContext(), "Invoice created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to create invoice", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to create invoice", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        modelNumber.setText("");
        productName.setText("");
        si.setText("");
        price.setText("");
        quantity.setText("");
        brand.setText("");
    }

    // Input filter to allow only numeric values for quantity
    public class NumberInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.length() > 0 && !Character.isDigit(source.charAt(0))) {
                return "";
            }
            return null;
        }
    }

    interface ModelNumberCheckCallback {
        void onResult(boolean exists);
    }
}
