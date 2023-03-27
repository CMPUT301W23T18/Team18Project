package com.example.team18project;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchController {
    public SearchController() {
        
    }

    public void generateUserList(final SearchFragment.UserListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Players").whereEqualTo("isHidden", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Pair<String, String>> userList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Pair<String, String> newUser = new Pair<>(document.getId(), document.get("username").toString());
                                userList.add(newUser);
                            }
                            callback.onUserListGenerated(userList);
                        } else {
                            Log.d("SearchError:", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
