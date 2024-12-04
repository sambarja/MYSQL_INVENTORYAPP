package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class analyticsAdapter extends RecyclerView.Adapter<analyticsAdapter.ViewHolder> {

    private List<String> monthNames;
    private Context context;

    public analyticsAdapter(List<String> monthNames, Context context) {
        this.monthNames = monthNames;
        this.context = context;
    }

    public void updateData(List<String> monthNames) {
        this.monthNames = monthNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView modelName;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            modelName = itemView.findViewById(R.id.monthName);
            card = itemView.findViewById(R.id.month_card);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.modelName.setText(monthNames.get(position));

        // Handle card click to pass selected month to analyticsView
        holder.card.setOnClickListener(view -> {
            // Set the selected month in session data
            SessionData.getInstance().selectedMonth = monthNames.get(position);

            // Start analyticsView activity with the selected month
            Intent intent = new Intent(context, analyticsView.class);
            intent.putExtra("selectedMonth", monthNames.get(position));  // Pass selected month to the next activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return monthNames.size();
    }
}
