package com.example.inventoryapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class InventoryBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        InventoryDataSource dataSource = new InventoryDataSource(context);
        List<product> productList = dataSource.fetchDataFromDatabase();
        InventoryChecker.checkInventoryAndNotify(productList, context);
    }
}
