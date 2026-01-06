package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.DeliveryPerson;

import java.util.List;

/** DeliveryPersonnelAdapter - Manages list of delivery drivers and their availability status in the Admin Panel. */
public class DeliveryPersonnelAdapter extends RecyclerView.Adapter<DeliveryPersonnelAdapter.ViewHolder> {

    private Context context;
    private List<DeliveryPerson> personnel;
    private OnStatusToggleListener listener;

    /** Interface for toggling driver availability (Ready for duty vs Offline). */
    public interface OnStatusToggleListener {
        void onToggleStatus(DeliveryPerson person);
    }

    /** Constructor. */
    public DeliveryPersonnelAdapter(Context context, List<DeliveryPerson> personnel, OnStatusToggleListener listener) {
        this.context = context;
        this.personnel = personnel;
        this.listener = listener;
    }

    /** Updates the data source and refreshes the UI. */
    public void updateList(List<DeliveryPerson> newList) {
        this.personnel = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_delivery_personnel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryPerson person = personnel.get(position);
        holder.nameTv.setText(person.getName());
        holder.phoneTv.setText(person.getPhone());

        // Show availability instead of status text
        boolean available = person.isAvailable();
        holder.statusTv.setText(available ? "Available" : "Unavailable");
        holder.statusTv.setTextColor(available ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));

        holder.toggleBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleStatus(person);
            }
        });
    }

    @Override
    public int getItemCount() {
        return personnel != null ? personnel.size() : 0;
    }

    /** ViewHolder for mapping delivery personnel components. */
    static class ViewHolder extends RecyclerView.ViewHolder {
        // UI Components
        TextView nameTv, phoneTv, statusTv;
        Button toggleBtn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.personNameTv);
            phoneTv = itemView.findViewById(R.id.personPhoneTv);
            statusTv = itemView.findViewById(R.id.personStatusTv);
            toggleBtn = itemView.findViewById(R.id.toggleStatusBtn);
        }
    }
}
