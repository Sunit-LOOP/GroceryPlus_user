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

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context context;
    private List<Address> addressList;
    private OnAddressActionListener listener;

    public interface OnAddressActionListener {
        void onEdit(Address address);
        void onDelete(Address address);
        void onSetDefault(Address address);
    }

    public AddressAdapter(Context context, List<Address> addressList, OnAddressActionListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
    }

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
        holder.addressTypeTv.setText(address.getType());
        holder.fullAddressTv.setText(address.getFullAddress() + ", " + address.getLandmark());
        holder.cityTv.setText(address.getCity());
        
        holder.defaultBadge.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);

        holder.editAddressBtn.setOnClickListener(v -> listener.onEdit(address));
        holder.deleteAddressBtn.setOnClickListener(v -> listener.onDelete(address));
        
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
