package com.studymatcher.app.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studymatcher.app.R;

import java.util.HashMap;
import java.util.Map;

/**
 * ProfileEditActivity — full form to edit name, institution, bio,
 * academic level, study goal, and mode preference.
 * Data is persisted to Firebase Realtime Database + Auth displayName.
 */
public class ProfileEditActivity extends AppCompatActivity {

    private TextInputEditText etName, etInstitution, etBio;
    private ChipGroup chipGroupLevel, chipGroupGoal, chipGroupMode;
    private LinearProgressIndicator progressBar;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Toolbar back button
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Find views
        etName        = findViewById(R.id.etName);
        etInstitution = findViewById(R.id.etInstitution);
        etBio         = findViewById(R.id.etBio);
        chipGroupLevel = findViewById(R.id.chipGroupLevel);
        chipGroupGoal  = findViewById(R.id.chipGroupGoal);
        chipGroupMode  = findViewById(R.id.chipGroupMode);
        progressBar   = findViewById(R.id.progressBar);

        // Style selected chips with coral tint
        styleChipGroup(chipGroupLevel);
        styleChipGroup(chipGroupGoal);
        styleChipGroup(chipGroupMode);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { finish(); return; }

        userRef = FirebaseDatabase.getInstance()
                .getReference("users").child(user.getUid());

        // Pre-fill from Firebase Auth
        if (user.getDisplayName() != null) etName.setText(user.getDisplayName());
        if (user.getEmail() != null) etInstitution.setHint("Institution / University");

        // Pre-fill from RTDB
        loadCurrentProfile();

        // Save button
        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> saveProfile());
    }

    private void loadCurrentProfile() {
        progressBar.setVisibility(View.VISIBLE);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);

                String institution = snapshot.child("institution").getValue(String.class);
                String bio         = snapshot.child("bio").getValue(String.class);
                String level       = snapshot.child("academicLevel").getValue(String.class);
                String goal        = snapshot.child("studyGoal").getValue(String.class);
                String mode        = snapshot.child("modePreference").getValue(String.class);

                if (institution != null) etInstitution.setText(institution);
                if (bio != null)         etBio.setText(bio);

                // Restore chip selections
                if (level != null) selectChip(chipGroupLevel, level);
                if (goal  != null) selectChip(chipGroupGoal,  goal);
                if (mode  != null) selectChip(chipGroupMode,  mode);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void saveProfile() {
        String name        = etName.getText() != null ? etName.getText().toString().trim() : "";
        String institution = etInstitution.getText() != null ? etInstitution.getText().toString().trim() : "";
        String bio         = etBio.getText() != null ? etBio.getText().toString().trim() : "";
        String level       = getSelectedChipValue(chipGroupLevel);
        String goal        = getSelectedChipValue(chipGroupGoal);
        String mode        = getSelectedChipValue(chipGroupMode);

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.btnSaveProfile).setEnabled(false);

        // 1. Update Firebase Auth displayName
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            user.updateProfile(profileUpdates);
        }

        // 2. Write to RTDB
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("institution", institution);
        updates.put("bio", bio);
        if (level != null) updates.put("academicLevel", level);
        if (goal  != null) updates.put("studyGoal", goal);
        if (mode  != null) updates.put("modePreference", mode);
        updates.put("profileUpdatedAt", System.currentTimeMillis());

        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Profile saved ✓", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    findViewById(R.id.btnSaveProfile).setEnabled(true);
                    Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /** Maps chip label text to a database key value. */
    private String getSelectedChipValue(ChipGroup group) {
        int id = group.getCheckedChipId();
        if (id == View.NO_ID) return null;
        Chip chip = group.findViewById(id);
        if (chip == null) return null;
        // Normalise: "High School" → "HIGH_SCHOOL", "In-Person" → "IN_PERSON" etc.
        return chip.getText().toString()
                .toUpperCase()
                .replace(" ", "_")
                .replace("-", "_");
    }

    /** Selects the chip in a group whose normalised text matches the stored value. */
    private void selectChip(ChipGroup group, String value) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof Chip) {
                Chip c = (Chip) child;
                String norm = c.getText().toString()
                        .toUpperCase()
                        .replace(" ", "_")
                        .replace("-", "_");
                if (norm.equals(value)) {
                    c.setChecked(true);
                    break;
                }
            }
        }
    }

    private void styleChipGroup(ChipGroup group) {
        int coralColor = getColor(R.color.warm_coral);
        int navyColor  = getColor(R.color.deep_navy);
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setChipBackgroundColorResource(android.R.color.transparent);
                chip.setCheckedIconVisible(false);
                // Listener to tint on select
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        chip.setChipBackgroundColor(
                                android.content.res.ColorStateList.valueOf(coralColor));
                        chip.setTextColor(android.graphics.Color.WHITE);
                    } else {
                        chip.setChipBackgroundColor(
                                android.content.res.ColorStateList.valueOf(
                                        getColor(R.color.input_fill)));
                        chip.setTextColor(navyColor);
                    }
                });
            }
        }
    }
}
