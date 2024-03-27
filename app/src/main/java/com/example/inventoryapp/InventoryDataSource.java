package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class InventoryDataSource {

    private SQLiteDatabase database;
    private InventorydbHelper dbHelper;

    public InventoryDataSource(Context context) {
        dbHelper = new InventorydbHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addProduct(product product) {
        ContentValues values = new ContentValues();
        values.put(InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME, product.getProductName());
        values.put(InventoryContract.ProductEntry.COLUMN_NAME_PRICE, product.getPrice());
        values.put(InventoryContract.ProductEntry.COLUMN_NAME_MODEL_NUMBER, product.getModelNumber());
        values.put(InventoryContract.ProductEntry.COLUMN_NAME_SI, product.getSi());
        values.put(InventoryContract.ProductEntry.COLUMN_NAME_BRAND, product.getBrand());
        values.put(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY, product.getQuantity());

        long newRowId = database.insert(InventoryContract.ProductEntry.TABLE_NAME, null, values);
    }

    public void addInvoice(Invoice invoice) {
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InvoiceEntry.COLUMN_NAME_USER, invoice.getUser());
        values.put(InventoryContract.InvoiceEntry.COLUMN_NAME_MODEL_NUMBER, invoice.getModelNumber());
        values.put(InventoryContract.InvoiceEntry.COLUMN_NAME_SI, invoice.getSi());
        values.put(InventoryContract.InvoiceEntry.COLUMN_NAME_QUANTITY, invoice.getQuantity());
        values.put(InventoryContract.InvoiceEntry.COLUMN_NAME_DATE, invoice.getDate());
        values.put(InventoryContract.InvoiceEntry.COLUMN_NAME_ACTIVITY_TYPE, invoice.getActivityType());
        long newRowId = database.insert(InventoryContract.InvoiceEntry.TABLE_NAME, null, values);
    }

    public void deleteProduct(String modelNumber) {
        database.delete(InventoryContract.ProductEntry.TABLE_NAME,
                InventoryContract.ProductEntry.COLUMN_NAME_MODEL_NUMBER + " = ?",
                new String[]{modelNumber});
    }

    public List<product> searchProducts(String searchTerm) {
        List<product> products = new ArrayList<>();

        // Define the columns you want to retrieve
        String[] projection = {
                InventoryContract.ProductEntry.COLUMN_NAME_SI,
                InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME,
                InventoryContract.ProductEntry.COLUMN_NAME_PRICE,
                InventoryContract.ProductEntry.COLUMN_NAME_MODEL_NUMBER,
                InventoryContract.ProductEntry.COLUMN_NAME_BRAND,
                InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY
        };

        // Define the selection criteria
        String selection = InventoryContract.ProductEntry.COLUMN_NAME_MODEL_NUMBER + " LIKE ? OR " +
                InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME + " LIKE ?";

        // Define the selection arguments (replace ? placeholders in selection)
        String[] selectionArgs = {"%" + searchTerm + "%", "%" + searchTerm + "%"};

        // Execute the query
        Cursor cursor = database.query(
                InventoryContract.ProductEntry.TABLE_NAME,  // The table to query
                projection,                                 // The columns to return
                selection,                                  // The columns for the WHERE clause
                selectionArgs,                              // The values for the WHERE clause
                null,                                       // Don't group the rows
                null,                                       // Don't filter by row groups
                null                                        // The sort order
        );

        // Iterate over the result set and create Product objects
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int si = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_SI));
                String productName = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_PRICE));
                String modelNumber = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_MODEL_NUMBER));
                String brand = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_BRAND));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY));

                product product = new product(si, price, quantity, modelNumber, brand, productName);
                products.add(product);
            }
            cursor.close();
        }

        return products;
    }
        public boolean checkModelNumberExists(String modelNumber) {
            // Check if the model number exists in the inventory table
            String[] columns = {InventoryContract.ProductEntry.COLUMN_NAME_MODEL_NUMBER};
            String selection = InventoryContract.ProductEntry.COLUMN_NAME_MODEL_NUMBER + " = ?";
            String[] selectionArgs = {modelNumber};

            Cursor cursor = database.query(
                    InventoryContract.ProductEntry.TABLE_NAME,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            boolean exists = (cursor != null && cursor.getCount() > 0);
            if (cursor != null) {
                cursor.close();
            }

            return exists;
        }

        public void updateQuantityByModelNumber(String modelNumber, int quantity) {
            // Update the quantity in the inventory table
            ContentValues values = new ContentValues();
            values.put(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY, quantity);

            String selection = InventoryContract.ProductEntry.COLUMN_NAME_MODEL_NUMBER + " = ?";
            String[] selectionArgs = {modelNumber};

            database.update(
                    InventoryContract.ProductEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
            );
        }
    }



