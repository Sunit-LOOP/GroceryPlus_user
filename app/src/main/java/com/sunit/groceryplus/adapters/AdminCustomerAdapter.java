package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sunit.groceryplus.models.User;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;

import java.util.List;

public class AdminCustomerAdapter extends RecyclerView.Adapter<AdminCustomerAdapter.CustomerViewHolder> {

    private Context context;
    private List<User> users;

    public AdminCustomerAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_admin_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public void updateUsers(List<User> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameTv;
        TextView customerEmailTv;
        TextView customerPhoneTv;
        TextView customerTypeTv;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTv = itemView.findViewById(R.id.customerNameTv);
            customerEmailTv = itemView.findViewById(R.id.customerEmailTv);
            customerPhoneTv = itemView.findViewById(R.id.customerPhoneTv);
            customerTypeTv = itemView.findViewById(R.id.customerTypeTv);
        }

        public void bind(User user) {
            customerNameTv.setText(user.getName());
            customerEmailTv.setText(user.getEmail());
            customerPhoneTv.setText(user.getPhone());
            customerTypeTv.setText(user.getUserType());
        }
    }
}
