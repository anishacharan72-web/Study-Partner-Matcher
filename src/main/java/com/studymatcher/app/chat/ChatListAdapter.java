package com.studymatcher.app.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.studymatcher.app.R;

import java.util.List;

/**
 * Adapter for the chat list — shows conversations with last message preview,
 * timestamp, and unread badge.
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    public static class Conversation {
        public String conversationId;
        public String partnerId;
        public String partnerName;
        public String partnerPhotoUrl;
        public String lastMessage;
        public long   timestamp;
        public int    unreadCount;

        public Conversation() {}
    }

    public interface OnConversationClick {
        void onClick(Conversation conversation);
    }

    private List<Conversation> conversations;
    private final OnConversationClick listener;

    public ChatListAdapter(List<Conversation> conversations, OnConversationClick listener) {
        this.conversations = conversations;
        this.listener = listener;
    }

    public void updateConversations(List<Conversation> list) {
        this.conversations = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_row, parent, false);
        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(conversations.get(position));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastMsg, tvTimestamp, tvUnread;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName      = itemView.findViewById(R.id.tvPartnerName);
            tvLastMsg   = itemView.findViewById(R.id.tvLastMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvUnread    = itemView.findViewById(R.id.tvUnreadBadge);
        }

        void bind(Conversation conv) {
            tvName.setText(conv.partnerName);
            tvLastMsg.setText(conv.lastMessage);
            if (conv.unreadCount > 0) {
                tvUnread.setVisibility(View.VISIBLE);
                tvUnread.setText(String.valueOf(conv.unreadCount));
            } else {
                tvUnread.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(v -> listener.onClick(conv));
        }
    }
}
