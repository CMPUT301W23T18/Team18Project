package com.example.team18project;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile, null);
        userNameText =  view.findViewById(R.id.UserName_editText);
        emailText = view.findViewById(R.id.playerEmail_TextEmailAddress);
        userPhoneText = view.findViewById(R.id.player_phone_number_editTextPhone);
        hideSwitch = view.findViewById(R.id.hide_Account_switch);

        userNameText.setText(currentPlayer.getUsername());
        emailText.setText(currentPlayer.getEmail());
        userPhoneText.setText(currentPlayer.getPhoneNumber());

        return view;
    }
    /*
    public void onSwitchClick(View view) {
        if(hideSwitch.isChecked() == true){
            currentPlayer.setHidden(true);
            Toast.makeText(getContext(), "hidden", Toast.LENGTH_SHORT).show();
        }
        else {
            currentPlayer.setHidden(false);
            Toast.makeText(getContext(), "not hidden", Toast.LENGTH_SHORT).show();
        }
    }

     */

}