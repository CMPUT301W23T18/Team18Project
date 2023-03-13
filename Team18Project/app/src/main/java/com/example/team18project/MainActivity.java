package com.example.team18project;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.team18project.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Player player;
    private FirebaseFirestore db;
    private ActivityMainBinding binding;
    private boolean isTesting = false;
    public String testAndroidID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //check if in testing mode
        Intent intent = getIntent();
        isTesting = intent.getBooleanExtra("isTesting",false);
        testAndroidID = intent.getStringExtra("testAndroidID");

        db = FirebaseFirestore.getInstance();
        login();
    }

    /**
     * Initializes the frame and navigation bar of the main activity. This needs to be done after
     * the player is logged in, which is why this code is separate from onCreate
     * @author Michael Schaefer-Pham
     */
    private void activityInit() {
        replaceFragment(HomeFragment.newInstance(player));

        binding.navBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_icon:player.update(); replaceFragment(HomeFragment.newInstance(player)); break;
                case R.id.all_qr_codes_icon: replaceFragment(new AllQRCodesFragment()); break;
                case R.id.search_icon: replaceFragment(new SearchFragment().newInstance()); break;
                case R.id.stats_icon: replaceFragment(new StatsFragment().newInstance(player)); break;
                case R.id.profile_icon: replaceFragment(new ProfileFragment().newInstance(player)); break;
            }
            return true;
        });
    }

    /**
     * Get the user's account data from the database or makes a new account if the user doesn't
     * have one yet, then sets this object's player field to a Player object containing that data
     * @author Michael Schaefer-Pham
     */
    private void login() {
        String id = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        if (isTesting) {
            id = this.testAndroidID;
        }
        //listener needs variable to be effectively final
        String finalId = id;

        CollectionReference playersColl = db.collection("Players");
        DocumentReference playerReference = playersColl.document(id);
        Task readTask = playerReference.get();

        //try to read account from database
        readTask.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username = documentSnapshot.getString("username");

                //user doesn't have an account yet, make new one
                if (username == null) {
                    player = new Player(finalId);
                    Map<String, Object> data = new HashMap<>();
                    data.put("codes",new ArrayList<DocumentReference>());
                    data.put("email","");
                    data.put("isHidden",true);
                    data.put("phoneNumber","");
                    data.put("username","");
                    playerReference.set(data);
                    return;
                }
                //user account was found
                String email = documentSnapshot.getString("email");
                String phoneNumber = documentSnapshot.getString("phoneNumber");
                boolean isHidden = documentSnapshot.getBoolean("isHidden");
                ArrayList<DocumentReference> codeRefs = (ArrayList<DocumentReference>) documentSnapshot.get("codes");
                ArrayList<QRCode> codes = new ArrayList<QRCode>();

                //convert QR code DocumentReferences to QRCode objects
                for (int i = 0; i < codeRefs.size(); i++) {
                    codes.add(new QRCode(codeRefs.get(i)));
                }

                player = new Player(codes,finalId,username,email,phoneNumber,isHidden);
                activityInit();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Gets the logged in player. Only available for testing mode.
     * @return The logged in player if testing mode is on, or null otherwise
     */
    public Player getPlayer() {
        if (isTesting) {
            return this.player;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // This is important, otherwise the result will not be passed to the fragment
        super.onActivityResult(requestCode, resultCode, data);
//        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
//            fragment.onActivityResult(requestCode, resultCode, data);
//        }

    }
}