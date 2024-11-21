package com.example.matata;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CancelledEntrantAdapter extends RecyclerView.Adapter<CancelledEntrantAdapter.ViewHolder> {

    private List<CancelledEntrant> cancelledEntrants;

    public CancelledEntrantAdapter(List<CancelledEntrant> cancelledEntrants) {
        this.cancelledEntrants = cancelledEntrants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cancelled_entrant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CancelledEntrant entrant = cancelledEntrants.get(position);
        holder.nameTextView.setText(entrant.getName());
        holder.reasonTextView.setText(entrant.getReason());
    }

    @Override
    public int getItemCount() {
        return cancelledEntrants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, reasonTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.cancelled_entrant_name);
            reasonTextView = itemView.findViewById(R.id.cancelled_entrant_reason);
        }
    }
}

