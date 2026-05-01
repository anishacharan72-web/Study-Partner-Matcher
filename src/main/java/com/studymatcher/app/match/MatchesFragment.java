package com.studymatcher.app.match;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.studymatcher.app.R;
import com.studymatcher.app.api.ApiClient;
import com.studymatcher.app.model.Match;
import com.studymatcher.app.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.studymatcher.app.chat.ChatListAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MatchesFragment — top-10 match cards sorted by score DESC.
 * Gracefully handles offline / backend-not-running by showing empty state silently.
 */
public class MatchesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerLayout;
    private View emptyState;
    private SwipeRefreshLayout swipeRefresh;
    private MatchAdapter matchAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView  = view.findViewById(R.id.rvMatches);
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        emptyState    = view.findViewById(R.id.emptyState);
        swipeRefresh  = view.findViewById(R.id.swipeRefresh);

        matchAdapter = new MatchAdapter(new ArrayList<>(), this::onAccept, this::onDecline);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true); // Optimization: Item sizes are uniform
        recyclerView.setAdapter(matchAdapter);

        swipeRefresh.setColorSchemeResources(R.color.warm_coral);
        swipeRefresh.setOnRefreshListener(this::loadMatches);

        loadMatches();
        listenForNewUsers(); // Start real-time discovery
    }

    private com.google.firebase.database.ChildEventListener newUserListener;
    private com.google.firebase.database.DatabaseReference usersRef;

    private void listenForNewUsers() {
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        newUserListener = new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot snapshot, String previousChildName) {
                // Throttle: only refresh if we aren't already loading
                if (isAdded() && swipeRefresh != null && !swipeRefresh.isRefreshing()) {
                    loadMatches();
                }
            }
            @Override public void onChildChanged(@NonNull com.google.firebase.database.DataSnapshot s, String p) {}
            @Override public void onChildRemoved(@NonNull com.google.firebase.database.DataSnapshot s) {}
            @Override public void onChildMoved(@NonNull com.google.firebase.database.DataSnapshot s, String p) {}
            @Override public void onCancelled(@NonNull com.google.firebase.database.DatabaseError e) {}
        };
        usersRef.addChildEventListener(newUserListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Crucial: Remove listener to prevent lag and memory leaks
        if (usersRef != null && newUserListener != null) {
            usersRef.removeEventListener(newUserListener);
        }
    }

    private void loadMatches() {
        showShimmer(true);
        emptyState.setVisibility(View.GONE);

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        ApiClient.getInstance().getMatches(userId).enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(@NonNull Call<List<Match>> call,
                                   @NonNull Response<List<Match>> response) {
                showShimmer(false);
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Match> matches = response.body();
                    matchAdapter.updateMatches(matches);
                    emptyState.setVisibility(View.GONE);
                } else {
                    // Backend empty or error — show mock partners for testing
                    showMockPartners();
                }
            }
 
            @Override
            public void onFailure(@NonNull Call<List<Match>> call, @NonNull Throwable t) {
                showShimmer(false);
                swipeRefresh.setRefreshing(false);
                // Backend unreachable — show mock partners for testing
                showMockPartners();
            }
        });
    }

    private void showMockPartners() {
        List<Match> mocks = new ArrayList<>();
        
        // Mock 1
        Match m1 = new Match();
        m1.matchId = "mock_1";
        m1.score = 98;
        m1.partner = new com.studymatcher.app.model.User();
        m1.partner.userId = "user_sarah";
        m1.partner.name = "Sarah Miller";
        m1.partner.institution = "Stanford University";
        m1.partner.subjectIds = java.util.Arrays.asList("Java", "Data Structures", "Algorithms");
        mocks.add(m1);

        // Mock 2
        Match m2 = new Match();
        m2.matchId = "mock_2";
        m2.score = 85;
        m2.partner = new com.studymatcher.app.model.User();
        m2.partner.userId = "user_james";
        m2.partner.name = "James Wilson";
        m2.partner.institution = "MIT";
        m2.partner.subjectIds = java.util.Arrays.asList("Python", "Machine Learning", "Calculus");
        mocks.add(m2);

        // Mock 3
        Match m3 = new Match();
        m3.matchId = "mock_3";
        m3.score = 72;
        m3.partner = new com.studymatcher.app.model.User();
        m3.partner.userId = "user_priya";
        m3.partner.name = "Priya Sharma";
        m3.partner.institution = "IIT Delhi";
        m3.partner.subjectIds = java.util.Arrays.asList("Database Systems", "Operating Systems");
        mocks.add(m3);

        matchAdapter.updateMatches(mocks);
        emptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void onAccept(Match match) {
        if (match.partner == null) return;
        
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        String currentUserName = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getDisplayName() : "Me";

        // 1. Notify Backend (Only for REAL users, skip for mocks)
        if (!match.matchId.startsWith("mock_")) {
            ApiClient.getInstance().acceptMatch(match.matchId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {}
                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
            });
        }

        // 2. Remove from current list immediately
        matchAdapter.removeMatch(match);

        // 3. Create Firebase Conversation (Works for both Real and Mock!)
        String conversationId = match.matchId;
        DatabaseReference convRef = FirebaseDatabase.getInstance().getReference("conversations");

        ChatListAdapter.Conversation convForMe = new ChatListAdapter.Conversation();
        convForMe.conversationId = conversationId;
        convForMe.partnerId      = match.partner.userId;
        convForMe.partnerName    = match.partner.name;
        convForMe.partnerPhotoUrl = match.partner.profilePhotoUrl;
        convForMe.lastMessage    = "Match accepted! Say hello.";
        convForMe.timestamp      = System.currentTimeMillis();

        ChatListAdapter.Conversation convForPartner = new ChatListAdapter.Conversation();
        convForPartner.conversationId = conversationId;
        convForPartner.partnerId      = currentUserId;
        convForPartner.partnerName    = currentUserName;
        convForPartner.lastMessage    = "New match! Say hello.";
        convForPartner.timestamp      = System.currentTimeMillis();

        convRef.child(currentUserId).child(conversationId).setValue(convForMe);
        
        // If it's a real user, update their conversation list too
        if (!match.matchId.startsWith("mock_")) {
            convRef.child(match.partner.userId).child(conversationId).setValue(convForPartner);
        }

        // 4. Switch to Chat tab to show success
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).switchTab("chat");
        }
    }

    private void onDecline(Match match) {
        ApiClient.getInstance().declineMatch(match.matchId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) matchAdapter.removeMatch(match);
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) { /* silent */ }
        });
    }

    private void showShimmer(boolean show) {
        if (show) {
            shimmerLayout.setVisibility(View.VISIBLE);
            shimmerLayout.startShimmer();
            swipeRefresh.setVisibility(View.GONE);
        } else {
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
            swipeRefresh.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
