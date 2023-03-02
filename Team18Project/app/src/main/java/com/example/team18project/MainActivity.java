package com.example.team18project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.team18project.databinding.ActivityMainBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Player player;
    private ArrayList<QRCode> qrData;
    private ListView qrList;
    private QRArrayAdapter qrAdapter;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        login(); //TODO maybe don't call if this is the 2nd visit to MainActivity

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

//    private void login() {
//        //TODO this is temp code, get player from Firestore or make new account
//        ArrayList<QRCode> list = new ArrayList<QRCode>();
//        list.add(new QRCode("Here",null,new ArrayList<Comment>(),9,9,9));
//        player = new Player(list,"","","","",false);
//    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
}