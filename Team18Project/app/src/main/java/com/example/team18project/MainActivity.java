package com.example.team18project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Player player;
    private ArrayList<QRCode> qrData;
    private ListView qrList;
    private QRArrayAdapter qrAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login(); //TODO maybe don't call if this is the 2nd visit to MainActivity

        qrData = player.getCodes();

        //make ListView
        qrList = (ListView) findViewById(R.id.qr_list);
        qrAdapter = new QRArrayAdapter(this, qrData);
        qrList.setAdapter(qrAdapter);
    }

    private void login() {
        //TODO this is temp code, get player from Firestore or make new account
        ArrayList<QRCode> list = new ArrayList<QRCode>();
        list.add(new QRCode("Here",null,new ArrayList<Comment>(),9,9,9));
        player = new Player(list,"","","","",false);
    }
}