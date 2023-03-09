package com.example.team18project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "player";

    // TODO: Rename and change types of parameters
    private Player player;
    private ArrayList<QRCode> qrData;
    private ListView qrList;
    private QRArrayAdapter qrAdapter;

    /**
     * Required empty public constructor
     */
    public HomeFragment() {
        // Required empty public constructor
    }

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
        System.out.println(player == null);
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
        return inflater.inflate(R.layout.fragment_home, container, false);
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
}