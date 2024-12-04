package com.example.inventoryapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class home extends AppCompatActivity {

    CardView choice1, choice2, choice3, choice4, choice5, choice6;
    ImageView menu;
    NavigationView navigationView;
    TextView usernameText, nameText;
    DrawerLayout drawerLayout;

    int userId;
    String username, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_home);
        drawerLayout = findViewById(R.id.drawer_home);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        usernameText = headerView.findViewById(R.id.username);
        nameText = headerView.findViewById(R.id.name);

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        username = sharedPreferences.getString("username", "Guest");
        name = sharedPreferences.getString("name", "Unknown User");

        // Update navigation header with user info
        TextView usernameText = headerView.findViewById(R.id.username);
        TextView nameText = headerView.findViewById(R.id.name);

        usernameText.setText(username);
        nameText.setText(name);



        // Fetch user data from API
        fetchUserData();



        // Handle menu choices
        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);
        choice4 = findViewById(R.id.choice4);
        choice5 = findViewById(R.id.choice5);
        choice6 = findViewById(R.id.choice6);
        menu = findViewById(R.id.menuImage);

        menu.setOnClickListener(view -> drawerLayout.open());


        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.nav_home) {
                // Handle home action
            } else if (itemId == R.id.nav_inventory) {
                startActivity(new Intent(home.this, inventory.class));
            } else if (itemId == R.id.nav_inbound) {
                startActivity(new Intent(home.this, inbound.class));
            } else if (itemId == R.id.nav_outbound) {
                startActivity(new Intent(home.this, outbound.class));
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(home.this, addproduct.class));
            } else if (itemId == R.id.nav_delete) {
                startActivity(new Intent(home.this, deleteproduct.class));
            } else if (itemId == R.id.nav_analytics) {
                startActivity(new Intent(home.this, analytics.class));
            } else if (itemId == R.id.logout) {
                logout.logout(home.this);
            }

            drawerLayout.close();
            return true;
        });

        // Menu choice click listeners
        choice1.setOnClickListener(view -> startActivity(new Intent(home.this, inventory.class)));
        choice2.setOnClickListener(view -> startActivity(new Intent(home.this, inbound.class)));
        choice3.setOnClickListener(view -> startActivity(new Intent(home.this, outbound.class)));
        choice4.setOnClickListener(view -> startActivity(new Intent(home.this, addproduct.class)));
        choice5.setOnClickListener(view -> startActivity(new Intent(home.this, deleteproduct.class)));
        choice6.setOnClickListener(view -> startActivity(new Intent(home.this, analytics.class)));
    }



    private void fetchUserData() {


        Api apiService = RetrofitClient.getInstance(getApplicationContext()).getApi();
        Call<UserResponse> call = apiService.getUserByUsername(username); // Pass the username here
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse.User user = response.body().getUser();
                    // Update UI with user data
                    usernameText.setText(username);
                    nameText.setText(name);
                    Log.d("HomeActivity", "Retrieved username: " + user.getUsername());
                    Log.d("HomeActivity", "Retrieved name: " + user.getName());

                } else {
                    Toast.makeText(home.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("HomeActivity", "Error fetching user data: " + t.getMessage());
                Toast.makeText(home.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Schedule task (unchanged)
    private void scheduleTask() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), InventoryBroadcast.class);
        intent.setAction("com.example.inventoryapp");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Set the alarm to start at midnight
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Schedule the task to repeat every day
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(this, "Daily Monitoring initiated", Toast.LENGTH_SHORT).show();
    }
}
