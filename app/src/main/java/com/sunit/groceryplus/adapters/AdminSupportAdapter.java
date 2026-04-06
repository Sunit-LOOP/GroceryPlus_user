package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.SupportTicket;

import java.util.List;

/** AdminSupportAdapter - Specialized adapter for administrative ticket management. */
public class AdminSupportAdapter extends RecyclerView.Adapter<AdminSupportAdapter.ViewHolder> {

    private Context context;
    private List<SupportTicket> tickets;
    private OnTicketActionListener listener;

    public interface OnTicketActionListener {
        void onResolveTicket(SupportTicket ticket);
        void onApproveWalletRefund(SupportTicket ticket, double amount);
    }

    public AdminSupportAdapter(Context context, List<SupportTicket> tickets, OnTicketActionListener listener) {
        this.context = context;
        this.tickets = tickets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_admin_support_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SupportTicket ticket = tickets.get(position);
        
        holder.subjectTv.setText(ticket.getSubject());
        holder.statusTv.setText(ticket.getStatus().toUpperCase());
        holder.descTv.setText(ticket.getDescription());
        holder.userInfoTv.setText("User ID: " + ticket.getUserId() + " | Order #" + ticket.getOrderId());
        holder.dateTypeTv.setText(ticket.getCreatedAt() + " | Type: " + ticket.getIssueType());

        // Handle Image
        if (ticket.getIssueImage() != null && !ticket.getIssueImage().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(ticket.getIssueImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.issueIv.setImageBitmap(decodedByte);
                holder.issueIv.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                holder.issueIv.setVisibility(View.GONE);
            }
        } else {
            holder.issueIv.setVisibility(View.GONE);
        }

        // Action Logic
        if ("resolved".equalsIgnoreCase(ticket.getStatus()) || ticket.getStatus().contains("refunded")) {
            holder.actionsLayout.setVisibility(View.GONE);
        } else {
            holder.actionsLayout.setVisibility(View.VISIBLE);
        }

        holder.resolveBtn.setOnClickListener(v -> listener.onResolveTicket(ticket));
        
        holder.refundBtn.setOnClickListener(v -> {
            // In a real app, we'd fetch the item price. 
            // For this demo, let's assume a standard refund amount or provide a dialog to enter it.
            listener.onApproveWalletRefund(ticket, 100.0); // Demo amount
        });
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTv, statusTv, descTv, userInfoTv, dateTypeTv;
        ImageView issueIv;
        MaterialButton resolveBtn, refundBtn;
        View actionsLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTv = itemView.findViewById(R.id.ticketSubjectTv);
            statusTv = itemView.findViewById(R.id.ticketStatusTv);
            descTv = itemView.findViewById(R.id.ticketDescTv);
            userInfoTv = itemView.findViewById(R.id.userInfoTv);
            dateTypeTv = itemView.findViewById(R.id.dateTypeTv);
            issueIv = itemView.findViewById(R.id.issueIv);
            resolveBtn = itemView.findViewById(R.id.resolveBtn);
            refundBtn = itemView.findViewById(R.id.refundBtn);
            actionsLayout = itemView.findViewById(R.id.actionsLayout);
        }
    }
}
