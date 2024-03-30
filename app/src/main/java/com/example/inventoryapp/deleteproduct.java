package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class deleteproduct extends AppCompatActivity {

    EditText modelNumber;
    Button delete;

    DrawerLayout drawerLayout;

    NavigationView navigationView;

    ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_deleteproduct);


        InvoiceActivity dataSource = new InvoiceActivity(this);

        modelNumber = findViewById(R.id.editTextModelNumber);
        delete = findViewById(R.id.doneBtn);

        menu = findViewById(R.id.menuImage);

        drawerLayout = findViewById(R.id.drawer_deleteproduct);
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
                    startActivity(new Intent(deleteproduct.this, home.class));
                }
                if (itemId == R.id.nav_inventory){
                    startActivity(new Intent(deleteproduct.this, inventory.class));

                }
                if (itemId == R.id.nav_inbound){
                    startActivity(new Intent(deleteproduct.this, inbound.class));
                }
                if (itemId == R.id.nav_outbound){
                    startActivity(new Intent(deleteproduct.this, outbound.class));
                }
                if (itemId == R.id.nav_add){
                    startActivity(new Intent(deleteproduct.this, addproduct.class));

                }
                if (itemId == R.id.nav_delete){


                }
                if (itemId == R.id.nav_analytics){
                    startActivity(new Intent(deleteproduct.this, Analytics.class));

                }
                if (itemId == R.id.logout){
                    startActivity(new Intent(deleteproduct.this, logout.class));

                }

                drawerLayout.close();

                return false;
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String modelNumberText = modelNumber.getText().toString();

                if (modelNumberText.isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the model number exists in the inventory table
                boolean modelNumberExists = dataSource.checkModelNumberExists(modelNumberText);

                if(modelNumberExists){
                    dataSource.removeProduct(modelNumberText);
                    Toast.makeText(getApplicationContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    modelNumber.setText("");
                }else {
                    Toast.makeText(getApplicationContext(), "Product does not exist", Toast.LENGTH_SHORT).show();
                    modelNumber.setText("");
                }
            }

            });

    }
}