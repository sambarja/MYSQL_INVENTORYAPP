package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class register extends AppCompatActivity {

    EditText edUsername, edName, edPassword, edConPass;
    Button register;
    InventorydbHelper dbHelper;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new InventorydbHelper(this);

        edUsername = findViewById(R.id.userid);
        edPassword = findViewById(R.id.password);
        edName = findViewById(R.id.name);
        edConPass = findViewById(R.id.confirmpass);
        register = findViewById(R.id.registerbtn);
        back = findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(register.this, MainActivity.class));
            }
        });



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edUsername.getText().toString();
                String name = edName.getText().toString();
                String password = edPassword.getText().toString();
                String conPass = edConPass.getText().toString();

                if (username.length() == 0 || password.length() == 0 || name.length() == 0 || conPass.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please fill All details", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(conPass)) {
                    Toast.makeText(getApplicationContext(), "Password confirmation is incorrect", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if username already exists
                    if (isUsernameExists(username)) {
                        Toast.makeText(getApplicationContext(), "Username already exists, please choose another one", Toast.LENGTH_SHORT).show();
                    } else {
                        // Create a User object
                        User user = new User(username, name, password);
                        // Insert new user into database
                        long newRowId = insertUser(user);
                        if (newRowId != -1) {
                            Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(register.this, MainActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }

    private long insertUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryContract.UserEntry.COLUMN_NAME_USERNAME, user.getUsername());
        values.put(InventoryContract.UserEntry.COLUMN_NAME_NAME, user.getName()); // Add name to ContentValues
        values.put(InventoryContract.UserEntry.COLUMN_NAME_PASSWORD, user.getPassword());
        return db.insert(InventoryContract.UserEntry.TABLE_NAME, null, values);
    }

    private boolean isUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {InventoryContract.UserEntry._ID};
        String selection = InventoryContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(
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
}