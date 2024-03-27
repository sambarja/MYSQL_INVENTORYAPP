package com.example.inventoryapp;
import android.content.Context;


public class InvoiceActivity {

    private InventoryDataSource dataSource; // Assuming you have a class to handle database operations

    public InvoiceActivity(Context context) {
        dataSource = new InventoryDataSource(context);
    }

    public void addDataToInvoice(String modelNumber, String quantity) {
        // Add data to the invoice table
        Invoice invoice = new Invoice();
        invoice.setModelNumber(modelNumber);
        invoice.setQuantity(Integer.parseInt(quantity));

        dataSource.open();
        dataSource.addInvoice(invoice);
        dataSource.close();
    }

    public void updateInventoryQuantity(String modelNumber, int quantity) {
        // Update the quantity in the inventory table
        dataSource.open();
        dataSource.updateQuantityByModelNumber(modelNumber, quantity);
        dataSource.close();
    }

    public boolean checkModelNumberExists(String modelNumber) {
        // Check if the model number exists in the inventory table
        dataSource.open();
        boolean exists = dataSource.checkModelNumberExists(modelNumber);
        dataSource.close();
        return exists;
    }
}
