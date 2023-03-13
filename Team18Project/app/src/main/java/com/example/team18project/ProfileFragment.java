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
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private EditText userNameText;
    private EditText emailText;
    private EditText userPhoneText;
    private Player currentPlayer;

    private Button submitUser;
    private Button submitPhone;
    private Button submitEmail;
    private FirebaseFirestore db;
    CollectionReference usersRef;
    DocumentReference playerRef;

    private Switch hideSwitch;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player An instance of the player class
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(Player player) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("Player", player);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentPlayer = getArguments().getParcelable("Player");
        }
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("Players");
        playerRef = usersRef.document(currentPlayer.getUid());

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
                Query query = usersRef.whereIn("username", Arrays.asList(newUsername));

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot.isEmpty()) {
                                // There are no instances of the data
                                playerRef.update("username", newUsername);
                            } else {
                                // There is at least one instance of the data
                                userNameText.setText(currentPlayer.getUsername());
                            }
                        } else {
                            // Handle any errors
                        }
                    }
                });

            }
        });


        // updating email

        submitPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerRef.update("phoneNumber", userPhoneText.getText().toString());
            }
        });

        submitEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerRef.update("email", emailText.getText().toString());
            }
        });
        // changing if profile is hidden or not
        hideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                playerRef.update("isHidden", isChecked);
            }
        });


        return view;
    }


}