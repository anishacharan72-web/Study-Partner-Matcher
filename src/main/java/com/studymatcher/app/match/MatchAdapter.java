package com.studymatcher.app.match;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.studymatcher.app.R;
import com.studymatcher.app.model.Match;

import java.util.List;

/**
 * RecyclerView adapter for match suggestion cards.
 * Each card: avatar, name, institution, subject tags, score badge, accept/decline buttons.
 */
public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    public interface OnMatchAction {
        void onAction(Match match);
    }

    private List<Match> matches;
    private final OnMatchAction onAccept;
    private final OnMatchAction onDecline;

    public MatchAdapter(List<Match> matches, OnMatchAction onAccept, OnMatchAction onDecline) {
        this.matches   = matches;
        this.onAccept  = onAccept;
        this.onDecline = onDecline;
    }

    public void updateMatches(List<Match> newMatches) {
        this.matches = newMatches;
        notifyDataSetChanged();
    }

    public void removeMatch(Match match) {
        int idx = matches.indexOf(match);
        if (idx >= 0) {
            matches.remove(idx);
            notifyItemRemoved(idx);
        }
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match_card, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        holder.bind(matches.get(position));
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    class MatchViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivAvatar;
        private final TextView  tvName, tvInstitution, tvScore;
        private final ChipGroup chipGroupSubjects;
        private final MaterialButton btnAccept, btnDecline;

        MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar        = itemView.findViewById(R.id.ivAvatar);
            tvName          = itemView.findViewById(R.id.tvName);
            tvInstitution   = itemView.findViewById(R.id.tvInstitution);
            tvScore         = itemView.findViewById(R.id.tvScore);
            chipGroupSubjects = itemView.findViewById(R.id.chipGroupSubjects);
            btnAccept       = itemView.findViewById(R.id.btnAccept);
            btnDecline      = itemView.findViewById(R.id.btnDecline);
        }

        void bind(Match match) {
            if (match.partner == null) return;

            tvName.setText(match.partner.name);
            tvInstitution.setText(match.partner.institution);
            tvScore.setText(match.score + "%");

            // Load avatar
            if (match.partner.profilePhotoUrl != null && !match.partner.profilePhotoUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(match.partner.profilePhotoUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            }

            // Subject chips
            chipGroupSubjects.removeAllViews();
            if (match.partner.subjectIds != null) {
                int limit = Math.min(match.partner.subjectIds.size(), 4);
                for (int i = 0; i < limit; i++) {
                    Chip chip = new Chip(itemView.getContext());
                    chip.setText(match.partner.subjectIds.get(i));
                    chip.setChipBackgroundColorResource(R.color.lavender);
                    chip.setTextColor(itemView.getContext().getColor(R.color.deep_navy));
                    chip.setChipCornerRadius(100f);
                    chip.setClickable(false);
                    chipGroupSubjects.addView(chip);
                }
            }

            btnAccept.setOnClickListener(v -> onAccept.onAction(match));
            btnDecline.setOnClickListener(v -> onDecline.onAction(match));
        }
    }
}
