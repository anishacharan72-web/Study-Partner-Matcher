package com.studymatcher.app.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studymatcher.app.R;
import com.studymatcher.app.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * ProfileFragment — reads Firebase user data from Auth + RTDB,
 * navigates to ProfileEditActivity and SubjectPickerActivity.
 */
public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvRatingText, tvInstitution,
            tvStudyGoal, tvModePref, tvSubjectsList;
    private CircleImageView ivPhoto;
    private DatabaseReference userRef;
    private ValueEventListener profileListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName        = view.findViewById(R.id.tvProfileName);
        tvEmail       = view.findViewById(R.id.tvProfileEmail);
        tvRatingText  = view.findViewById(R.id.tvRatingText);
        tvInstitution = view.findViewById(R.id.tvInstitution);
        tvStudyGoal   = view.findViewById(R.id.tvStudyGoal);
        tvModePref    = view.findViewById(R.id.tvModePreference);
        ivPhoto       = view.findViewById(R.id.ivProfilePhoto);

        // Try to find the subjects label (added in fragment_profile)
        tvSubjectsList = view.findViewById(R.id.tvSubjectsList);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { return; }

        // Auth data
        String displayName = user.getDisplayName();
        tvName.setText(displayName != null && !displayName.isEmpty() ? displayName : "Student");
        tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");

        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .into(ivPhoto);
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        // Button actions — navigate to real activities
        view.findViewById(R.id.btnEditProfile).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ProfileEditActivity.class)));

        view.findViewById(R.id.btnUpdateSubjects).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), SubjectPickerActivity.class)));

        view.findViewById(R.id.btnSignOut).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).signOut();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload RTDB data every time user returns from edit screens
        if (userRef != null) attachListener();

        // Refresh display name in case it was just updated
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && tvName != null) {
            user.reload().addOnCompleteListener(t -> {
                String name = user.getDisplayName();
                if (tvName != null)
                    tvName.setText(name != null && !name.isEmpty() ? name : "Student");
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (userRef != null && profileListener != null)
            userRef.removeEventListener(profileListener);
    }

    private void attachListener() {
        profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;

                String institution = snapshot.child("institution").getValue(String.class);
                String goal        = snapshot.child("studyGoal").getValue(String.class);
                String mode        = snapshot.child("modePreference").getValue(String.class);
                String level       = snapshot.child("academicLevel").getValue(String.class);
                Double rating      = snapshot.child("ratingAvg").getValue(Double.class);
                Long   ratingCount = snapshot.child("ratingCount").getValue(Long.class);
                String name        = snapshot.child("name").getValue(String.class);

                // Update display name from RTDB if it was just set
                if (name != null && !name.isEmpty() && tvName != null) {
                    tvName.setText(name);
                }

                if (tvInstitution != null)
                    tvInstitution.setText(institution != null ? institution : "Not set yet");

                if (tvStudyGoal != null)
                    tvStudyGoal.setText(goal != null ? formatValue(goal) : "Not set yet");

                if (tvModePref != null)
                    tvModePref.setText(mode != null ? formatValue(mode) : "Not set yet");

                if (tvRatingText != null) {
                    if (rating != null && ratingCount != null && ratingCount > 0) {
                        tvRatingText.setText(String.format("%.1f / 5.0 · %d rating%s",
                                rating, ratingCount, ratingCount == 1 ? "" : "s"));
                    } else {
                        tvRatingText.setText("New member · 0 ratings");
                    }
                }

                // Subjects list
                if (tvSubjectsList != null) {
                    List<String> subjects = new ArrayList<>();
                    for (DataSnapshot child : snapshot.child("subjects").getChildren()) {
                        String s = child.getValue(String.class);
                        if (s != null) subjects.add(s);
                    }
                    if (!subjects.isEmpty()) {
                        tvSubjectsList.setText(android.text.TextUtils.join("  ·  ", subjects));
                        tvSubjectsList.setVisibility(View.VISIBLE);
                    } else {
                        tvSubjectsList.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { /* silent */ }
        };
        userRef.addValueEventListener(profileListener);
    }

    /** Converts DB enum strings like "EXAM_PREP" → "Exam Prep" */
    private String formatValue(String raw) {
        if (raw == null) return "Not set yet";
        StringBuilder sb = new StringBuilder();
        for (String part : raw.split("_")) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(part.substring(0, 1).toUpperCase())
              .append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}
