package com.example.urz_1.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.urz_1.R;
import com.example.urz_1.adapter.UserAdapter;
import com.example.urz_1.model.User;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFragment extends Fragment {
    private List<User> userList = new ArrayList<>();
    private Button btnFind;
    private RecyclerView rvUser;
    private EditText edtIndex;
    private static String key_username = "username";
    private String currentUsername;
    private Intent intent;
    private User user;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    //设置瀑布流布局
                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                    rvUser.setLayoutManager(layoutManager);
                    UserAdapter userAdapter = new UserAdapter(userList, currentUsername);
                    rvUser.setAdapter(userAdapter);
                    break;
            }
        }
    };


    public FindFragment() {
        // Required empty public constructor
    }

    public static FindFragment getInstance(String username) {
        Bundle bundle = new Bundle();
        bundle.putString(key_username, username);
        FindFragment findFragment = new FindFragment();
        findFragment.setArguments(bundle);
        return findFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.find, container, false);
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
        }

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String index = edtIndex.getText().toString();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        userList = (List<User>) LitePal.select("username", "nickname")
                                .where("username like ? or nickname like ?", "%" + index + "%", "%" + index + "%")
                                .find(User.class, true);
                        handler.sendEmptyMessage(1000);
                    }
                }.start();

            }
        });

    }

    void initView() {
        btnFind = getActivity().findViewById(R.id.btnFind);
        edtIndex = getActivity().findViewById(R.id.edtIndex);
        rvUser = getActivity().findViewById(R.id.rvUser);
    }

}
