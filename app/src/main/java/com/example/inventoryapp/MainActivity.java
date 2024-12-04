package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText edUsername, edPassword;
    TextView register;
    Button btn;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edUsername = findViewById(R.id.editTextText);
        edPassword = findViewById(R.id.editTextTextPassword);
        btn = findViewById(R.id.loginButton);
        register = findViewById(R.id.registerText);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean("loggedIn", false)) {
            startActivity(new Intent(MainActivity.this, home.class));
            finish(); // Finish this activity to prevent going back to login screen
        }

        register.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, register.class)));

        btn.setOnClickListener(view -> {
            String username = edUsername.getText().toString();
            String password = edPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill all details", Toast.LENGTH_SHORT).show();
            } else {
                authenticateUser(username, password);
            }
        });
    }

    private void authenticateUser(String username, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/myApi/public/") // Emulator URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        Call<UserResponse> call = api.userLogin(username, password);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    if (!userResponse.isError()) {
                        UserResponse.User user = userResponse.getUser();

                        // Save user data in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("loggedIn", true);
                        editor.putInt("userId", user.getId());
                        editor.putString("username", user.getUsername());
                        editor.putString("name", user.getName());
                        editor.apply(); // Apply changes

                        Log.d("MainActivity", "User logged in: " + user.getUsername());

                        Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, home.class);
                        startActivity(intent);
                        finish(); // Finish this activity to prevent going back to the login screen
                    } else {
                        Toast.makeText(getApplicationContext(), "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
