package com.studymatcher.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.studymatcher.app.R;
import com.studymatcher.app.profile.ProfileSetupActivity;

/**
 * RegisterActivity — Firebase Email/Password + Google OAuth registration.
 * Input: Full Name, Email, Password (min 8 chars), Institution.
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPassword, tilInstitution;
    private TextInputEditText etName, etEmail, etPassword, etInstitution;
    private MaterialButton btnRegister, btnGoogleSignIn;
    private CircularProgressIndicator progressIndicator;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        tilName        = findViewById(R.id.tilName);
        tilEmail       = findViewById(R.id.tilEmail);
        tilPassword    = findViewById(R.id.tilPassword);
        tilInstitution = findViewById(R.id.tilInstitution);
        etName         = findViewById(R.id.etName);
        etEmail        = findViewById(R.id.etEmail);
        etPassword     = findViewById(R.id.etPassword);
        etInstitution  = findViewById(R.id.etInstitution);
        btnRegister    = findViewById(R.id.btnRegister);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        progressIndicator = findViewById(R.id.progressIndicator);

        btnRegister.setOnClickListener(v -> attemptRegister());

        btnGoogleSignIn.setOnClickListener(v ->
                GoogleSignInHelper.launchGoogleSignIn(this));

        findViewById(R.id.tvSignIn).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        // Clear previous errors
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilInstitution.setError(null);

        String name        = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email       = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password    = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String institution = etInstitution.getText() != null ? etInstitution.getText().toString().trim() : "";

        boolean valid = true;

        if (TextUtils.isEmpty(name) || name.length() > 80) {
            tilName.setError("Enter your full name (max 80 chars)");
            valid = false;
        }
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Enter your email address");
            valid = false;
        }
        if (password.length() < 8) {
            tilPassword.setError("Password must be at least 8 characters");
            valid = false;
        }
        if (TextUtils.isEmpty(institution) || institution.length() > 120) {
            tilInstitution.setError("Enter your institution (max 120 chars)");
            valid = false;
        }

        if (!valid) return;

        setLoading(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        // Send verification email
                        mAuth.getCurrentUser().sendEmailVerification()
                                .addOnCompleteListener(verifyTask -> {
                                    setLoading(false);
                                    Toast.makeText(this,
                                            getString(R.string.email_verification_sent),
                                            Toast.LENGTH_LONG).show();
                                    // Move to profile setup (with name + institution)
                                    Intent intent = new Intent(this, ProfileSetupActivity.class);
                                    intent.putExtra("name", name);
                                    intent.putExtra("institution", institution);
                                    startActivity(intent);
                                    finish();
                                });
                    } else {
                        setLoading(false);
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : getString(R.string.error_generic);
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
        btnGoogleSignIn.setEnabled(!loading);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleSignInHelper.handleActivityResult(this, requestCode, data);
    }
}
