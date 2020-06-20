package com.example.urz_1.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.urz_1.AddPostActivity;
import com.example.urz_1.R;
import com.example.urz_1.adapter.PostAdapter;
import com.example.urz_1.model.Post;
import com.example.urz_1.model.User;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private ImageView ivBack, ivAddPost;
    private RecyclerView rvPosts;
    private String currentUsername;
    private Intent intent;
    private List<Post> postList = new ArrayList<>();
    private StaggeredGridLayoutManager layoutManager;
    private static String key_username = "username";

    private User currentUser;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment getInstance(String username) {
        Bundle bundle = new Bundle();
        bundle.putString(key_username, username);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

        //获取从 LoggedActivity 传递的username
        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.d("error", "HomeBundle为空");
        } else {
            currentUsername = bundle.getString(key_username, "username");
        }

        currentUser = LitePal.where("username like ?", currentUsername).find(User.class).get(0);
        postList.addAll(LitePal.where("user_id like ?", String.valueOf(currentUser.getId())).order("date desc").find(Post.class, true));
        //动态列表（目前只有自己的）
        for (User user : currentUser.getUserList()) {
            postList.addAll(LitePal.where("user_id like ?", String.valueOf(user.getId())).order("date desc").find(Post.class, true));
        }
        PostAdapter postAdapter = new PostAdapter(postList, currentUsername);
        rvPosts.setAdapter(postAdapter);
        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvPosts.setLayoutManager(layoutManager);

        //返回按钮（已取消）
        /*ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getView().getContext(), MainActivity.class);
                startActivity(intent);
            }
        });*/

        //添加动态按钮
        ivAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getView().getContext(), AddPostActivity.class);
                startActivityForResult(intent, 3);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }


    void initView() {
        ivBack = getActivity().findViewById(R.id.ivBack);
        ivAddPost = getActivity().findViewById(R.id.ivAddPost);
        rvPosts = getActivity().findViewById(R.id.rvPosts);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 3:
                if (resultCode == 30) {
                    String postContent = data.getStringExtra("postContent");
                    String dateString = data.getStringExtra("dateString");
                    //把post加到数据库
                    Post post = new Post(postContent, dateString, 0, 0, currentUser);
                    post.save();

                    //在ListView中展示出来
                    rvPosts.setLayoutManager(layoutManager);


                    //好友名单（好友的用户名）（...）
                    for (User item : currentUser.getUserList()) {
                        postList.addAll(LitePal.where("user_id like ?", String.valueOf(item.getId())).order("date desc").find(Post.class, true));
                    }
                    PostAdapter postAdapter = new PostAdapter(postList, currentUsername);
                    rvPosts.setAdapter(postAdapter);
                }
                break;
        }

    }

}
