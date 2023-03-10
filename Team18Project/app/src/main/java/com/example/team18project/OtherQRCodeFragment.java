package com.example.team18project;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import java.io.File;



public class OtherQRCodeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<QRCode> otherQRCodeList = new ArrayList<QRCode>();
        QRArrayAdapter qrArrayAdapter = new QRArrayAdapter(getContext(), otherQRCodeList);
        getOtherQRCode(otherQRCodeList, qrArrayAdapter);
    }


    private void getOtherQRCode(ArrayList<QRCode> otherQRCodeList, QRArrayAdapter qrArrayAdapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference qrCodesColl = db.collection("QRCodes");

        qrCodesColl.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                otherQRCodeList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    //Log.d(TAG, String.valueOf(doc.getData().get("Province Name")));
                    Double longitude = doc.getDouble("longitude");
                    Double latitude = doc.getDouble("latitude");
                    String value = doc.getString("value");
                    ArrayList<Comment> comments = (ArrayList<Comment>) doc.get("comments");
                    ArrayList<String> photoIds = (ArrayList<String>) doc.get("photo");
                    otherQRCodeList.add(new QRCode(value, photoIds, comments, longitude, latitude)); // Adding the cities and provinces from FireStore
                }
                qrArrayAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
            }
        });

        }
    }