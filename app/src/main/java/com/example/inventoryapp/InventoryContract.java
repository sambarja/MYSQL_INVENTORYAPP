package com.example.inventoryapp;
import android.provider.BaseColumns;

public final class InventoryContract {
    private InventoryContract() {}

    /* Inner class that defines the table contents */
    public static class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_NAME_PRODUCT_NAME = "product_name";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_MODEL_NUMBER = "model_number";
        public static final String COLUMN_NAME_SI = "si";
        public static final String COLUMN_NAME_BRAND = "brand";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
    }

    public static class InvoiceEntry implements BaseColumns {
        public static final String TABLE_NAME = "invoices";
        public static final String COLUMN_NAME_USER = "user";
        public static final String COLUMN_NAME_MODEL_NUMBER = "model_number";
        public static final String COLUMN_NAME_SI = "si";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_ACTIVITY_TYPE = "activity_type";
    }

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }
}
