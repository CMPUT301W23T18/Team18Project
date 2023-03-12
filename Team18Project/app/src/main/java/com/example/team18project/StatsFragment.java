package com.example.team18project;

import android.os.Bundle;
import android.util.Log;
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
    private int highScore;
    private int lowScore;
    private int qrCodesScanned;
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
            if (player.getHighestQRCode() != null) {
                highScore = player.getHighestQRCode().getScore();
            } else {
                highScore = 0;
            }
            if (player.getLowestQRCode() != null) {
                lowScore = player.getLowestQRCode().getScore();
            } else {
                lowScore = 0;
            }
            qrCodesScanned = player.totalAmountOfQRCodes();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_stats, null);
        TextView tScore = view.findViewById(R.id.total_score_number_textView);
        tScore.setText(Integer.toString(totalScore));

        TextView high_score = view.findViewById(R.id.High_qr_score_number_textView);
        high_score.setText(Integer.toString(highScore));

        TextView low_score = view.findViewById(R.id.lowest_qr_score_number_textView);
        low_score.setText(Integer.toString(lowScore));

        TextView total_qr_codes = view.findViewById(R.id.Codes_scanned_numebr_textView);
        total_qr_codes.setText(Integer.toString(qrCodesScanned));
        return view;
    }
}