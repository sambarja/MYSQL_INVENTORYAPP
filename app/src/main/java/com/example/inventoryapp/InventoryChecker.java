package com.example.inventoryapp;

import android.content.Context;

//import org.apache.commons.math3.stat.descriptive.summary.Product;

import java.util.List;

public class InventoryChecker {
    private static final int LOW_INVENTORY_THRESHOLD = 10; // Define your threshold here

    public static void checkInventoryAndNotify(List<product> productList, Context context) {
        for (product product : productList) {
            if (product.getQuantity() < LOW_INVENTORY_THRESHOLD) {
                String message = "The product " + product.getProductName() + " has low quantity. Please restock soon.";
                NotificationHelper.showLowInventoryNotification(context, message);
            }
        }
    }
}
