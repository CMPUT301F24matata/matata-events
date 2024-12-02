package com.example.matata;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * The {@code EntrantAdapter} class is a custom RecyclerView adapter designed to bind a list of {@link Entrant} objects
 * to a RecyclerView for display. Each item in the RecyclerView represents a single entrant, showing their name and status.
 * The adapter provides functionality for viewing entrant details in a dialog and dynamically updating UI elements
 * based on entrant status.
 *
 * <h2>Key Features:</h2>
 * <ul>
 *     <li>Inflates the layout defined in {@code entrant_card.xml} for each entrant item.</li>
 *     <li>Binds entrant data such as name and status to the corresponding views.</li>
 *     <li>Displays entrant details in a dialog when an item is clicked.</li>
 *     <li>Dynamically updates the status indicator icon based on entrant status.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>
 * // Initialize the adapter
 * List&lt;Entrant&gt; entrantList = ...;
 * EntrantAdapter adapter = new EntrantAdapter(context, entrantList, "Rejected");
 *
 * // Set the adapter to the RecyclerView
 * RecyclerView recyclerView = findViewById(R.id.recycler_view);
 * recyclerView.setAdapter(adapter);
 * </pre>
 *
 * <h2>Limitations:</h2>
 * <ul>
 *     <li>Requires the entrant list and context to be passed at initialization.</li>
 *     <li>Assumes the presence of {@code entrant_card.xml} layout with specific view IDs.</li>
 *     <li>No built-in mechanism for real-time updates; the dataset must be updated externally.</li>
 * </ul>
 *
 * <h2>Thread Safety:</h2>
 * This class is not thread-safe. Any modifications to the entrant list should be synchronized
 * if accessed from multiple threads.
 */
public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.EntrantViewHolder> {

    /**
     * List of {@link Entrant} objects to display in the RecyclerView.
     */
    private final List<Entrant> userList;

    /**
     * Context of the application or activity where the RecyclerView is used.
     */
    private final Context context;

    /**
     * Status of entrants, used to determine the visual representation of status indicators.
     * Possible values include "Rejected", "Pending", "Cancelled", "Waitlist", etc.
     */
    private final String status;

    /**
     * Constructs an {@code EntrantAdapter} with the specified context, entrant list, and status.
     *
     * @param context  the context in which the RecyclerView is being used
     * @param userList the list of {@link Entrant} objects to display
     * @param status   the status of entrants, used for setting the status indicator icon
     */
    public EntrantAdapter(Context context, List<Entrant> userList, String status) {
        this.context = context;
        this.userList = userList;
        this.status = status;
    }

    /**
     * Inflates the layout for an individual entrant item and creates a {@link EntrantViewHolder}.
     *
     * @param parent   the parent ViewGroup into which the new view will be added
     * @param viewType the view type of the new View
     * @return a new {@link EntrantViewHolder} holding an inflated layout
     */
    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.entrant_card, parent, false);
        return new EntrantViewHolder(view);
    }

    /**
     * Binds data from an {@link Entrant} object to the views in the {@link EntrantViewHolder}.
     * Sets the entrant's name and updates the status indicator based on the entrant's status.
     *
     * @param holder   the view holder containing views for the entrant item
     * @param position the position of the entrant item within the list
     */
    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        Entrant entrant = userList.get(position);
        String name = entrant.getName();
        holder.userNameText.setText(name);
        if (status.equals("Rejected")){
            holder.statusIndicator.setImageResource(R.drawable.ic_entrant_status_red);
        }else if (status.equals("Pending") || status.equals("Cancelled")){
            holder.statusIndicator.setImageResource(R.drawable.ic_entrant_status_yellow);
        }else if (status.equals("Waitlist")){
            holder.statusIndicator.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(view -> entrantDetailDialog(entrant));
    }

    /**
     * Displays an alert dialog with detailed information about the selected entrant.
     *
     * @param entrant the {@link Entrant} object whose details are displayed
     */
    private void entrantDetailDialog(Entrant entrant) {
        String name = entrant.getName();
        String email = entrant.getEmail();
        String phone = entrant.getPhoneNumber();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Name: " + name +
                        "\nEmail: " + email +
                        "\nPhone Number: " + phone)
                .setNegativeButton("OK", null);
        builder.create().show();
    }

    /**
     * Returns the total number of items in the entrant list.
     *
     * @return the number of {@link Entrant} items to display in the RecyclerView
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * ViewHolder class for individual entrant items in the RecyclerView. Holds references
     * to views within the {@code entrant_card.xml} layout for displaying entrant details.
     */
    public static class EntrantViewHolder extends RecyclerView.ViewHolder {

        /**
         * ImageView for displaying the entrant's profile picture or avatar.
         */
        ImageView userImage;

        /**
         * ImageView for displaying the status indicator icon.
         */
        ImageView statusIndicator;

        /**
         * TextView for displaying the entrant's name.
         */
        TextView userNameText;

        /**
         * Constructs a new {@code EntrantViewHolder} and binds its views to the layout elements.
         *
         * @param itemView the item view representing an entrant in the RecyclerView
         */
        EntrantViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userNameText = itemView.findViewById(R.id.user_name);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
        }
    }
}
