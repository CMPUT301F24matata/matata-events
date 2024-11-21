package com.example.matata;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter class for displaying a list of cancelled entrants in a RecyclerView.
 */
public class CancelledEntrantAdapter extends RecyclerView.Adapter<CancelledEntrantAdapter.ViewHolder> {

    /**
     * List of cancelled entrants to be displayed.
     */
    private List<CancelledEntrant> cancelledEntrants;

    /**
     * Constructs a new {@code CancelledEntrantAdapter} with the given list of cancelled entrants.
     *
     * @param cancelledEntrants The list of cancelled entrants.
     */
    public CancelledEntrantAdapter(List<CancelledEntrant> cancelledEntrants) {
        this.cancelledEntrants = cancelledEntrants;
    }

    /**
     * Inflates the layout for an individual item in the RecyclerView.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new {@code ViewHolder} that holds the inflated layout.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cancelled_entrant, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data from a {@code CancelledEntrant} object to the views in the ViewHolder.
     *
     * @param holder   The ViewHolder to update with data.
     * @param position The position of the item in the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CancelledEntrant entrant = cancelledEntrants.get(position);
        holder.nameTextView.setText(entrant.getName());
        holder.reasonTextView.setText(entrant.getReason());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The size of the cancelled entrants list.
     */
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
