package com.example.team18project.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.team18project.controller.AllQRCodesController;
import com.example.team18project.model.Player;
import com.example.team18project.model.QRArrayAdapter;
import com.example.team18project.R;
import com.example.team18project.model.Comment;
import com.example.team18project.model.QRCode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private static final String ARG_PARAM1 = "player";

    private AllQRCodesController controller;
    private ArrayList<QRCode> otherQRCodeList;
    private ListView qrListView;
    private QRArrayAdapter qrAdapter;
    private Player player;

    /**
     * Create a new instance of the AllQRCodesFragment and bundle any passed parameters
     * @return the instance of our new fragment
     */
    public static AllQRCodesFragment newInstance(Player player) {
        AllQRCodesFragment fragment = new AllQRCodesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1,player);
        fragment.setArguments(args);
        fragment.controller = new AllQRCodesController();
        return fragment;
    }

    /**
     * Unbundled any parameters passed and add them to the native variables of the Fragment
     * @param savedInstanceState A bundle of all passed parameters to the AllQRCodesFragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            player = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        otherQRCodeList = new ArrayList<QRCode>();
        qrListView = (ListView) getView().findViewById(R.id.other_qrcode_list);
        qrAdapter = new QRArrayAdapter(getContext(), otherQRCodeList);
        qrListView.setAdapter(qrAdapter);
        qrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QRCode clicked = (QRCode) qrListView.getItemAtPosition(position);
                Log.d("Michael",Boolean.toString(clicked == null));
                ((MainActivity) getActivity()).replaceFragment(QRViewFragment.newInstance(player,clicked));
            }
        });

        FloatingActionButton mapbutton = getView().findViewById(R.id.view_map_button);
        mapbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(MapFragment.newInstance());
            }
        });

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
                controller.updateQRCodes(otherQRCodeList,queryDocumentSnapshots);
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