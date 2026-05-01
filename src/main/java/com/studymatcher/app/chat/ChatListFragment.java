package com.studymatcher.app.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studymatcher.app.R;
import com.studymatcher.app.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatListFragment — shows list of active conversations (accepted matches only).
 * Real-time updates from Firebase Realtime Database.
 */
public class ChatListFragment extends Fragment {

    private RecyclerView recyclerView;
    private View emptyState;
    private ChatListAdapter adapter;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView  = view.findViewById(R.id.rvChats);
        emptyState    = view.findViewById(R.id.emptyState);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        adapter = new ChatListAdapter(new ArrayList<>(), conversation -> {
            Intent intent = new Intent(getContext(), ChatDetailActivity.class);
            intent.putExtra("conversationId", conversation.conversationId);
            intent.putExtra("partnerId", conversation.partnerId);
            intent.putExtra("partnerName", conversation.partnerName);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadConversations();
    }

    private void loadConversations() {
        FirebaseDatabase.getInstance()
                .getReference("conversations")
                .child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ChatListAdapter.Conversation> conversations = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            ChatListAdapter.Conversation conv =
                                    snap.getValue(ChatListAdapter.Conversation.class);
                            if (conv != null) conversations.add(conv);
                        }
                        adapter.updateConversations(conversations);
                        emptyState.setVisibility(conversations.isEmpty() ? View.VISIBLE : View.GONE);
                        recyclerView.setVisibility(conversations.isEmpty() ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { /* handle */ }
                });
    }
}
