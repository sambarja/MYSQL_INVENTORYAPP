package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class logout {
    public static void logout(Context context) {

        SharedPreferences preferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to login screen
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
