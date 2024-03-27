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
    private OnEditClickListener onEditClickListener;

    public productAdapter(List<product> productList) {
        this.productList = productList;
    }

    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView modelName, modelNumber, quantity;
        ImageView editProduct;

        public ViewHolder(@NonNull View itemView, final OnEditClickListener listener) {
            super(itemView);
            modelName = itemView.findViewById(R.id.modelName);
            modelNumber = itemView.findViewById(R.id.modelNumber);
            quantity = itemView.findViewById(R.id.quantity);
            editProduct = itemView.findViewById(R.id.editProduct);

            editProduct.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(view, onEditClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        product product = productList.get(position);
        holder.modelName.setText(product.getProductName());
        holder.modelNumber.setText(product.getModelNumber());
        holder.quantity.setText(String.valueOf(product.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(List<product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }
}

