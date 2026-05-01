package com.studymatcher.app.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studymatcher.app.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * SubjectPickerActivity — searchable multi-select chip grid for subjects.
 * Persists selection to Firebase RTDB at /users/{uid}/subjects.
 */
public class SubjectPickerActivity extends AppCompatActivity {

    /** Master list of available subjects */
    private static final List<String> ALL_SUBJECTS = Arrays.asList(
            "Mathematics", "Calculus", "Linear Algebra", "Statistics", "Discrete Math",
            "Physics", "Quantum Mechanics", "Thermodynamics", "Electromagnetism",
            "Chemistry", "Organic Chemistry", "Biochemistry", "Physical Chemistry",
            "Biology", "Genetics", "Microbiology", "Ecology", "Anatomy",
            "Computer Science", "Data Structures", "Algorithms", "Machine Learning",
            "Deep Learning", "Computer Networks", "Operating Systems", "Databases",
            "Web Development", "Mobile Development", "Cloud Computing", "Cybersecurity",
            "Economics", "Microeconomics", "Macroeconomics", "Finance", "Accounting",
            "History", "Political Science", "Sociology", "Psychology", "Philosophy",
            "English Literature", "Creative Writing", "Linguistics",
            "Engineering", "Civil Engineering", "Mechanical Engineering", "Electrical Engineering",
            "Data Science", "Artificial Intelligence", "Robotics", "Signal Processing"
    );

    private ChipGroup chipGroupSubjects;
    private TextView tvSelectedCount;
    private TextInputEditText etSearch;
    private final Set<String> selectedSubjects = new HashSet<>();
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_picker);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        chipGroupSubjects = findViewById(R.id.chipGroupSubjects);
        tvSelectedCount   = findViewById(R.id.tvSelectedCount);
        etSearch          = findViewById(R.id.etSearch);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { finish(); return; }

        userRef = FirebaseDatabase.getInstance()
                .getReference("users").child(user.getUid());

        // Load existing subjects from RTDB then build chip UI
        userRef.child("subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String subj = child.getValue(String.class);
                    if (subj != null) selectedSubjects.add(subj);
                }
                buildChips(ALL_SUBJECTS);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                buildChips(ALL_SUBJECTS);
            }
        });

        // Search filter
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                String query = s.toString().toLowerCase(Locale.ROOT).trim();
                if (query.isEmpty()) {
                    buildChips(ALL_SUBJECTS);
                } else {
                    List<String> filtered = new ArrayList<>();
                    for (String subj : ALL_SUBJECTS) {
                        if (subj.toLowerCase(Locale.ROOT).contains(query)) filtered.add(subj);
                    }
                    buildChips(filtered);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Save button
        findViewById(R.id.btnDone).setOnClickListener(v -> saveSubjects());
    }

    private void buildChips(List<String> subjects) {
        chipGroupSubjects.removeAllViews();

        int coralColor  = getColor(R.color.warm_coral);
        int fillColor   = getColor(R.color.input_fill);
        int navyColor   = getColor(R.color.deep_navy);

        for (String subject : subjects) {
            Chip chip = new Chip(this);
            chip.setText(subject);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setChecked(selectedSubjects.contains(subject));

            // Initial colour
            boolean isSelected = selectedSubjects.contains(subject);
            chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(
                    isSelected ? coralColor : fillColor));
            chip.setTextColor(isSelected ? android.graphics.Color.WHITE : navyColor);
            chip.setTextSize(14f);
            chip.setChipCornerRadius(20f);

            chip.setOnCheckedChangeListener((buttonView, checked) -> {
                if (checked) {
                    selectedSubjects.add(subject);
                    chip.setChipBackgroundColor(
                            android.content.res.ColorStateList.valueOf(coralColor));
                    chip.setTextColor(android.graphics.Color.WHITE);
                } else {
                    selectedSubjects.remove(subject);
                    chip.setChipBackgroundColor(
                            android.content.res.ColorStateList.valueOf(fillColor));
                    chip.setTextColor(navyColor);
                }
                updateCountLabel();
            });

            chipGroupSubjects.addView(chip);
        }

        updateCountLabel();
    }

    private void updateCountLabel() {
        int count = selectedSubjects.size();
        tvSelectedCount.setText(count + " selected");
        tvSelectedCount.setTextColor(count > 0
                ? getColor(R.color.warm_coral)
                : getColor(R.color.text_muted));
    }

    private void saveSubjects() {
        if (selectedSubjects.isEmpty()) {
            Toast.makeText(this, "Select at least one subject", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert to a numbered map for RTDB (lists stored as maps)
        List<String> subjectList = new ArrayList<>(selectedSubjects);

        userRef.child("subjects").setValue(subjectList)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, subjectList.size() + " subjects saved ✓",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
}
