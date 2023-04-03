package com.example.team18project.view;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * A fragment used to represent the leaderboards
 */
public class LeaderboardFragment extends Fragment {
    public interface UserListCallback {
        void onUserListGenerated(ArrayList<String> usernameFormatedUserList, ArrayList<String> Usernames);
    }
    private FirebaseFirestore db;
    CollectionReference usersRef;
    private LeaderBoardController controller;
    private Player clickedPlayer;

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
                controller.generateUserListHighScore(new LeaderboardFragment.UserListCallback() {
                    @Override
                    public void onUserListGenerated(ArrayList<String> usernameFormatedUserList, ArrayList<String> Usernames) {
                        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, usernameFormatedUserList);
                        userListView.setAdapter(adapter);

                        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String username = Usernames.get(position);

                                // qureys fire base then returns a doucment that contaoins  all the player info to be passed to the stats Fragment
                                usersRef.whereEqualTo("username", username)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        String email = document.getString("email");
                                                        String phoneNumber = document.getString("phoneNumber");
                                                        boolean isHidden = document.getBoolean("isHidden");
                                                        ArrayList<DocumentReference> codeRefs = (ArrayList<DocumentReference>) document.get("codes");
                                                        ArrayList<QRCode> codes = new ArrayList<QRCode>();

                                                        for (int i = 0; i < codeRefs.size(); i++) {
                                                            codes.add(new QRCode(codeRefs.get(i)));
                                                            Log.d("codes",codes.get(i).toString());
                                                        }

                                                        clickedPlayer = new Player(codes,document.getId(),username,email,phoneNumber,isHidden);
                                                        MainActivity activity = (MainActivity) getActivity();
                                                        activity.replaceFragment(new StatsFragment().newInstance(clickedPlayer));
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });

                            }
                        });

                    }
                });
            }
        });

        TotalScansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.generateUserListScans(new LeaderboardFragment.UserListCallback() {
                    @Override
                    public void onUserListGenerated(ArrayList<String> usernameFormatedUserList, ArrayList<String> Usernames) {
                        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, usernameFormatedUserList);
                        userListView.setAdapter(adapter);
                        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String username = Usernames.get(position);

                                // qureys fire base then returns a doucment that contaoins  all the player info to be passed to the stats Fragment
                                usersRef.whereEqualTo("username", username)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        String email = document.getString("email");
                                                        String phoneNumber = document.getString("phoneNumber");
                                                        boolean isHidden = document.getBoolean("isHidden");
                                                        ArrayList<DocumentReference> codeRefs = (ArrayList<DocumentReference>) document.get("codes");
                                                        ArrayList<QRCode> codes = new ArrayList<QRCode>();

                                                        for (int i = 0; i < codeRefs.size(); i++) {
                                                            codes.add(new QRCode(codeRefs.get(i)));
                                                            Log.d("codes",codes.get(i).toString());
                                                        }

                                                        clickedPlayer = new Player(codes,document.getId(),username,email,phoneNumber,isHidden);
                                                        MainActivity activity = (MainActivity) getActivity();
                                                        activity.replaceFragment(new StatsFragment().newInstance(clickedPlayer));
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });

                            }
                        });
                    }
                });
            }
        });

        BestQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.generateUserBestQR(new LeaderboardFragment.UserListCallback() {
                    @Override
                    public void onUserListGenerated(ArrayList<String> usernameFormatedUserList, ArrayList<String> Usernames) {
                        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, usernameFormatedUserList);
                        userListView.setAdapter(adapter);

                        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String username = Usernames.get(position);

                                // qureys fire base then returns a doucment that contaoins  all the player info to be passed to the stats Fragment
                                usersRef.whereEqualTo("username", username)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        String email = document.getString("email");
                                                        String phoneNumber = document.getString("phoneNumber");
                                                        boolean isHidden = document.getBoolean("isHidden");
                                                        ArrayList<DocumentReference> codeRefs = (ArrayList<DocumentReference>) document.get("codes");
                                                        ArrayList<QRCode> codes = new ArrayList<QRCode>();

                                                        for (int i = 0; i < codeRefs.size(); i++) {
                                                            codes.add(new QRCode(codeRefs.get(i)));
                                                            Log.d("codes",codes.get(i).toString());
                                                        }

                                                        clickedPlayer = new Player(codes,document.getId(),username,email,phoneNumber,isHidden);
                                                        MainActivity activity = (MainActivity) getActivity();
                                                        activity.replaceFragment(new StatsFragment().newInstance(clickedPlayer));
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });

                            }
                        });
                    }
                });
            }
        });
        return view;
    }
}


