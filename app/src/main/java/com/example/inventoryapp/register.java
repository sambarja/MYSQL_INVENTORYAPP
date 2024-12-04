package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class register extends AppCompatActivity {

    EditText edUsername, edName, edPassword, edConPass;
    Button register;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edUsername = findViewById(R.id.userid);
        edPassword = findViewById(R.id.password);
        edName = findViewById(R.id.name);
        edConPass = findViewById(R.id.confirmpass);
        register = findViewById(R.id.registerbtn);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> startActivity(new Intent(register.this, MainActivity.class)));

        register.setOnClickListener(view -> {
            String username = edUsername.getText().toString();
            String name = edName.getText().toString();
            String password = edPassword.getText().toString();
            String conPass = edConPass.getText().toString();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || conPass.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill all details", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(conPass)) {
                Toast.makeText(getApplicationContext(), "Password confirmation is incorrect", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                // Check if username already exists via the API
                checkUsernameExists(username, name, password);
            }
        });
    }

    // Method to check if the username exists
    private void checkUsernameExists(String username, String name, String password) {
        Api apiService = RetrofitClient.getInstance(getApplicationContext()).getApi();
        Call<UserResponse> call = apiService.getUserByUsername(username);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    Log.d("Register", "Response Body: " + new Gson().toJson(userResponse));


                    if (userResponse.getUser() != null) {
                        Log.d("Register", "User exists: " + userResponse.getUser().getUsername());
                        Toast.makeText(getApplicationContext(), "Username already exists, please choose another one", Toast.LENGTH_SHORT).show();
                        clearFields();
                    } else {
                        Log.d("Register", "User does not exist, proceed with creation");
                        createUser(username, password, name);
                    }
                } else {
                    Log.e("Register", "Error: " + response.code() + " - " + response.message());
                    Toast.makeText(getApplicationContext(), "Failed to check username", Toast.LENGTH_SHORT).show();
                }
            }




            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error checking username: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to create the user
    private void createUser(String username, String password, String name ){
        Api apiService = RetrofitClient.getInstance(getApplicationContext()).getApi();
        Call<DefaultResponse> call = apiService.createUser(username,password,name);

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful()) {
                    DefaultResponse responseBody = response.body();
                    Log.d("Register", "Response: " + responseBody);

                        // Log the actual reason for failure
                        Log.e("Register", "Error: " + responseBody.getMessage());
                        Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();

                } else {
                    Log.e("Register", "Error: " + response.code() + " - " + response.message());
                    Toast.makeText(getApplicationContext(), "Failed to create user", Toast.LENGTH_SHORT).show();
                }
                clearFields();
            }


            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error during user creation: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Clear the input fields
    private void clearFields() {
        edUsername.setText("");
        edName.setText("");
        edPassword.setText("");
        edConPass.setText("");
    }
}
