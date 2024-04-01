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

import java.text.SimpleDateFormat;
import java.util.Date;

public class inbound extends AppCompatActivity {

    EditText modelNumber, si, client, quantity;
    Button done;

    CardView add,subtract;

    DrawerLayout drawerLayout;

    NavigationView navigationView;

    ImageView menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_inbound);


        InvoiceActivity dataSource = new InvoiceActivity(this);

        modelNumber = findViewById(R.id.editTextModelNumber);
        si = findViewById(R.id.editTextSi);
        client = findViewById(R.id.editTextClient);
        quantity = findViewById(R.id.quantityEditText);
        done = findViewById(R.id.doneBtn);
        subtract = findViewById(R.id.SubtractBtn);
        add = findViewById(R.id.AddBtn);
        menu = findViewById(R.id.menuImage);

        drawerLayout = findViewById(R.id.drawer_inbound);
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
                    startActivity(new Intent(inbound.this, home.class));
                }
                if (itemId == R.id.nav_inventory){
                    startActivity(new Intent(inbound.this, inventory.class));

                }
                if (itemId == R.id.nav_inbound){

                }
                if (itemId == R.id.nav_outbound){
                    startActivity(new Intent(inbound.this, outbound.class));

                }
                if (itemId == R.id.nav_add){
                    startActivity(new Intent(inbound.this, addproduct.class));

                }
                if (itemId == R.id.nav_delete){
                    startActivity(new Intent(inbound.this, deleteproduct.class));

                }
                if (itemId == R.id.nav_analytics){
                    startActivity(new Intent(inbound.this, analytics.class));

                }
                if (itemId == R.id.logout){
                    startActivity(new Intent(inbound.this, logout.class));

                }

                drawerLayout.close();

                return false;
            }
        });

        // Set input filter to allow only numbers
        quantity.setFilters(new InputFilter[]{new NumberInputFilter()});

        add.setOnClickListener(new View.OnClickListener() {
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

        subtract.setOnClickListener(new View.OnClickListener() {
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


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String modelNumberText = modelNumber.getText().toString();
                String quantityText = quantity.getText().toString();
                String siText = si.getText().toString();

                // Get current date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateValue = sdf.format(new Date());

                // Check if model number, quantity, and SI are not empty
                if (modelNumberText.isEmpty() || quantityText.isEmpty() || siText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the model number exists in the inventory table
                boolean modelNumberExists = dataSource.checkModelNumberExists(modelNumberText);

                if (modelNumberExists) {
                    // Add data to the invoice table
                    dataSource.addDataToInvoice(modelNumberText, quantityText, "inbound", "user", dateValue, Integer.parseInt(siText));

                    // Update quantity in the inventory table
                    dataSource.updateInventoryQuantity(modelNumberText, Integer.parseInt(quantityText),"inbound");

                    Toast.makeText(getApplicationContext(), "Data added successfully", Toast.LENGTH_SHORT).show();
                    modelNumber.setText("");
                    si.setText("");
                    client.setText("");
                    quantity.setText("0");

                } else {
                    // Display error message if model number doesn't exist
                    Toast.makeText(getApplicationContext(), "Model number does not exist", Toast.LENGTH_SHORT).show();
                    modelNumber.setText("");
                    si.setText("");
                    client.setText("");
                    quantity.setText("0");
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
