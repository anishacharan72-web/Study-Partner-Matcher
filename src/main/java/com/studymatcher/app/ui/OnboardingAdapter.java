package com.studymatcher.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.fragment.app.Fragment;

import com.studymatcher.app.R;

/**
 * Adapter for the 3-slide onboarding ViewPager2.
 */
public class OnboardingAdapter extends FragmentStateAdapter {

    private static final int[] TITLES = {
            R.string.onboard_title_1,
            R.string.onboard_title_2,
            R.string.onboard_title_3
    };

    private static final int[] DESCRIPTIONS = {
            R.string.onboard_desc_1,
            R.string.onboard_desc_2,
            R.string.onboard_desc_3
    };

    public OnboardingAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return OnboardingSlideFragment.newInstance(
                TITLES[position], DESCRIPTIONS[position]);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
