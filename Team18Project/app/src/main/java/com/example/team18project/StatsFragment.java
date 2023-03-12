package com.example.team18project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {

    private int totalScore;
    private int high_score;
    private int low_score;
    private Player player;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player an instance of the player class
     * @return A new instance of fragment StatsFragment.
     */
    public static StatsFragment newInstance(Player player) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putParcelable("player", player);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            player = getArguments().getParcelable("player");
            totalScore = player.totalQRScore();
            try {
                high_score = player.getHighestQRCode().getScore();
            } catch (NullPointerException e) {
                high_score = -1;
            }
            try {
                low_score = player.getLowestQRCode().getScore();
            } catch (NullPointerException e) {
                low_score = -1;
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_stats, null);
        TextView tScore = view.findViewById(R.id.TotalScore);
        tScore.setText(Integer.toString(totalScore));

        TextView high_score = view.findViewById(R.id.highest_score);
        high_score.setText(Integer.toString(this.high_score));

        TextView low_score = view.findViewById(R.id.lowest_score);
        low_score.setText(Integer.toString(this.low_score));
        return view;
    }
}