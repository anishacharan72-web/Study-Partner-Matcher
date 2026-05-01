package com.studymatcher.app.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.studymatcher.app.ui.MainActivity;

/**
 * SplashActivity — shown on launch with Inter logo + blush background.
 * Routes: unauthenticated → Onboarding; authenticated → MainActivity.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Install splash screen before super.onCreate
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // Keep splash visible until routing decision is made
        splashScreen.setKeepOnScreenCondition(() -> false);

        routeUser();
    }

    private void routeUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null && currentUser.isEmailVerified()) {
            // Already authenticated — go to main app
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Not authenticated — show onboarding
            startActivity(new Intent(this, OnboardingActivity.class));
        }
        finish();
    }
}
