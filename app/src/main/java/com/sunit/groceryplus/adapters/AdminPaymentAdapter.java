package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Payment;

import java.util.List;
import java.util.Locale;

public class AdminPaymentAdapter extends RecyclerView.Adapter<AdminPaymentAdapter.ViewHolder> {

    private Context context;
    private List<Payment> payments;

    public AdminPaymentAdapter(Context context, List<Payment> payments) {
        this.context = context;
        this.payments = payments;
    }

    public void updatePayments(List<Payment> newPayments) {
        this.payments = newPayments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_admin_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payment payment = payments.get(position);
        holder.orderIdTv.setText("Order #" + payment.getOrderId());
        holder.amountTv.setText(String.format(Locale.US, "Rs. %.2f", payment.getAmount()));
        
        // Format date for better display
        String formattedDate = formatDateForDisplay(payment.getPaymentDate());
        holder.dateTv.setText(formattedDate);
        
        // Set payment method and icon
        String method = payment.getPaymentMethod();
        holder.methodTv.setText(getPaymentMethodDisplayName(method));
        
        // Set appropriate icon
        if ("stripe".equalsIgnoreCase(method)) {
            holder.methodIcon.setImageResource(R.drawable.stripe_icon);
        } else if ("cod".equalsIgnoreCase(method)) {
            holder.methodIcon.setImageResource(R.drawable.cash_on_delivery_icon);
        } else {
            holder.methodIcon.setImageResource(R.drawable.ic_credit_card);
        }
        
        // Set transaction ID
        holder.txnIdTv.setText(payment.getTransactionId());
        
        // Set status chip
        holder.statusChip.setText("Completed");
    }
    
    private String formatDateForDisplay(String dateStr) {
        // Simplified date formatting - in a real app you'd parse the date properly
        // Assuming dateStr is in format "YYYY-MM-DD HH:MM:SS"
        if (dateStr != null && dateStr.length() >= 16) {
            String datePart = dateStr.substring(0, 10); // YYYY-MM-DD
            String timePart = dateStr.substring(11, 16); // HH:MM
            
            // Convert to more readable format
            String[] dateComponents = datePart.split("-");
            if (dateComponents.length == 3) {
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                 "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                int monthIndex = Integer.parseInt(dateComponents[1]) - 1;
                if (monthIndex >= 0 && monthIndex < 12) {
                    return months[monthIndex] + " " + dateComponents[2] + ", " + dateComponents[0] + 
                           " • " + timePart;
                }
            }
            return datePart + " • " + timePart;
        }
        return dateStr;
    }
    
    private String getPaymentMethodDisplayName(String method) {
        if ("stripe".equalsIgnoreCase(method)) {
            return "Stripe Payment";
        } else if ("cod".equalsIgnoreCase(method)) {
            return "Cash on Delivery";
        } else {
            return method;
        }
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTv, amountTv, methodTv, dateTv, txnIdTv;
        ImageView methodIcon;
        Chip statusChip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTv = itemView.findViewById(R.id.paymentOrderIdTv);
            amountTv = itemView.findViewById(R.id.paymentAmountTv);
            methodTv = itemView.findViewById(R.id.paymentMethodTv);
            dateTv = itemView.findViewById(R.id.paymentDateTv);
            txnIdTv = itemView.findViewById(R.id.paymentTransactionIdTv);
            methodIcon = itemView.findViewById(R.id.paymentMethodIcon);
            statusChip = itemView.findViewById(R.id.statusChip);
        }
    }
}
