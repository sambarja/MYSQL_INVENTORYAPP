package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;



public class userDataSource {
    private SQLiteDatabase database;
    private InventorydbHelper dbHelper;
    public userDataSource(Context context) {
        dbHelper = new InventorydbHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(InventoryContract.UserEntry.COLUMN_NAME_USERNAME, user.getUsername());
        values.put(InventoryContract.UserEntry.COLUMN_NAME_NAME, user.getName());
        values.put(InventoryContract.UserEntry.COLUMN_NAME_PASSWORD, user.getPassword());

        long newRowId = database.insert(InventoryContract.UserEntry.TABLE_NAME,null,values);
    }

    public boolean isUsernameExists(String username) {

        String[] projection = {InventoryContract.UserEntry._ID};
        String selection = InventoryContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = database.query(
                InventoryContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public User getUserDetails() {
        String[] projection = {
                InventoryContract.UserEntry.COLUMN_NAME_USERNAME,
                InventoryContract.UserEntry.COLUMN_NAME_NAME,
                InventoryContract.UserEntry.COLUMN_NAME_PASSWORD
        };
        Cursor cursor = database.query(
                InventoryContract.UserEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.UserEntry.COLUMN_NAME_USERNAME));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.UserEntry.COLUMN_NAME_NAME));
            String password= cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.UserEntry.COLUMN_NAME_PASSWORD));
            user = new User(username, name, password); //
            cursor.close();
        }
        return user;
    }

}
