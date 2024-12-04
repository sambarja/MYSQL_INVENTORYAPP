package com.example.inventoryapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.annotations.SerializedName;


import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class inventory extends AppCompatActivity implements productAdapter.OnEditClickListener {

    RecyclerView recyclerView;
    private productAdapter adapter;
    SearchView searchView;
    ImageView menu;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    int userId;
    String username, name;

    private List<ProductResponse.Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_inventory);

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

        // Fetch products by user ID
        fetchUserProducts(userId);

        // Set up the SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        menu.setOnClickListener(view -> drawerLayout.open());

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(inventory.this, home.class));
            } else if (itemId == R.id.nav_inventory) {
                startActivity(new Intent(inventory.this, inventory.class));
            } else if (itemId == R.id.nav_inbound) {
                // Already in the inbound activity
            } else if (itemId == R.id.nav_outbound) {
                startActivity(new Intent(inventory.this, outbound.class));
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(inventory.this, addproduct.class));
            } else if (itemId == R.id.nav_delete) {
                startActivity(new Intent(inventory.this, deleteproduct.class));
            } else if (itemId == R.id.nav_analytics) {
                startActivity(new Intent(inventory.this, analytics.class));
            } else if (itemId == R.id.logout) {
                startActivity(new Intent(inventory.this, logout.class));
            }
            drawerLayout.close();
            return false;
        });
    }

    private void fetchUserProducts(int userId) {
        Api apiService = RetrofitClient.getInstance(getApplicationContext()).getApi();
        Call<ProductResponse> call = apiService.getUserProducts(userId);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body().getProducts();
                    // Log the products for debugging
                    for (ProductResponse.Product product : productList) {
                        Log.d("ProductAdapter", "Product Name: " + product.getProductName());
                        Log.d("ProductAdapter", "Model Number: " + product.getModelNumber());
                    }
                    updateRecyclerView();
                } else {
                    Toast.makeText(inventory.this, "Failed to load products: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(inventory.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String query) {
        List<ProductResponse.Product> filteredList = new ArrayList<>();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(productList); // Show all products if query is empty
        } else {
            for (ProductResponse.Product product : productList) {
                if (product.getProductName().toLowerCase().contains(query.toLowerCase()) ||
                        product.getModelNumber().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        }
        adapter.updateData(filteredList); // Update the RecyclerView with the filtered list
    }

    private void updateRecyclerView() {
        if (adapter == null) {
            adapter = new productAdapter(this, productList);
            adapter.setOnEditClickListener(this); // Set edit click listener
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(productList);
        }
    }

    @Override
    public void onEditClick(int position) {
        ProductResponse.Product productToEdit = adapter.productList.get(position);
        showEditPopup(productToEdit);
    }

    private void showEditPopup(ProductResponse.Product productToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.edit, null);

        EditText editName = view.findViewById(R.id.productNameEditText);
        EditText editModelNumber = view.findViewById(R.id.modelNumberEditText);
        EditText editPrice = view.findViewById(R.id.priceEditText);

        // Set current values
        editName.setText(productToEdit.getProductName());
        editModelNumber.setText(productToEdit.getModelNumber());
        editPrice.setText(String.valueOf(productToEdit.getPrice()));

        String previousModelNumber = productToEdit.getModelNumber(); // Store the previous model number

        builder.setView(view)
                .setTitle("Edit Product")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = editName.getText().toString();
                    String newModelNumber = editModelNumber.getText().toString();
                    double newPrice = Double.parseDouble(editPrice.getText().toString()); // Convert to double

                    // Update the product and notify the RecyclerView
                    productToEdit.setProductName(newName);
                    productToEdit.setModelNumber(newModelNumber);
                    productToEdit.setPrice(newPrice);

                    // Call API to update product
                    updateProductDetails(productToEdit);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateProductDetails(ProductResponse.Product productToEdit) {
        Api apiService = RetrofitClient.getInstance(getApplicationContext()).getApi();

        // Use @FormUrlEncoded request
        Call<DefaultResponse> call = apiService.updateProduct(
                productToEdit.getQrId(), // Ensure this is properly fetched from your product
                productToEdit.getProductName(),
                productToEdit.getPrice(),
                productToEdit.getModelNumber()
        );

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    Toast.makeText(inventory.this, "Product updated successfully!", Toast.LENGTH_SHORT).show();
                    fetchUserProducts(userId); // Refresh the product list
                } else {
                    Toast.makeText(inventory.this, "Failed to update product.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(inventory.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
