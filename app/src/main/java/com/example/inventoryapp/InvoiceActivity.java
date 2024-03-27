package com.example.inventoryapp;
import android.content.Context;


public class InvoiceActivity {

    private InventoryDataSource dataSource; // Assuming you have a class to handle database operations

    public InvoiceActivity(Context context) {
        dataSource = new InventoryDataSource(context);
    }

    public void addDataToInvoice(String modelNumber, String quantity, String transactionType, String user, String date, int si) {
        // Add data to the invoice table
        Invoice invoice = new Invoice();
        invoice.setModelNumber(modelNumber);
        invoice.setQuantity(Integer.parseInt(quantity));
        invoice.setActivityType(transactionType);
        invoice.setUser(user);
        invoice.setDate(date);
        invoice.setSi(si);

        dataSource.open();
        dataSource.addInvoice(invoice);
        dataSource.close();
    }


    public void updateInventoryQuantity(String modelNumber, int quantity, String activity) {
        // Open the data source
        dataSource.open();

        // Get the current quantity from the inventory
        int currentQuantity = dataSource.getQuantityByModelNumber(modelNumber);

        // Calculate the new quantity based on the activity
        int newQuantity;
        if (activity.equals("inbound")) {
            // For inbound, add the given quantity to the current quantity
            newQuantity = currentQuantity + quantity;
        } else if (activity.equals("outbound")) {
            // For outbound, subtract the given quantity from the current quantity
            newQuantity = Math.max(currentQuantity - quantity, 0); // Ensure quantity doesn't go below 0
        } else {
            // Invalid activity type
            dataSource.close();
            return;
        }

        // Update the quantity in the inventory table
        dataSource.updateQuantityByModelNumber(modelNumber, newQuantity);

        // Close the data source
        dataSource.close();
    }


    public boolean checkModelNumberExists(String modelNumber) {
        // Check if the model number exists in the inventory table
        dataSource.open();
        boolean exists = dataSource.checkModelNumberExists(modelNumber);
        dataSource.close();
        return exists;
    }

    public void addProduct(product newProduct) {
        dataSource.open();
        dataSource.addProduct(newProduct);
        dataSource.close();
    }

    public void removeProduct(String modelNumberText) {
        dataSource.open();
        dataSource.deleteProduct(modelNumberText);
        dataSource.close();
    }
}
