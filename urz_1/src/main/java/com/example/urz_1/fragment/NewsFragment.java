package com.example.urz_1.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.urz_1.R;
import com.example.urz_1.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {
    private String currentUsername;
    private User currentUser;
    private static String key_username = "username";


    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment getInstance(String username) {
        Bundle bundle = new Bundle();
        bundle.putString(key_username, username);
        NewsFragment newsFragment = new NewsFragment();
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news, container, false);
    }

}
