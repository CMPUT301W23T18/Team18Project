package com.example.team18project;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A fragment used to represent the home screen, where QR codes are displayed and added
 */
public class HomeFragment extends Fragment {

    private static int request_Code = 3;
    public static int RESULT_OK = 3;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "player";

    // TODO: Rename and change types of parameters
    private Player player;
    private ArrayList<QRCode> qrData;
    private ListView qrList;
    private QRArrayAdapter qrAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player The logged in player
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(Player player) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1,player);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            player = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, null);
        FloatingActionButton scanCode = view.findViewById(R.id.add_qr_button);
        scanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanQRCode.class);
                Log.d("testing", "values in home fragment");
                Log.d("testing", player.toString());
                Log.d("testing", player.getUid());
                Log.d("testing", player.getCodes().toString());
                Log.d("testing", player.getEmail());
                Log.d("testing", player.getUsername());
                if (player != null) {
                    intent.putExtra("player", (Serializable) player);
                    Log.d("testing", "data passed successfully");
                }

                startActivityForResult(intent, 1);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        qrData = player.getCodes();
        //make ListView
        qrList = (ListView) getView().findViewById(R.id.qr_list);
        qrAdapter = new QRArrayAdapter(getContext(), qrData);
        qrList.setAdapter(qrAdapter);

        qrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QRCode clicked = (QRCode) qrList.getItemAtPosition(position);
                QRMenuFragment.newInstance(clicked).show(getParentFragmentManager(),"Menu");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == request_Code) {
            if (resultCode == RESULT_OK) {
                QRCode newCode = (QRCode) data.getParcelableExtra("newCode");
                qrAdapter.add(newCode);
            }
        }
    }

}