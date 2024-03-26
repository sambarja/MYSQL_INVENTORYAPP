package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText edUsername, edPassword;
    Button btn;

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

        edUsername = findViewById(R.id.editTextText);
        edPassword = findViewById(R.id.editTextTextPassword);
        btn = findViewById(R.id.loginButton);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String username = edUsername.getText().toString();
                String password = edPassword.getText().toString();
                String IDun = "admin123";
                String IDpass = "123";
                if(username.length()==0 || password.length()==0){
                    Toast.makeText(getApplicationContext(),"Please fill All details", Toast.LENGTH_SHORT).show();
                    edUsername.setText("");
                    edPassword.setText("");

                } else if (username.equals(IDun) && password.equals(IDpass)) {
                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, home.class));
                } else {
                    Toast.makeText(getApplicationContext(),"Incorrect Details", Toast.LENGTH_SHORT).show();
                    edUsername.setText("");
                    edPassword.setText("");
                }
            }
        });
    }
}
