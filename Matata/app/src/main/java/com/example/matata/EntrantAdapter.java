package com.example.matata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * EntrantAdapter is a RecyclerView adapter that binds a list of Entrant objects to views displayed in a RecyclerView.
 * Each Entrant is displayed in an item layout defined by entrant_card.xml. The adapter handles inflating the layout,
 * creating view holders, and binding data from Entrant objects to the appropriate views.
 *
 * Outstanding issues: The adapter currently only displays the name of the entrant in the item view. Additional data
 * like the status indicator or user image may need to be set based on specific conditions or entrant properties.
 */
public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.EntrantViewHolder> {

    /**
     * List of Entrant objects representing users in the event or application.
     */
    private List<Entrant> userList;

    /**
     * Context of the current state of the application, used to access resources and other application-specific data.
     */
    private Context context;


    /**
     * Constructs an EntrantAdapter with a specified context and list of Entrant objects.
     *
     * @param context the context in which the RecyclerView is being used
     * @param userList the list of Entrant objects to display in the RecyclerView
     */
    public EntrantAdapter(Context context, List<Entrant> userList) {
        this.context = context;
        this.userList = userList;
    }

    /**
     * Creates a new EntrantViewHolder by inflating the entrant_card layout.
     *
     * @param parent the parent ViewGroup into which the new view will be added
     * @param viewType the view type of the new View
     * @return a new EntrantViewHolder holding an inflated entrant_card layout
     */
    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.entrant_card, parent, false);
        return new EntrantViewHolder(view);
    }

    /**
     * Binds data from an Entrant object to the views in the EntrantViewHolder.
     *
     * @param holder the view holder containing views for the Entrant item
     * @param position the position of the Entrant item within the list
     */
    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        Entrant entrant = userList.get(position);
        String name = entrant.getName();
        holder.userNameText.setText(name);
    }

    /**
     * Returns the total number of items in the user list.
     *
     * @return the number of Entrant items to display
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * EntrantViewHolder is a view holder for Entrant items in the RecyclerView. It holds references to
     * the ImageView and TextView in the entrant_card layout to display entrant information.
     */
    public static class EntrantViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        ImageView statusIndicator;
        TextView userNameText;

        /**
         * Constructs an EntrantViewHolder with the specified itemView, binding its views to the layout elements.
         *
         * @param itemView the item view representing an entrant item in the RecyclerView
         */
        EntrantViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userNameText = itemView.findViewById(R.id.user_name);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
        }
    }
}
