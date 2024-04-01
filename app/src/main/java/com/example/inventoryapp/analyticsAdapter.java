package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class analyticsAdapter extends RecyclerView.Adapter<analyticsAdapter.ViewHolder> {

    public List<String> monthNames;
    private Context context;
    private OnEditClickListener onEditClickListener;

    public analyticsAdapter(List<String> monthNames, Context context) {
        this.monthNames = monthNames;
        this.context = context;
    }

    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView modelName;
        CardView card;

        public ViewHolder(@NonNull View itemView, final OnEditClickListener listener) {
            super(itemView);
            modelName = itemView.findViewById(R.id.monthName);
            card = itemView.findViewById(R.id.month_card);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_layout, parent, false);
        return new ViewHolder(view, onEditClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.modelName.setText(monthNames.get(position));
        holder.card.setOnClickListener(view -> {
                    SessionData.getInstance().selectedMonth = monthNames.get(position);
                    context.startActivity(new Intent(context,analyticsView.class));
                }
        );
    }

    @Override
    public int getItemCount() {
        return monthNames.size();
    }

}

