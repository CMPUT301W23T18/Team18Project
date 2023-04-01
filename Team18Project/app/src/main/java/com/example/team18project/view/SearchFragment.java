package com.example.team18project.view;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.team18project.R;
import com.example.team18project.controller.SearchController;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    public interface UserListCallback {
        void onUserListGenerated(ArrayList<String> usernameFilteredUserList);
    }
    private FirebaseFirestore db;
    private SearchController controller;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.controller = new SearchController();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        EditText usernameSearchEditText = view.findViewById(R.id.username_search);
        Button searchButton = view.findViewById(R.id.search_button);
        ListView userListView = view.findViewById(R.id.user_list);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = usernameSearchEditText.getText().toString();
                controller.generateUserList(searchText,new UserListCallback() {
                    @Override
                    public void onUserListGenerated(ArrayList<String> usernameFilteredUserList) {
                        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, usernameFilteredUserList);
                        userListView.setAdapter(adapter);
                    }
                });
            }
        });

        return view;
    }
}