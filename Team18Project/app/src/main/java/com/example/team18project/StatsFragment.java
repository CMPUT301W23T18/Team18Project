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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    // TODO: Rename and change types of parameters
    private int totalScore;
    private int high_score;
    private int low_score;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param totalScore Score of all player QR codes added together
     * @return A new instance of fragment StatsFragment.
     */
    public static StatsFragment newInstance(int totalScore,int highest_score,int lowest_score) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, totalScore);
        args.putInt(ARG_PARAM2, highest_score);
        args.putInt(ARG_PARAM3, lowest_score);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            totalScore = getArguments().getInt(ARG_PARAM1);
            high_score = getArguments().getInt(ARG_PARAM2);
            low_score = getArguments().getInt(ARG_PARAM3);
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
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_stats, container, false);
        return view;
    }
}