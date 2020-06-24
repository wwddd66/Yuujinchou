package com.example.urz_1.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.urz_1.R;
import com.example.urz_1.adapter.UserAdapter;
import com.example.urz_1.model.User;
import com.example.urz_1.model.UserRelation;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {
    private String currentUsername;
    private User currentUser;
    private static String key_username = "username";

    private List<User> userList = new ArrayList<>();
    private RecyclerView rvUserList;
    private Intent intent;


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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();

        //获取从 LoggedActivity 传递的username
        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.d("error", "FindBundle为空");
        } else {
            currentUsername = bundle.getString(key_username, "username");
            currentUser = LitePal.where("username like ?", currentUsername).findFirst(User.class, true);
        }

        showUserList(currentUser);
    }

    private void initView() {
        rvUserList = getActivity().findViewById(R.id.rvUserList);
    }

    private void showUserList(User currentUser) {
        List<UserRelation> relations = LitePal.where("userid like ?", String.valueOf(currentUser.getId())).find(UserRelation.class);
        userList = new ArrayList<>();
        for (UserRelation item : relations) {
            userList.add(LitePal.where("id like ?", String.valueOf(item.getFriendId())).findFirst(User.class, true));
        }
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvUserList.setLayoutManager(layoutManager);
        UserAdapter userAdapter = new UserAdapter(userList, currentUsername);
        rvUserList.setAdapter(userAdapter);
    }
}
