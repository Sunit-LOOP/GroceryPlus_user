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

public class DeliveryPersonnelAdapter extends RecyclerView.Adapter<DeliveryPersonnelAdapter.ViewHolder> {

    private Context context;
    private List<DeliveryPerson> personnel;
    private OnStatusToggleListener listener;

    public interface OnStatusToggleListener {
        void onToggleStatus(DeliveryPerson person);
    }

    public DeliveryPersonnelAdapter(Context context, List<DeliveryPerson> personnel, OnStatusToggleListener listener) {
        this.context = context;
        this.personnel = personnel;
        this.listener = listener;
    }

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
        
        if ("Available".equalsIgnoreCase(person.getStatus())) {
            holder.statusTv.setText("Available");
            holder.statusTv.setTextColor(Color.parseColor("#4CAF50")); // Green
            holder.statusTv.setBackgroundColor(Color.parseColor("#E8F5E9"));
        } else {
            holder.statusTv.setText("Busy");
            holder.statusTv.setTextColor(Color.parseColor("#F44336")); // Red
            holder.statusTv.setBackgroundColor(Color.parseColor("#FFEBEE"));
        }

        holder.toggleBtn.setOnClickListener(v -> listener.onToggleStatus(person));
    }

    @Override
    public int getItemCount() {
        return personnel.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv, phoneTv, statusTv;
        Button toggleBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.personNameTv);
            phoneTv = itemView.findViewById(R.id.personPhoneTv);
            statusTv = itemView.findViewById(R.id.personStatusTv);
            toggleBtn = itemView.findViewById(R.id.toggleStatusBtn);
        }
    }
}
