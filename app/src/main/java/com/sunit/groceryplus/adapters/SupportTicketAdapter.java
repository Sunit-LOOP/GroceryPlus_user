package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.SupportTicket;

import java.util.List;

/** SupportTicketAdapter - Displays support tickets with their status and basic info. */
public class SupportTicketAdapter extends RecyclerView.Adapter<SupportTicketAdapter.ViewHolder> {

    private Context context;
    private List<SupportTicket> tickets;

    public SupportTicketAdapter(Context context, List<SupportTicket> tickets) {
        this.context = context;
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_support_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SupportTicket ticket = tickets.get(position);
        holder.subjectTv.setText(ticket.getSubject());
        holder.statusTv.setText(ticket.getStatus().toUpperCase());
        holder.descTv.setText(ticket.getDescription());
        holder.idTv.setText("Ticket #" + ticket.getTicketId());
        holder.dateTv.setText(ticket.getCreatedAt());

        // Color status
        if ("open".equalsIgnoreCase(ticket.getStatus())) {
            holder.statusTv.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
        } else if ("resolved".equalsIgnoreCase(ticket.getStatus())) {
            holder.statusTv.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.statusTv.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTv, statusTv, descTv, idTv, dateTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTv = itemView.findViewById(R.id.ticketSubjectTv);
            statusTv = itemView.findViewById(R.id.ticketStatusTv);
            descTv = itemView.findViewById(R.id.ticketDescTv);
            idTv = itemView.findViewById(R.id.ticketIdTv);
            dateTv = itemView.findViewById(R.id.ticketDateTv);
        }
    }
}
