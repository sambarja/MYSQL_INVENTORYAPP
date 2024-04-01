
package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText edUsername, edPassword;
    TextView register;
    Button btn;
    SharedPreferences sharedPreferences;
    InventorydbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        });

        dbHelper = new InventorydbHelper(this);

        edUsername = findViewById(R.id.editTextText);
        edPassword = findViewById(R.id.editTextTextPassword);
        btn = findViewById(R.id.loginButton);
        register = findViewById(R.id.registerText);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean("loggedIn", false)) {
            // If logged in, directly open home activity
            String user_cred = sharedPreferences.getString("user", "");
            String[] creds = user_cred.split("_");
            if (authenticateUser(creds[0], creds[1])) {
                startActivity(new Intent(MainActivity.this, home.class));
                finish(); // Finish this activity to prevent going back to login screen
            }

        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, register.class));
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edUsername.getText().toString();
                String password = edPassword.getText().toString();

                if (username.length() == 0 || password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please fill All details", Toast.LENGTH_SHORT).show();
                } else {
                    // Authenticate user
                    if (authenticateUser(username, password)) {
                        // Mark user as logged in
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("loggedIn", true);
                        editor.putString("user", username + "_" + password);
                        editor.apply();

                        Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, home.class));
                        finish(); // Finish this activity to prevent going back to login screen
                    } else {
                        Toast.makeText(getApplicationContext(), "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
                        edUsername.setText("");
                        edPassword.setText("");
                    }
                }
            }
        });
    }

    private boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {InventoryContract.UserEntry._ID, InventoryContract.UserEntry.COLUMN_NAME_NAME};
        String selection = InventoryContract.UserEntry.COLUMN_NAME_USERNAME + " = ? AND " +
                InventoryContract.UserEntry.COLUMN_NAME_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(
                InventoryContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndexOrThrow(InventoryContract.UserEntry.COLUMN_NAME_NAME);
            String retrievedName = cursor.getString(index);
            user = new User(username, retrievedName);
        }
        SessionData.getInstance().user = user;
        boolean authenticated = cursor.getCount() > 0;
        cursor.close();
        return authenticated;
    }
}