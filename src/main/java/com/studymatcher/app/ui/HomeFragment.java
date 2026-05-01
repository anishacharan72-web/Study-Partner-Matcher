package com.studymatcher.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.studymatcher.app.R;
import com.studymatcher.app.match.MatchesFragment;

import java.util.Calendar;

/**
 * HomeFragment — dashboard with greeting, stats, quick actions, sessions.
 */
public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Greeting
        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        tvGreeting.setText(getTimeGreeting());

        if (user != null) {
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                tvUserName.setText(displayName);
            } else {
                // Use email prefix as name fallback
                String email = user.getEmail();
                if (email != null) {
                    String name = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
                    tvUserName.setText(capitalize(name));
                } else {
                    tvUserName.setText("Student");
                }
            }
        } else {
            tvUserName.setText("Student");
        }

        // Stats (placeholder until backend runs)
        TextView tvMatchCount  = view.findViewById(R.id.tvMatchCount);
        TextView tvSessionCount = view.findViewById(R.id.tvSessionCount);
        TextView tvRating       = view.findViewById(R.id.tvRating);
        tvMatchCount.setText("0");
        tvSessionCount.setText("0");
        tvRating.setText("—");

        // Quick action cards
        view.findViewById(R.id.cardFindMatches).setOnClickListener(v -> navigateTo("matches"));
        view.findViewById(R.id.cardSchedule).setOnClickListener(v ->
                android.widget.Toast.makeText(getContext(), "Availability setup coming soon!", android.widget.Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.cardProfile).setOnClickListener(v -> navigateTo("profile"));

        // Find partners button inside empty state
        view.findViewById(R.id.btnFindPartners).setOnClickListener(v -> navigateTo("matches"));
    }

    /** Routes bottom nav to the requested tab. */
    private void navigateTo(String tab) {
        if (getActivity() instanceof MainActivity) {
            MainActivity main = (MainActivity) getActivity();
            main.switchTab(tab);
        }
    }

    private String getTimeGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) return "Good morning 👋";
        if (hour < 17) return "Good afternoon 👋";
        return "Good evening 👋";
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
