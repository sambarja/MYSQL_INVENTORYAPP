package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class addproduct extends AppCompatActivity {

    EditText modelNumber, productName, si, brand, price, quantity;
    Button add;

    CardView subtractbtn, addbtn;
    DrawerLayout drawerLayout;

    NavigationView navigationView;

    ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_addproduct);


        InvoiceActivity dataSource = new InvoiceActivity(this);
        InventoryDataSource lol = new InventoryDataSource(this);

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
                    startActivity(new Intent(addproduct.this, home.class));
                }
                if (itemId == R.id.nav_inventory){
                    startActivity(new Intent(addproduct.this, inventory.class));

                }
                if (itemId == R.id.nav_inbound){
                    startActivity(new Intent(addproduct.this, inbound.class));
                }
                if (itemId == R.id.nav_outbound){
                    startActivity(new Intent(addproduct.this, outbound.class));

                }
                if (itemId == R.id.nav_add){


                }
                if (itemId == R.id.nav_delete){
                    startActivity(new Intent(addproduct.this, deleteproduct.class));

                }
                if (itemId == R.id.nav_analytics){
                    startActivity(new Intent(addproduct.this, Analytics.class));

                }
                if (itemId == R.id.logout){
                    startActivity(new Intent(addproduct.this, logout.class));

                }

                drawerLayout.close();

                return false;
            }
        });

        // Set input filter to allow only numbers
        quantity.setFilters(new InputFilter[]{new NumberInputFilter()});

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from the quantity EditText
                String quantityText = quantity.getText().toString();

                // Check if the quantity text is not empty
                if (!quantityText.isEmpty()) {
                    // Increment quantity by 1
                    int initialQuantity = Integer.parseInt(quantityText);
                    int finalQuantityM = initialQuantity + 1;
                    quantity.setText(String.valueOf(finalQuantityM));
                }
            }
        });

        subtractbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from the quantity EditText
                String quantityText = quantity.getText().toString();

                // Check if the quantity text is not empty
                if (!quantityText.isEmpty()) {
                    // Decrement quantity by 1, ensuring it doesn't go below 0
                    int initialQuantity = Integer.parseInt(quantityText);
                    int finalQuantityM = Math.max(initialQuantity - 1, 0);
                    quantity.setText(String.valueOf(finalQuantityM));
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String modelNumberText = modelNumber.getText().toString();
                String quantityText = quantity.getText().toString();
                String nameText = productName.getText().toString();
                String siText = si.getText().toString();
                String priceText = price.getText().toString();
                String brandText = brand.getText().toString();

                // Check if model number, quantity, and SI are not empty
                if (modelNumberText.isEmpty() || quantityText.isEmpty() || siText.isEmpty() || nameText.isEmpty() || priceText.isEmpty() || brandText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the model number exists in the inventory table
                boolean modelNumberExists = dataSource.checkModelNumberExists(modelNumberText);

                if (modelNumberExists) {
                    // Display error message if model number exists
                    Toast.makeText(getApplicationContext(), "Product Already Exists", Toast.LENGTH_SHORT).show();
                    modelNumber.setText("");
                    productName.setText("");
                    si.setText("");
                    brand.setText("");
                    price.setText("");
                    quantity.setText("0");
                } else {
                    // Add data to the inventory table
                    int quantityValue = Integer.parseInt(quantityText);
                    int siValue = Integer.parseInt(siText);
                    double priceValue = Double.parseDouble(priceText);

                    product newProduct = new product(siValue, priceValue, quantityValue, modelNumberText, brandText, nameText);

                    dataSource.addProduct(newProduct);

                    Toast.makeText(getApplicationContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
                    modelNumber.setText("");
                    productName.setText("");
                    si.setText("");
                    brand.setText("");
                    price.setText("");
                    quantity.setText("0");

                    List<product> productList = lol.fetchDataFromDatabase();
                    System.out.println("Inventory:");
                    for (product product : productList) {
                        System.out.println("Product Name: " + product.getProductName() +
                                ", Model Number: " + product.getModelNumber() +
                                ", Quantity: " + product.getQuantity() +
                                ", Price: " + product.getPrice());
                    }
                }
            }
        });
    }

    private class NumberInputFilter implements InputFilter {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // Allow only digits
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    }
}
