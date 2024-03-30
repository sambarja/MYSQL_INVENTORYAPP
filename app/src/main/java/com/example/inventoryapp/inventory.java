package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.List;

import android.view.MenuItem;
import android.view.View;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.ImageView;
import java.util.ArrayList;
import android.text.TextUtils;

import com.google.android.material.navigation.NavigationView;


public class inventory extends AppCompatActivity implements productAdapter.OnEditClickListener {

    RecyclerView recyclerView;
    private productAdapter adapter;
    private InventoryDataSource dataSource;
    SearchView searchView;
    ImageView menu;

    DrawerLayout drawerLayout;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_inventory);


        dataSource = new InventoryDataSource(this);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        menu = findViewById(R.id.menuImage);

        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_inventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<product> productList = dataSource.fetchDataFromDatabase();

        updateRecyclerView(productList);

        // Set up the SearchView
        SearchView searchView = findViewById(R.id.searchView);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        filter(newText,productList);
                        return true;
                    }
                });
            }
                                      }

        );

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

                    startActivity(new Intent(inventory.this, home.class));
                }
                if (itemId == R.id.nav_inventory){

                }
                if (itemId == R.id.nav_inbound){
                    startActivity(new Intent(inventory.this, inbound.class));

                }
                if (itemId == R.id.nav_outbound){
                    startActivity(new Intent(inventory.this, outbound.class));

                }
                if (itemId == R.id.nav_add){
                    startActivity(new Intent(inventory.this, addproduct.class));

                }
                if (itemId == R.id.nav_delete){
                    startActivity(new Intent(inventory.this, deleteproduct.class));

                }
                if (itemId == R.id.nav_analytics){
                    startActivity(new Intent(inventory.this, Analytics.class));

                }
                if (itemId == R.id.logout){
                    startActivity(new Intent(inventory.this, logout.class));

                }

                drawerLayout.close();

                return false;
            }
        });


    }

    private void filter(String query,List<product> productList) {
        List<product> filteredList = new ArrayList<>();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(productList); // If the query is empty, show all products
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (product product : productList) {
                if (product.getModelNumber().toLowerCase().contains(lowerCaseQuery) ||
                        product.getProductName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(product);
                }
            }
        }
        adapter.updateData(filteredList); // Update the RecyclerView with the filtered list
    }

    private void updateRecyclerView(List<product> productList) {

        // Print the inventory products
        System.out.println("Inventory Products:");
        for (product product : productList) {
            System.out.println("Product Name: " + product.getProductName() +
                    ", Model Number: " + product.getModelNumber() +
                    ", Quantity: " + product.getQuantity() +
                    ", Price: " + product.getPrice());
        }


        if (adapter == null) {
            adapter = new productAdapter(productList);
            adapter.setOnEditClickListener(this); // Set edit click listener
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(productList);
        }
    }



    @Override
    public void onEditClick(int position) {
        // Handle edit action here
        product productToEdit = adapter.productList.get(position);
        showEditPopup(productToEdit);
    }


    private void showEditPopup(product productToEdit) {
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
                    dataSource.open();

                    // Check if the new model number is unique
                    if (!newModelNumber.equals(previousModelNumber) && dataSource.checkModelNumberExists(newModelNumber)) {
                        Toast.makeText(getApplicationContext(), "Model number already exists. Please enter a unique model number.", Toast.LENGTH_SHORT).show();
                        return; // Stop further execution
                    }

                    dataSource.close();

                    Invoice invoice = new Invoice();

                    // Update product details in database
                    productToEdit.setProductName(newName);
                    productToEdit.setModelNumber(newModelNumber);
                    invoice.setModelNumber(newModelNumber);
                    productToEdit.setPrice(newPrice);
                    dataSource.updateProduct(productToEdit, previousModelNumber); // Pass the previous model number

                    List<product> updatedProductList = dataSource.fetchDataFromDatabase();
                    // Update RecyclerView
                    updateRecyclerView(updatedProductList);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }



}


