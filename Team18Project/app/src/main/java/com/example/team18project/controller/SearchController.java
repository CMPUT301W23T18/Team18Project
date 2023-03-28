package com.example.team18project.controller;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.team18project.view.SearchFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchController {
    public SearchController() {
        
    }

    public void generateUserList(String searchText, final SearchFragment.UserListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Players").whereEqualTo("isHidden", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> usernameFilteredUserList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String newUser = document.get("username").toString();
                                if (newUser.toLowerCase().contains(searchText.toLowerCase())) {
                                    usernameFilteredUserList.add(newUser);
                                }
                            }
                            callback.onUserListGenerated(usernameFilteredUserList);
                        } else {
                            Log.d("SearchError:", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
