package com.studymatcher.app.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.studymatcher.app.R;
import com.studymatcher.app.ui.OnboardingAdapter;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

/**
 * OnboardingActivity — 3-slide intro with ViewPager2 + dots indicator.
 */
public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private DotsIndicator dotsIndicator;
    private MaterialButton btnNext;
    private MaterialButton btnSkip;

    private static final int TOTAL_SLIDES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager    = findViewById(R.id.viewPager);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        btnNext      = findViewById(R.id.btnNext);
        btnSkip      = findViewById(R.id.btnSkip);

        OnboardingAdapter adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);
        dotsIndicator.attachTo(viewPager);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == TOTAL_SLIDES - 1) {
                    btnNext.setText(R.string.get_started);
                    btnSkip.setVisibility(android.view.View.GONE);
                } else {
                    btnNext.setText(R.string.next);
                    btnSkip.setVisibility(android.view.View.VISIBLE);
                }
            }
        });

        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < TOTAL_SLIDES - 1) {
                viewPager.setCurrentItem(current + 1);
            } else {
                goToRegister();
            }
        });

        btnSkip.setOnClickListener(v -> goToRegister());
    }

    private void goToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }
}
