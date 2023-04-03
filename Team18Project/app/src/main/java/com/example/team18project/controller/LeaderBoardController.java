package com.example.team18project.controller;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.team18project.view.LeaderboardFragment;
import com.example.team18project.view.SearchFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;

public class LeaderBoardController {
    public LeaderBoardController() {

    }

    public void generateUserListHighScore(final LeaderboardFragment.UserListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Players").orderBy("highscore", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int count = 1;
                        ArrayList<String> usernameFilteredUserList = new ArrayList<>();
                        ArrayList<String> Usernames = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String newUser = document.get("username").toString();
                                String FormatedNewUser = count + ": " + newUser + " " + document.get("highscore").toString();
                                usernameFilteredUserList.add(FormatedNewUser);
                                Usernames.add(newUser);
                                count++;
                            }
                            callback.onUserListGenerated(usernameFilteredUserList, Usernames);
                        } else {
                            Log.d("SearchError:", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void generateUserListScans(final LeaderboardFragment.UserListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Players").orderBy("QRCount", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int count = 1;
                        ArrayList<String> usernameFilteredUserList = new ArrayList<>();
                        ArrayList<String> Usernames = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String newUser = document.get("username").toString();
                                String FormatednewUser = count + ": " + newUser + " " + document.get("QRCount").toString();
                                usernameFilteredUserList.add(FormatednewUser);
                                Usernames.add(newUser);
                                count++;
                            }
                            callback.onUserListGenerated(usernameFilteredUserList, Usernames);
                        } else {
                            Log.d("SearchError:", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void generateUserBestQR(final LeaderboardFragment.UserListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Players").orderBy("BestQRScore", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int count = 1;
                        ArrayList<String> usernameFilteredUserList = new ArrayList<>();
                        ArrayList<String> Usernames = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String newUser = document.get("username").toString();
                                String FormatnewUser = count + ": " + newUser + " " + document.get("BestQRScore").toString();
                                usernameFilteredUserList.add(FormatnewUser);
                                Usernames.add(newUser);
                                count++;
                            }
                            callback.onUserListGenerated(usernameFilteredUserList, Usernames);
                        } else {
                            Log.d("SearchError:", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
