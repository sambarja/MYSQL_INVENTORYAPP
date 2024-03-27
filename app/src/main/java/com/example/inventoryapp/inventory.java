package com.example.inventoryapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.List;
import android.view.View;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;




public class inventory extends AppCompatActivity implements productAdapter.OnEditClickListener {

    RecyclerView recyclerView;
    private productAdapter adapter;
    private InventoryDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dataSource = new InventoryDataSource(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateRecyclerView();
    }

    private void updateRecyclerView() {
        List<product> productList = dataSource.fetchDataFromDatabase();

        if (adapter == null) {
            adapter = new productAdapter(productList);
            adapter.setOnEditClickListener(this); // Set edit click listener
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(productList);
        }
    }

    @Override
    public void onEditClick(int position) {
        // Handle edit action here
        product productToEdit = adapter.productList.get(position);
        showEditPopup(productToEdit);
    }


    private void showEditPopup(product productToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.edit, null);

        EditText editName = view.findViewById(R.id.productNameEditText);
        EditText editModelNumber = view.findViewById(R.id.modelNumberEditText);
        EditText editPrice = view.findViewById(R.id.priceEditText);

        // Set current values
        editName.setText(productToEdit.getProductName());
        editModelNumber.setText(productToEdit.getModelNumber());
        editPrice.setText(String.valueOf(productToEdit.getPrice()));

        builder.setView(view)
                .setTitle("Edit Product")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = editName.getText().toString();
                    String newModelNumber = editModelNumber.getText().toString();
                    int newPrice = Integer.parseInt(editPrice.getText().toString());

                    // Update product details in database
                    productToEdit.setProductName(newName);
                    productToEdit.setModelNumber(newModelNumber);
                    productToEdit.setPrice(newPrice);
                    dataSource.updateProduct(productToEdit);

                    // Update RecyclerView
                    updateRecyclerView();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}


