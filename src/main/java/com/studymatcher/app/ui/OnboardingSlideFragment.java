package com.studymatcher.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.studymatcher.app.R;

/** Single onboarding slide fragment. */
public class OnboardingSlideFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESC  = "desc";

    public static OnboardingSlideFragment newInstance(int titleRes, int descRes) {
        OnboardingSlideFragment f = new OnboardingSlideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, titleRes);
        args.putInt(ARG_DESC, descRes);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_slide, container, false);
        if (getArguments() != null) {
            ((TextView) view.findViewById(R.id.tvSlideTitle))
                    .setText(getArguments().getInt(ARG_TITLE));
            ((TextView) view.findViewById(R.id.tvSlideDesc))
                    .setText(getArguments().getInt(ARG_DESC));
        }
        return view;
    }
}
