package com.example.team18project.controller;

import com.example.team18project.model.Player;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileController {
    private DocumentReference playerRef;
    private Player player;

    public ProfileController(Player player) {
        this.player = player;
        this.playerRef = FirebaseFirestore.getInstance().collection("Players").document(player.getUid());
    }

    public void updateUsername(String newUsername, FirebaseWriter.OnWrittenListener listener) {
        FirebaseWriter.getInstance().updateUsername(player,newUsername,listener);
        player.setUsername(newUsername);
    }

    public void updateEmail(String newEmail) {
        playerRef.update("email", newEmail);
    }

    public void updatePhoneNumber(String newPhoneNumber) {
        playerRef.update("phoneNumber", newPhoneNumber);
    }

    public void updateIsHidden(boolean newIsHidden) {
        playerRef.update("isHidden", newIsHidden);
    }
}
