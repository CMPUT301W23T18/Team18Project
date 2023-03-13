package com.example.team18project;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private FirebaseFirestore db;
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
        playerRef = db.collection("Players").document(currentPlayer.getUid());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile, null);
        userNameText =  view.findViewById(R.id.UserName_editText);
        emailText = view.findViewById(R.id.playerEmail_TextEmailAddress);
        userPhoneText = view.findViewById(R.id.player_phone_number_editTextPhone);
        hideSwitch = view.findViewById(R.id.hide_Account_switch);
        Button submitUser = view.findViewById(R.id.changeUsername);

        userNameText.setText(currentPlayer.getUsername());
        emailText.setText(currentPlayer.getEmail());
        userPhoneText.setText(currentPlayer.getPhoneNumber());
        // updating player username
        submitUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the button is clicked
                playerRef.update("username", userNameText.getText().toString());
            }
        });


        // updating email
        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

                currentPlayer.setEmail(s.toString());
                Log.v("Email",currentPlayer.getEmail());

            }
        });
        userPhoneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                currentPlayer.setPhoneNumber(s.toString());
                Log.d("Phone", currentPlayer.getPhoneNumber());
                //Toast.makeText(getContext(), "Invalid Phone number", Toast.LENGTH_SHORT).show();

            }
        });

        // changing if profile is hidden or not
        hideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentPlayer.setHidden(isChecked);
                Log.d("Switch State=", ""+isChecked);
            }
        });


        return view;
    }


}