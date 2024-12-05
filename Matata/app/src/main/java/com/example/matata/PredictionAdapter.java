package com.example.matata;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import java.util.ArrayList;
import java.util.List;
public class PredictionAdapter extends RecyclerView.Adapter<PredictionAdapter.ViewHolder> {
    private List<AutocompletePrediction> predictions = new ArrayList<>();
    private final OnPredictionClickListener listener;
    public interface OnPredictionClickListener {
        void onPredictionClick(AutocompletePrediction prediction);
    }
    public PredictionAdapter(OnPredictionClickListener listener) {
        this.listener = listener;
    }
    public void updateData(List<AutocompletePrediction> newPredictions) {
        predictions = newPredictions;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AutocompletePrediction prediction = predictions.get(position);
        holder.textView.setText(prediction.getFullText(null).toString());
        holder.itemView.setOnClickListener(v -> listener.onPredictionClick(prediction));
    }
    @Override
    public int getItemCount() {
        return predictions.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}