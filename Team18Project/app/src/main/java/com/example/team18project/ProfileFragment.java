package com.example.team18project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Collections;

/**
 * A fragment used to represent the profile screen, where account information is displayed
 */
public class ProfileFragment extends Fragment {
    private ProfileController controller;

    private EditText userNameText;
    private EditText emailText;
    private EditText userPhoneText;
    private Player currentPlayer;

    private Button submitUser;
    private Button submitPhone;
    private Button submitEmail;
    private Switch hideSwitch;


    public static ProfileFragment newInstance(Player player) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("Player", player);
        fragment.setArguments(args);
        fragment.controller = new ProfileController(player);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentPlayer = getArguments().getParcelable("Player");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile, null);
        userNameText =  view.findViewById(R.id.UserName_editText);
        emailText = view.findViewById(R.id.playerEmail_TextEmailAddress);
        userPhoneText = view.findViewById(R.id.player_phone_number_editTextPhone);
        hideSwitch = view.findViewById(R.id.hide_Account_switch);
        submitUser = view.findViewById(R.id.SubmitchangeUsername);
        submitEmail = view.findViewById(R.id.submitEmailbutton);
        submitPhone = view.findViewById(R.id.submitPhoneButton);

        userNameText.setText(currentPlayer.getUsername());
        emailText.setText(currentPlayer.getEmail());
        userPhoneText.setText(currentPlayer.getPhoneNumber());

        // updating player username
        submitUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the button is clicked
                String newUsername = userNameText.getText().toString();
                controller.updateUsername(newUsername, new FirebaseWriter.OnWrittenListener() {
                    @Override
                    public void onWritten(boolean isSuccessful) {
                        if (!isSuccessful) {
                            // There is at least one instance of the data
                            userNameText.setText(currentPlayer.getUsername());
                            // implement pop up here
                            Toast.makeText(getContext(), "Username already in Use!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        submitPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.updatePhoneNumber(userPhoneText.getText().toString());
            }
        });

        submitEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.updateEmail(emailText.getText().toString());
            }
        });
        // changing if profile is hidden or not
        hideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                controller.updateIsHidden(isChecked);
            }
        });

        return view;
    }
}