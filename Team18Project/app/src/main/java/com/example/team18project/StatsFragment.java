package com.example.team18project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {

    // Fields related to player information and their QR codes
    private Player player;
    private QRCode lowestScoredQR;
    private QRCode highestScoredQR;
    private QRCode blank;

    // Fields related to QR score and information
    private int totalScore;
    private int highScore;
    private int lowScore;
    private int totalCount;

    // Fields related to QR name and image
    private String lowName;
    private String highName;
    private Bitmap lowImage;
    private Bitmap highImage;

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
            // retrieve the highest and lowest QR codes
            highestScoredQR = player.getHighestQRCode();
            lowestScoredQR = player.getLowestQRCode();
            // retrieve QR information stored in the player
            totalCount = player.totalAmountOfQRCodes();
            totalScore = player.totalQRScore();
            // retrieve QR information for the highest Scored QR code or set defaults if no QR are associated with the account
            if (highestScoredQR != null) {
                highScore = highestScoredQR.getScore();
                highName = highestScoredQR.getName();
                highImage = highestScoredQR.getVisual(getContext(),2);
            } else {
                highScore = 0;
                highName = "NO QR CODES WERE FOUND FOR THIS ACCOUNT";
                highImage = blank_bitmap(getContext(), 2);
            }
            // retrieve QR information for the lowest Scored QR code or set defaults if no QR are associated with the account
            if (lowestScoredQR != null) {
                lowScore = lowestScoredQR.getScore();
                lowName = lowestScoredQR.getName();
                lowImage = lowestScoredQR.getVisual(getContext(),2);
            } else {
                lowScore = 0;
                lowName = "NO QR CODES WERE FOUND FOR THIS ACCOUNT";
                lowImage = blank_bitmap(getContext(), 2);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_stats, null);
        // display the information that was stored in the player
        TextView total_count = view.findViewById(R.id.QRTotalCount);
        total_count.setText(Integer.toString(totalCount));

        TextView total_score = view.findViewById(R.id.QRTotalScore);
        total_score.setText(Integer.toString(totalScore));

        // display the information related to the highest Scored QR code of the player
        TextView high_score = view.findViewById(R.id.HighScoreQRscore);
        high_score.setText("Score: " + Integer.toString(highScore));

        TextView high_name = view.findViewById(R.id.HighScoreQRname);
        high_name.setText(highName);

        ImageView high_image = view.findViewById(R.id.HighScoreQRImage);
        high_image.setImageBitmap(highImage);

        // display the information related to the lowest Scored QR code of the player
        TextView low_score = view.findViewById(R.id.LowScoreQRscore);
        low_score.setText("Score: " + Integer.toString(lowScore));

        TextView low_name = view.findViewById(R.id.LowScoreQRname);
        low_name.setText(lowName);

        ImageView low_image = view.findViewById(R.id.LowScoreQRImage);
        low_image.setImageBitmap(lowImage);

        // display the information related to the leader board
        TextView leaderBoard = view.findViewById(R.id.leaderboardRank);
        leaderBoard.setText("0");

        return view;
    }

    private Bitmap blank_bitmap(Context context, int scaleFactor) {
        Bitmap blank = BitmapFactory.decodeResource(context.getResources(), R.drawable.imagegen_nothing);

        int baseWidth = blank.getWidth();
        int baseHeight = blank.getHeight();

        int newWidth = baseWidth * scaleFactor;
        int newHeight = baseHeight * scaleFactor;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);

        Bitmap blankMap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        // Draw all the scaled images over each other to create our unique sprite
        Canvas canvas = new Canvas(blankMap);

        Bitmap scaledBody = Bitmap.createBitmap(blank, 0, 0, baseWidth, baseHeight, matrix, true);
        canvas.drawBitmap(scaledBody, 0, 0, null);
        return blankMap;
    }
}