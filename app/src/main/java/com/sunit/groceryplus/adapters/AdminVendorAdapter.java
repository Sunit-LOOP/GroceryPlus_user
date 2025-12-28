package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Vendor;

import java.util.List;

public class AdminVendorAdapter extends RecyclerView.Adapter<AdminVendorAdapter.VendorViewHolder> {

    private Context context;
    private List<Vendor> vendorList;
    private OnVendorActionListener listener;

    public interface OnVendorActionListener {
        void onEdit(Vendor vendor);
        void onDelete(Vendor vendor);
    }

    public AdminVendorAdapter(Context context, List<Vendor> vendorList, OnVendorActionListener listener) {
        this.context = context;
        this.vendorList = vendorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_vendor_admin, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        Vendor vendor = vendorList.get(position);
        holder.nameTv.setText(vendor.getVendorName());
        holder.addressTv.setText(vendor.getAddress());
        holder.ratingTv.setText(String.format("Rating: %.1f ⭐", vendor.getRating()));

        // Set icon
        int iconId = context.getResources().getIdentifier(vendor.getIcon(), "drawable", context.getPackageName());
        if (iconId != 0) {
            holder.iconIv.setImageResource(iconId);
        } else {
            holder.iconIv.setImageResource(R.drawable.vendor_icon); // Default icon
        }

        holder.editBtn.setOnClickListener(v -> listener.onEdit(vendor));
        holder.deleteBtn.setOnClickListener(v -> listener.onDelete(vendor));
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    public void updateList(List<Vendor> newList) {
        this.vendorList = newList;
        notifyDataSetChanged();
    }

    static class VendorViewHolder extends RecyclerView.ViewHolder {
        ImageView iconIv;
        TextView nameTv, addressTv, ratingTv;
        ImageButton editBtn, deleteBtn;

        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            iconIv = itemView.findViewById(R.id.vendorIconIv);
            nameTv = itemView.findViewById(R.id.vendorNameTv);
            addressTv = itemView.findViewById(R.id.vendorAddressTv);
            ratingTv = itemView.findViewById(R.id.vendorRatingTv);
            editBtn = itemView.findViewById(R.id.editVendorBtn);
            deleteBtn = itemView.findViewById(R.id.deleteVendorBtn);
        }
    }
}
