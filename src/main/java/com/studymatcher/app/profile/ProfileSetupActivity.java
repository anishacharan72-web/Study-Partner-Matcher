package com.studymatcher.app.profile;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.studymatcher.app.R;

/** Multi-step profile setup — 5 steps after registration. */
public class ProfileSetupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);
    }
}
