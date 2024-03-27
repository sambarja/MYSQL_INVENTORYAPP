package com.example.inventoryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class productAdapter extends RecyclerView.Adapter<productAdapter.ViewHolder> {

    public List<product> productList;

    public productAdapter(List<product> productList) {
        this.productList = productList;
    }

    // Method to update the dataset
    public void updateData(List<product> productList) {
        this.productList = productList;
        notifyDataSetChanged(); // Notify adapter that the dataset has changed
    }

    // Method to add a single product to the dataset
    public void addProduct(product product) {
        productList.add(product);
        notifyItemInserted(productList.size() - 1); // Notify adapter that item is inserted
    }

    // Method to remove a single product from the dataset
    public void removeProduct(int position) {
        productList.remove(position);
        notifyItemRemoved(position); // Notify adapter that item is removed
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        product product = productList.get(position);
        holder.modelName.setText(product.getProductName());
        holder.modelNumber.setText(product.getModelNumber());
        holder.quantity.setText(String.valueOf(product.getQuantity()));
        // Implement edit button click listener if needed
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView modelName, modelNumber, quantity;
        ImageView editProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            modelName = itemView.findViewById(R.id.modelName);
            modelNumber = itemView.findViewById(R.id.modelNumber);
            quantity = itemView.findViewById(R.id.quantity);
            editProduct = itemView.findViewById(R.id.editProduct);
        }
    }
}
