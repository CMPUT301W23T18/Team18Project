package com.example.team18project;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.team18project.model.Comment;
import com.example.team18project.model.QRArrayAdapter;
import com.example.team18project.model.QRCode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class OtherQRCodeFragment extends Fragment {

    private ArrayList<QRCode> otherQRCodeList;
    private ListView qrListView;
    private QRArrayAdapter qrAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otherQRCodeList = new ArrayList<QRCode>();
        qrListView = (ListView) getView().findViewById(R.id.other_qrcode_list);
        qrAdapter = new QRArrayAdapter(getContext(), otherQRCodeList);
        qrListView.setAdapter(qrAdapter);
        getOtherQRCode(otherQRCodeList, qrAdapter);
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