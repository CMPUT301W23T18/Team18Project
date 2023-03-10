package com.example.team18project;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

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
     * Insure the player is in sink with firebase before switching fragments
     * @author Michael Schaefer-Pham, Dominyk Gallatin
     */
    private void activityInit() {
        replaceFragment(HomeFragment.newInstance(player));

        binding.navBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_icon:replaceFragment(HomeFragment.newInstance(player)); break;
                case R.id.all_qr_codes_icon: replaceFragment(new AllQRCodesFragment()); break;
                case R.id.search_icon: replaceFragment(new SearchFragment().newInstance()); break;
                case R.id.stats_icon:replaceFragment(new StatsFragment().newInstance(player)); break;
                case R.id.profile_icon:replaceFragment(new ProfileFragment().newInstance(player)); break;
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

                // Generate a new user than start then start the home frame and navigation bar
                player = new Player(codes,finalId,username,email,phoneNumber,isHidden);
                activityInit();
            }
        });
    }

    /**
     * Replace the current fragment viewed by the user
     * @param fragment An instance of the fragment we want to switch to
     */
    private void replaceFragment(Fragment fragment) {
        player.sync();
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

    /**
     * Allows for the onActivityResult method in the HomeFragment to be called. Necessary to
     * process newly scanned QRCodes.
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // This is important, otherwise the result will not be passed to the fragment
        super.onActivityResult(requestCode, resultCode, data);
    }
}