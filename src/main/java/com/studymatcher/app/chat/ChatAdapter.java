package com.studymatcher.app.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.studymatcher.app.R;
import com.studymatcher.app.model.Message;

import java.util.List;

/**
 * Adapter for chat messages — sent (right, Warm Coral) and received (left, Card Warm) bubbles.
 * Shows double-tick delivery status indicator.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_SENT     = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private final List<Message> messages;
    private final String currentUserId;

    public ChatAdapter(List<Message> messages, String currentUserId) {
        this.messages      = messages;
        this.currentUserId = currentUserId;
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return currentUserId.equals(messages.get(position).senderId)
                ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == VIEW_TYPE_SENT
                ? R.layout.item_message_sent
                : R.layout.item_message_received;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvText, tvStatus;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText   = itemView.findViewById(R.id.tvMessageText);
            tvStatus = itemView.findViewById(R.id.tvDeliveryStatus);
        }

        void bind(Message msg) {
            tvText.setText(msg.text);
            if (tvStatus != null) {
                // Delivery ticks: ✓ sent, ✓✓ delivered, ✓✓ (blue) read
                switch (msg.status != null ? msg.status : "SENT") {
                    case "READ":      tvStatus.setText("✓✓"); tvStatus.setAlpha(1f); break;
                    case "DELIVERED": tvStatus.setText("✓✓"); tvStatus.setAlpha(0.6f); break;
                    default:          tvStatus.setText("✓");  tvStatus.setAlpha(0.6f); break;
                }
            }
        }
    }
}
