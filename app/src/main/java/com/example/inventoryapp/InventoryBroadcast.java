package com.example.inventoryapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryBroadcast extends BroadcastReceiver {

    int userId;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Use context to get SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            Log.e("InventoryBroadcast", "User ID not found in SharedPreferences");
            return; // Exit if userId is not found
        }

        // Call the API to get the user's products from MySQL database
        Api api = RetrofitClient.getInstance(context.getApplicationContext()).getApi();
        Call<ProductResponse> call = api.getUserProducts(userId);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get the list of products
                    List<ProductResponse.Product> productList = response.body().getProducts();

                    // Check inventory and notify
                    InventoryChecker.checkInventoryAndNotify(userId, context);
                } else {
                    Log.e("InventoryBroadcast", "Failed to fetch products. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e("InventoryBroadcast", "Error fetching products: ", t);
            }
        });
    }
}
