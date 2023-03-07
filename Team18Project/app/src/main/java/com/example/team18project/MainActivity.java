package com.example.team18project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.team18project.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Player player;
    private FirebaseFirestore db;
    //private ArrayList<QRCode> qrData;
    //private ListView qrList;
    //private QRArrayAdapter qrAdapter;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        login();

//        qrData = player.getCodes();
//
//        //make ListView
//        qrList = (ListView) findViewById(R.id.qr_list);
//        qrAdapter = new QRArrayAdapter(this, qrData);
//        qrList.setAdapter(qrAdapter);

        replaceFragment(new HomeFragment());

        binding.navBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_icon: replaceFragment(new HomeFragment()); break;
                case R.id.all_qr_codes_icon: replaceFragment(new AllQRCodesFragment()); break;
                case R.id.search_icon: replaceFragment(new SearchFragment()); break;
                case R.id.stats_icon: replaceFragment(new StatsFragment()); break;
                case R.id.profile_icon: replaceFragment(new ProfileFragment()); break;
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
        CollectionReference playersColl = db.collection("Players");
        DocumentReference playerReference = playersColl.document(id);

        //try to read account from database
        playerReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username = documentSnapshot.getString("username");

                //user doesn't have an account yet, make new one
                if (username == null) {
                    player = new Player(id);
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

                player = new Player(codes,id,username,email,phoneNumber,isHidden);
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
}