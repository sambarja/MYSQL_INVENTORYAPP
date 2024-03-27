package com.example.inventoryapp;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class addproduct extends AppCompatActivity {

    EditText modelNumber, productName, si, brand, price, quantity;
    Button add;

    CardView subtractbtn, addbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproduct);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InvoiceActivity dataSource = new InvoiceActivity(this);

        modelNumber = findViewById(R.id.editTextModelNumber);
        si = findViewById(R.id.editTextSi);
        productName = findViewById(R.id.editTextProductName);
        quantity = findViewById(R.id.quantityEditText);
        brand = findViewById(R.id.editTextBrand);
        price = findViewById(R.id.editTextPrice);
        add = findViewById(R.id.doneBtn);
        subtractbtn = findViewById(R.id.SubtractBtn);
        addbtn = findViewById(R.id.AddBtn);

        // Set input filter to allow only numbers
        quantity.setFilters(new InputFilter[]{new NumberInputFilter()});

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from the quantity EditText
                String quantityText = quantity.getText().toString();

                // Check if the quantity text is not empty
                if (!quantityText.isEmpty()) {
                    // Increment quantity by 1
                    int initialQuantity = Integer.parseInt(quantityText);
                    int finalQuantityM = initialQuantity + 1;
                    quantity.setText(String.valueOf(finalQuantityM));
                }
            }
        });

        subtractbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from the quantity EditText
                String quantityText = quantity.getText().toString();

                // Check if the quantity text is not empty
                if (!quantityText.isEmpty()) {
                    // Decrement quantity by 1, ensuring it doesn't go below 0
                    int initialQuantity = Integer.parseInt(quantityText);
                    int finalQuantityM = Math.max(initialQuantity - 1, 0);
                    quantity.setText(String.valueOf(finalQuantityM));
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String modelNumberText = modelNumber.getText().toString();
                String quantityText = quantity.getText().toString();
                String nameText = productName.getText().toString();
                String siText = si.getText().toString();
                String priceText = price.getText().toString();
                String brandText = brand.getText().toString();

                // Check if model number, quantity, and SI are not empty
                if (modelNumberText.isEmpty() || quantityText.isEmpty() || siText.isEmpty() || nameText.isEmpty() || priceText.isEmpty() || brandText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the model number exists in the inventory table
                boolean modelNumberExists = dataSource.checkModelNumberExists(modelNumberText);

                if (modelNumberExists) {
                    // Display error message if model number exists
                    Toast.makeText(getApplicationContext(), "Product Already Exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Add data to the inventory table
                    int quantityValue = Integer.parseInt(quantityText);
                    int siValue = Integer.parseInt(siText);
                    int priceValue = Integer.parseInt(priceText);

                    product newProduct = new product(siValue, priceValue, quantityValue, modelNumberText, brandText, nameText);

                    dataSource.addProduct(newProduct);

                    Toast.makeText(getApplicationContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class NumberInputFilter implements InputFilter {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // Allow only digits
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    }
}
