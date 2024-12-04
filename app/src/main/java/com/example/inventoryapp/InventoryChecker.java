package com.example.inventoryapp;

import android.content.Context;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryChecker {
    private static final int LOW_INVENTORY_THRESHOLD = 5; // Define your threshold here

    // Fetch products for the user from the database and check inventory
    public static void checkInventoryAndNotify(int userId, Context context) {
        // Create Retrofit instance and call the API to fetch products by user ID
        Api api = RetrofitClient.getInstance(context.getApplicationContext()).getApi();
        Call<ProductResponse> call = api.getUserProducts(userId);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse.Product> productList = response.body().getProducts();

                    // Check each product's inventory
                    for (ProductResponse.Product product : productList) {
                        if (product.getQuantity() < LOW_INVENTORY_THRESHOLD) {
                            String message = "The product " + product.getProductName() + " has low quantity. Please restock soon.";
                            NotificationHelper.showLowInventoryNotification(context, message);
                        }
                    }
                } else {
                    Log.e("InventoryChecker", "Failed to fetch products. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e("InventoryChecker", "Error fetching products: ", t);
            }
        });
    }
}
