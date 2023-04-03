package com.example.team18project.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.team18project.R;
import com.example.team18project.controller.LeaderBoardController;
import com.example.team18project.controller.SearchController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * A fragment used to represent the leaderboards
 */
public class LeaderboardFragment extends Fragment {
    public interface UserListCallback {
        void onUserListGenerated(ArrayList<String> usernameFilteredUserList);
    }
    private FirebaseFirestore db;
    CollectionReference usersRef;
    DocumentReference playerRef;
    private LeaderBoardController controller;

    public static LeaderboardFragment newInstance() {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.controller = new LeaderBoardController();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("Players");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_leaderboards, null);
        Button TotalScoreButton = view.findViewById(R.id.Score);
        Button TotalScansButton = view.findViewById(R.id.Scans);
        Button BestQRButton = view.findViewById(R.id.bestQR);
        ListView userListView = view.findViewById(R.id.leaderBoard);

        TotalScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.generateUserListHighScore(new SearchFragment.UserListCallback() {
                    @Override
                    public void onUserListGenerated(ArrayList<String> usernameFilteredUserList) {
                        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, usernameFilteredUserList);
                        userListView.setAdapter(adapter);
                    }
                });
            }
        });

        TotalScansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.generateUserListScans(new SearchFragment.UserListCallback() {
                    @Override
                    public void onUserListGenerated(ArrayList<String> usernameFilteredUserList) {
                        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, usernameFilteredUserList);
                        userListView.setAdapter(adapter);
                    }
                });
            }
        });

        BestQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.generateUserBestQR(new SearchFragment.UserListCallback() {
                    @Override
                    public void onUserListGenerated(ArrayList<String> usernameFilteredUserList) {
                        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, usernameFilteredUserList);
                        userListView.setAdapter(adapter);
                    }
                });
            }
        });
        return view;
    }
}


