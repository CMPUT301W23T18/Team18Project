package com.example.team18project.controller;

import com.example.team18project.model.Player;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Controller class for the profile screen
 */
public class ProfileController {
    private DocumentReference playerRef;
    private Player player;

    /**
     * Constructs a new ProfileController
     * @param player The currently logged in player
     */
    public ProfileController(Player player) {
        this.player = player;
        this.playerRef = FirebaseFirestore.getInstance().collection("Players").document(player.getUid());
    }

    /**
     * Updates the username of the logged in player
     * @param newUsername The new username
     * @param listener A listener that will be called when the username is changed in Firebase
     */
    public void updateUsername(String newUsername, FirebaseWriter.OnWrittenListener listener) {
        FirebaseWriter.getInstance().updateUsername(player,newUsername,listener);
        player.setUsername(newUsername);
    }

    /**
     * Updates the email of the logged in player
     * @param newEmail The new email
     */
    public void updateEmail(String newEmail) {
        playerRef.update("email", newEmail);
        player.setEmail(newEmail);
    }

    /**
     * Updates the phone number of the logged in player
     * @param newPhoneNumber The new phone number
     */
    public void updatePhoneNumber(String newPhoneNumber) {
        playerRef.update("phoneNumber", newPhoneNumber);
        player.setPhoneNumber(newPhoneNumber);
    }

    /**
     * Updates whether the logged in player is hidden or not
     * @param newIsHidden The new value of isHidden
     */
    public void updateIsHidden(boolean newIsHidden) {
        playerRef.update("isHidden", newIsHidden);
        player.setHidden(newIsHidden);
    }
}
