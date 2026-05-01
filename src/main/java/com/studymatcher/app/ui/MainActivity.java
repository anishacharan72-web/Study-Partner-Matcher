package com.studymatcher.app.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.studymatcher.app.R;
import com.studymatcher.app.auth.LoginActivity;
import com.studymatcher.app.chat.ChatListFragment;
import com.studymatcher.app.match.MatchesFragment;
import com.studymatcher.app.profile.ProfileFragment;

/**
 * MainActivity — host activity for bottom nav fragments:
 * Home | Matches | Chat | Profile
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        // Handle deep link tab from notification
        String tab = getIntent().getStringExtra("tab");

        // Default to home / navigate to requested tab
        if ("matches".equals(tab)) {
            loadFragment(new MatchesFragment());
            bottomNav.setSelectedItemId(R.id.nav_matches);
        } else if ("chat".equals(tab)) {
            loadFragment(new ChatListFragment());
            bottomNav.setSelectedItemId(R.id.nav_chat);
        } else if ("profile".equals(tab)) {
            loadFragment(new ProfileFragment());
            bottomNav.setSelectedItemId(R.id.nav_profile);
        } else {
            loadFragment(new HomeFragment());
            bottomNav.setSelectedItemId(R.id.nav_home);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (id == R.id.nav_matches) {
                fragment = new MatchesFragment();
            } else if (id == R.id.nav_chat) {
                fragment = new ChatListFragment();
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            } else {
                return false;
            }
            loadFragment(fragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    /** Called by child fragments to switch the active bottom nav tab. */
    public void switchTab(String tab) {
        switch (tab) {
            case "matches":
                bottomNav.setSelectedItemId(R.id.nav_matches);
                break;
            case "chat":
                bottomNav.setSelectedItemId(R.id.nav_chat);
                break;
            case "profile":
                bottomNav.setSelectedItemId(R.id.nav_profile);
                break;
            default:
                bottomNav.setSelectedItemId(R.id.nav_home);
                break;
        }
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
