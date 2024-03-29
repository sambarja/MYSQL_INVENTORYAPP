package com.example.inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventorydbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Inventory.db";

    private static final String SQL_CREATE_PRODUCTS_TABLE =
            "CREATE TABLE " + InventoryContract.ProductEntry.TABLE_NAME + " (" +
                    InventoryContract.ProductEntry.COLUMN_NAME_SI + " INTEGER PRIMARY KEY," +
                    InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME + " TEXT," +
                    InventoryContract.ProductEntry.COLUMN_NAME_PRICE + " INTEGER," +
                    InventoryContract.ProductEntry.COLUMN_NAME_MODEL_NUMBER + " TEXT," +
                    InventoryContract.ProductEntry.COLUMN_NAME_BRAND + " TEXT," +
                    InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY + " INTEGER)";

    private static final String SQL_CREATE_INVOICES_TABLE =
            "CREATE TABLE " + InventoryContract.InvoiceEntry.TABLE_NAME + " (" +
                    InventoryContract.InvoiceEntry._ID + " INTEGER PRIMARY KEY," +
                    InventoryContract.InvoiceEntry.COLUMN_NAME_USER + " TEXT," +
                    InventoryContract.InvoiceEntry.COLUMN_NAME_MODEL_NUMBER + " TEXT," +
                    InventoryContract.InvoiceEntry.COLUMN_NAME_SI + " INTEGER," +
                    InventoryContract.InvoiceEntry.COLUMN_NAME_QUANTITY + " INTEGER," +
                    InventoryContract.InvoiceEntry.COLUMN_NAME_DATE + " TEXT," +
                    InventoryContract.InvoiceEntry.COLUMN_NAME_ACTIVITY_TYPE + " TEXT)";

    private static final String SQL_CREATE_USERS_TABLE =
            "CREATE TABLE " + InventoryContract.UserEntry.TABLE_NAME + " (" +
                    InventoryContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                    InventoryContract.UserEntry.COLUMN_NAME_USERNAME + " TEXT," +
                    InventoryContract.UserEntry.COLUMN_NAME_NAME + " TEXT," +
                    InventoryContract.UserEntry.COLUMN_NAME_PASSWORD + " TEXT)";

    private static final String SQL_DELETE_PRODUCTS_TABLE =
            "DROP TABLE IF EXISTS " + InventoryContract.ProductEntry.TABLE_NAME;

    private static final String SQL_DELETE_INVOICES_TABLE =
            "DROP TABLE IF EXISTS " + InventoryContract.InvoiceEntry.TABLE_NAME;

    private static final String SQL_DELETE_USERS_TABLE =
            "DROP TABLE IF EXISTS " + InventoryContract.UserEntry.TABLE_NAME;

    public InventorydbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        db.execSQL(SQL_CREATE_INVOICES_TABLE);
        db.execSQL(SQL_CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PRODUCTS_TABLE);
        db.execSQL(SQL_DELETE_INVOICES_TABLE);
        db.execSQL(SQL_DELETE_USERS_TABLE);
        onCreate(db);
    }
}
