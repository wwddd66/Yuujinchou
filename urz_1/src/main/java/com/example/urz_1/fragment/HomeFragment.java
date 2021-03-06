package com.example.urz_1.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.urz_1.model.UserRelation;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    //private static Bitmap bitmap;

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

    //传递头像
/*    static void setDate(Bitmap b) {
        bitmap = b;
    }*/

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

        currentUser = LitePal.where("username like ?", currentUsername).findFirst(User.class);
        List<UserRelation> relations = LitePal.where("userid like ?", String.valueOf(currentUser.getId())).find(UserRelation.class);
        //动态列表
        for (UserRelation item : relations) {
            postList.addAll(LitePal.where("user_id like ?", String.valueOf(item.getFriendId())).find(Post.class, true));
        }
        //实现Comparable接口，对所有动态进行按时间降序排列
        Collections.sort(postList, new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
        PostAdapter postAdapter = new PostAdapter(getContext(), postList, currentUsername);
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


    private void initView() {
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
                    postList = new ArrayList<>();

                    List<UserRelation> userRelations = LitePal.where("userid like ?", String.valueOf(currentUser.getId())).find(UserRelation.class);

                    //好友名单（好友的用户名）
                    //动态列表
                    for (UserRelation relation : userRelations) {
                        postList.addAll(LitePal.where("user_id like ?", String.valueOf(relation.getFriendId())).find(Post.class, true));
                    }

                    Collections.sort(postList, new Comparator<Post>() {
                        @Override
                        public int compare(Post o1, Post o2) {
                            return o2.getDate().compareTo(o1.getDate());
                        }
                    });

                    PostAdapter postAdapter = new PostAdapter(getContext(), postList, currentUsername);
                    rvPosts.setAdapter(postAdapter);
                    rvPosts.setLayoutManager(layoutManager);
                }
                break;
        }

    }

}
