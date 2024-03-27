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


public class inventory extends AppCompatActivity {

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

    // Method to update RecyclerView with the latest data from the database
    private void updateRecyclerView() {
        List<product> productList = dataSource.fetchDataFromDatabase();

        if (adapter == null) {
            // If adapter is null, create a new one and set it to the RecyclerView
            adapter = new productAdapter(productList);
            recyclerView.setAdapter(adapter);
        } else {
            // If adapter already exists, update its dataset
            adapter.updateData(productList);
        }
    }



}

