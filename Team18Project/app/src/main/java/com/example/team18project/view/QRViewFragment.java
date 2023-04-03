package com.example.team18project.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.team18project.TestSettings;
import com.example.team18project.model.CommentArrayAdapter;
import com.example.team18project.R;
import com.example.team18project.controller.QRViewController;
import com.example.team18project.model.Comment;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;

import java.util.ArrayList;

/**
 * A fragment used to display all information about a QR code
 */
public class QRViewFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "player";
    private static final String ARG_PARAM2 = "code";

    private Player player;
    private QRCode code;
    private CommentArrayAdapter commentAdapter;
    private QRViewController controller;
    private ArrayAdapter<String> otherPlayerAdapter;
    private ArrayList<String> otherPlayerList;

    public QRViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param code The QR code being viewed
     * @return A new instance of fragment QRViewFragment.
     */
    public static QRViewFragment newInstance(Player player, QRCode code) {
        QRViewFragment fragment = new QRViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, player);
        args.putParcelable(ARG_PARAM2, code);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            player = getArguments().getParcelable(ARG_PARAM1);
            code = getArguments().getParcelable(ARG_PARAM2);
            controller = new QRViewController(player,code);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_view, container, false);
        ImageView visual = view.findViewById(R.id.qr_visual);
        TextView name = view.findViewById(R.id.qrcode_name);
        TextView score = view.findViewById(R.id.qrcode_score);
        TextView location = view.findViewById(R.id.qrcode_location);
        ListView commentList = view.findViewById(R.id.comment_list);
        ListView otherPlayerListView = view.findViewById(R.id.other_player_list);
        Button commentButton = view.findViewById(R.id.post_comment_button);
        Button viewImagesButton = view.findViewById(R.id.view_images_button);
        EditText commentEditText = view.findViewById(R.id.edit_text_comment);

        String latitude;
        String longitude;

        if (code.getLatitude() == QRCode.NULL_LOCATION) {
            latitude = "null location";
            longitude = "null location";
        } else {
            latitude = Double.toString(code.getLatitude());
            longitude = Double.toString(code.getLongitude());
        }

        visual.setImageBitmap(code.getVisual(getContext(),2));
        name.setText(code.getName());
        score.setText("Score: " + Integer.toString(code.getScore()));
        location.setText("Latitude: " + latitude + "\nLongitude: " + longitude);

        otherPlayerList = new ArrayList<>();
        otherPlayerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, otherPlayerList);
        otherPlayerListView.setAdapter(otherPlayerAdapter);
        controller.getOtherPlayers(otherPlayerList,otherPlayerAdapter);

        commentAdapter = new CommentArrayAdapter(getContext(),code.getComments());
        commentList.setAdapter(commentAdapter);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = commentEditText.getText().toString();
                controller.postComment(text);

                //update views
                commentAdapter.notifyDataSetChanged();
                commentEditText.setText("");
            }
        });


        viewImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(ViewImagesFragment.newInstance(player, code));
            }
        });
        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Comment clicked = (Comment) commentList.getItemAtPosition(position);

                //check if this a comment posted by the user
                Boolean isPoster = false;
                if (TestSettings.getInstance().isTesting()) {
                    //isTesting is true, check test ID
                    if (TestSettings.getInstance().getTestAndroidID().equals(clicked.getPosterId())) {
                        isPoster = true;
                    }
                }
                else if (Settings.Secure.getString(getContext().getContentResolver(),Settings.Secure.ANDROID_ID).equals(clicked.getPosterId())) {
                    //isTesting is false, check device ID
                    isPoster = true;
                }

                //show menu
                new CommentMenuFragment(clicked,isPoster,new CommentMenuFragment.OnDeleteListener() {
                    @Override
                    public void onDelete() {
                        controller.deleteComment(clicked);
                        commentAdapter.notifyDataSetChanged();
                    }
                }).show(getParentFragmentManager(),"Menu");
            }
        });
        return view;
    }
}