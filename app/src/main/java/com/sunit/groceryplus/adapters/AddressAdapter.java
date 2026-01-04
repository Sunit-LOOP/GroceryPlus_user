package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Address;

import java.util.List;

/**
 * Adapter for displaying a list of User Addresses.
 * Handles binding address data to the RecyclerView, managing edit/delete actions,
 * and setting a default address.
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context context;
    private List<Address> addressList;
    private OnAddressActionListener listener;

    /**
     * Interface for handling address-related user actions.
     */
    public interface OnAddressActionListener {
        void onEdit(Address address);
        void onDelete(Address address);
        void onSetDefault(Address address);
    }

    /**
     * Constructor for AddressAdapter.
     * @param context Application context
     * @param addressList List of Address objects
     * @param listener Callback listener for actions
     */
    public AddressAdapter(Context context, List<Address> addressList, OnAddressActionListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
    }

    /**
     * Updates the data source and refreshes the UI.
     * @param newAddresses New list of addresses
     */
    public void updateAddresses(List<Address> newAddresses) {
        this.addressList = newAddresses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address address = addressList.get(position);
        
        // Bind data to views
        holder.addressTypeTv.setText(address.getType());
        holder.fullAddressTv.setText(address.getFullAddress() + ", " + address.getLandmark());
        holder.cityTv.setText(address.getCity());
        
        // Show "Default" badge if applicable
        holder.defaultBadge.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);

        // Set listeners for action buttons
        holder.editAddressBtn.setOnClickListener(v -> listener.onEdit(address));
        holder.deleteAddressBtn.setOnClickListener(v -> listener.onDelete(address));
        
        // Clicking the item usually sets it as default (if not already)
        holder.itemView.setOnClickListener(v -> {
            if (!address.isDefault()) {
                listener.onSetDefault(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    /**
     * ViewHolder class to cache view lookups for performance.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView addressTypeTv, fullAddressTv, cityTv, defaultBadge;
        View editAddressBtn, deleteAddressBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addressTypeTv = itemView.findViewById(R.id.addressTypeTv);
            fullAddressTv = itemView.findViewById(R.id.fullAddressTv);
            cityTv = itemView.findViewById(R.id.cityTv);
            defaultBadge = itemView.findViewById(R.id.defaultBadge);
            editAddressBtn = itemView.findViewById(R.id.editAddressBtn);
            deleteAddressBtn = itemView.findViewById(R.id.deleteAddressBtn);
        }
    }
}
