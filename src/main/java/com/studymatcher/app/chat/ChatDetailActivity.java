package com.studymatcher.app.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.studymatcher.app.R;
import com.studymatcher.app.model.Message;

import java.util.ArrayList;

/**
 * ChatDetailActivity — real-time chat with double-tick delivery indicators.
 * Sent: Warm Coral bubble, right-aligned.
 * Received: Card Warm bubble, left-aligned.
 * Max message: 1,000 chars.
 */
public class ChatDetailActivity extends AppCompatActivity {

    private static final int MAX_MESSAGE_LENGTH = 1000;

    private RecyclerView recyclerView;
    private EditText etMessage;
    private TextView tvCharCounter;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;

    private DatabaseReference chatRef;
    private String currentUserId;
    private String conversationId;
    private String partnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        conversationId  = getIntent().getStringExtra("conversationId");
        partnerId       = getIntent().getStringExtra("partnerId");
        String partnerName = getIntent().getStringExtra("partnerName");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(partnerName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView  = findViewById(R.id.rvMessages);
        etMessage     = findViewById(R.id.etMessage);
        tvCharCounter = findViewById(R.id.tvCharCounter);
        btnSend       = findViewById(R.id.btnSend);

        chatAdapter = new ChatAdapter(new ArrayList<>(), currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Character counter
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.length();
                // Show counter only when near limit (≥900 chars)
                if (len >= 900) {
                    tvCharCounter.setVisibility(View.VISIBLE);
                    tvCharCounter.setText(getString(R.string.chars_remaining, len));
                } else {
                    tvCharCounter.setVisibility(View.GONE);
                }
                btnSend.setEnabled(len > 0 && len <= MAX_MESSAGE_LENGTH);
            }
        });

        btnSend.setOnClickListener(v -> sendMessage());

        // Delete conversation option
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete_chat) {
                confirmDeleteConversation();
                return true;
            }
            return false;
        });

        // Firebase reference — path: chats/{conversationId}/messages
        chatRef = FirebaseDatabase.getInstance()
                .getReference("chats")
                .child(conversationId)
                .child("messages");

        listenForMessages();
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty() || text.length() > MAX_MESSAGE_LENGTH) return;

        Message message = new Message(currentUserId, partnerId, text);
        String key = chatRef.push().getKey();
        if (key != null) {
            chatRef.child(key).setValue(message);
            // Update conversation list metadata
            updateConversationMeta(text);
        }
        etMessage.setText("");
    }

    private void listenForMessages() {
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Message msg = snapshot.getValue(Message.class);
                if (msg != null) {
                    chatAdapter.addMessage(msg);
                    recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    // Mark as delivered/read if we are the receiver
                    if (partnerId.equals(msg.senderId)) {
                        snapshot.getRef().child("status").setValue(Message.Status.READ.name());
                    }
                }
            }
            @Override public void onChildChanged(@NonNull DataSnapshot s, String p) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot s) {}
            @Override public void onChildMoved(@NonNull DataSnapshot s, String p) {}
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void updateConversationMeta(String lastMessage) {
        DatabaseReference convRef = FirebaseDatabase.getInstance()
                .getReference("conversations");
        long timestamp = System.currentTimeMillis();

        convRef.child(currentUserId).child(conversationId).child("lastMessage").setValue(lastMessage);
        convRef.child(currentUserId).child(conversationId).child("timestamp").setValue(timestamp);
        convRef.child(partnerId).child(conversationId).child("lastMessage").setValue(lastMessage);
        convRef.child(partnerId).child(conversationId).child("timestamp").setValue(timestamp);
        // Increment unread count for partner
        convRef.child(partnerId).child(conversationId).child("unreadCount")
                .get().addOnSuccessListener(snap -> {
                    long count = snap.exists() ? ((Long) snap.getValue() + 1) : 1;
                    convRef.child(partnerId).child(conversationId).child("unreadCount").setValue(count);
                });
    }

    private void confirmDeleteConversation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_conversation)
                .setMessage(R.string.delete_confirm)
                .setPositiveButton(R.string.delete, (d, w) -> deleteConversation())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteConversation() {
        FirebaseDatabase.getInstance()
                .getReference("conversations")
                .child(currentUserId)
                .child(conversationId)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Conversation deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
