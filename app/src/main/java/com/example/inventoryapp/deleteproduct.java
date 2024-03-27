package com.example.inventoryapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class deleteproduct extends AppCompatActivity {

    EditText modelNumber;
    Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleteproduct);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InvoiceActivity dataSource = new InvoiceActivity(this);

        modelNumber = findViewById(R.id.editTextModelNumber);
        delete = findViewById(R.id.doneBtn);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String modelNumberText = modelNumber.getText().toString();

                if (modelNumberText.isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the model number exists in the inventory table
                boolean modelNumberExists = dataSource.checkModelNumberExists(modelNumberText);

                if(modelNumberExists){
                    dataSource.removeProduct(modelNumberText);
                    Toast.makeText(getApplicationContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    modelNumber.setText("");
                }else {
                    Toast.makeText(getApplicationContext(), "Product does not exist", Toast.LENGTH_SHORT).show();
                    modelNumber.setText("");
                }
            }

            });

    }
}