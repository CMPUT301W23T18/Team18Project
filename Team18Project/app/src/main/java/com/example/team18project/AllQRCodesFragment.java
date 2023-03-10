package com.example.team18project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllQRCodesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllQRCodesFragment extends Fragment {

    private ArrayList<QRCode> otherQRCodeList;
    private ListView qrListView;
    private QRArrayAdapter qrAdapter;

    /**
     * Create a new instance of the AllQRCodesFragment and bundle any passed parameters
     * @return the instance of our new fragment
     */
    public static AllQRCodesFragment newInstance() {
        AllQRCodesFragment fragment = new AllQRCodesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Unbundled any parameters passed and add them to the native variables of the Fragment
     * @param savedInstanceState A bundle of all passed parameters to the AllQRCodesFragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        otherQRCodeList = new ArrayList<QRCode>();
        qrListView = (ListView) getView().findViewById(R.id.other_qrcode_list);
        qrAdapter = new QRArrayAdapter(getContext(), otherQRCodeList);
        qrListView.setAdapter(qrAdapter);
        getAllQRCode(otherQRCodeList, qrAdapter);
    }

    /**
     This method retrieves all QR codes from the Firestore database and updates the provided QRArrayAdapter.
     @param otherQRCodeList an ArrayList of QRCode objects to store the retrieved QR codes.
     @param qrArrayAdapter the QRArrayAdapter to be updated with the retrieved QR codes.
     */
    private void getAllQRCode(ArrayList<QRCode> otherQRCodeList, QRArrayAdapter qrArrayAdapter) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_qr_codes, container, false);
    }
}