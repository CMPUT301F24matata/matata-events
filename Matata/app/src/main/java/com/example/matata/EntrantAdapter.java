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

public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.EntrantViewHolder> {
    private List<Entrant> usertList;
    private Context context;

    public EntrantAdapter(Context context, List<Entrant> usertList) {
        this.context = context;
        this.usertList = usertList;
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.entrant_card, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        Entrant entrant = usertList.get(position);
        String name = entrant.getName();
        holder.userNameText.setText(name);
    }

    @Override
    public int getItemCount() { return usertList.size(); }

    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage, statusIndicator;
        TextView userNameText;

        EntrantViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userNameText = itemView.findViewById(R.id.user_name);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
        }
    }
}

