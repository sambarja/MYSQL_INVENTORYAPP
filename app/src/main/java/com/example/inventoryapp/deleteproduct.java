package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class deleteproduct extends AppCompatActivity {

    EditText modelNumber;
    Button delete;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu;

    int userId;
    String username, name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_deleteproduct);

        modelNumber = findViewById(R.id.editTextModelNumber);
        delete = findViewById(R.id.doneBtn);

        menu = findViewById(R.id.menuImage);

        drawerLayout = findViewById(R.id.drawer_deleteproduct);
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
                startActivity(new Intent(deleteproduct.this, home.class));
            } else if (itemId == R.id.nav_inventory) {
                startActivity(new Intent(deleteproduct.this, inventory.class));
            } else if (itemId == R.id.nav_inbound) {
                startActivity(new Intent(deleteproduct.this, inbound.class));
            } else if (itemId == R.id.nav_outbound) {
                startActivity(new Intent(deleteproduct.this, outbound.class));
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(deleteproduct.this, addproduct.class));
            } else if (itemId == R.id.nav_analytics) {
                startActivity(new Intent(deleteproduct.this, analytics.class));
            } else if (itemId == R.id.logout) {
                startActivity(new Intent(deleteproduct.this, logout.class));
            }

            drawerLayout.close();
            return false;
        });

        // Handle the delete product action
        delete.setOnClickListener(view -> {
            String modelNumberText = modelNumber.getText().toString();

            if (modelNumberText.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter the model number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send a request to delete the product by model number
            deleteProductByModelNumber(modelNumberText,userId);
        });
    }

    // Method to delete the product from MySQL database by model number
    private void deleteProductByModelNumber(String modelNumberinput, int user_id) {
        Api api = RetrofitClient.getInstance(getApplicationContext()).getApi();
        Call<DefaultResponse> deleteCall = api.deleteProduct(modelNumberinput,user_id);

        deleteCall.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    modelNumber.setText("");  // Clear the input field
                } else {
                    Toast.makeText(getApplicationContext(), "Error deleting product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to delete product", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
