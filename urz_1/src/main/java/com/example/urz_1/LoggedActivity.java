package com.example.urz_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.urz_1.fragment.BaseFragment;
import com.example.urz_1.fragment.FindFragment;
import com.example.urz_1.fragment.HomeFragment;
import com.example.urz_1.fragment.MineFragment;
import com.example.urz_1.fragment.NewsFragment;

public class LoggedActivity extends AppCompatActivity implements View.OnClickListener {
    //底部的四个导航图片，主页、发现、消息、我的
    private ImageView ivHome, ivFind, ivNews, ivMine;
    private LinearLayout llHome, llFind, llNews, llMine;
    private HomeFragment tabHome;
    private FindFragment tabFind;
    private NewsFragment tabNews;
    private MineFragment tabMine;

    private static String currentUsername;
    private Intent intent;
    private Bundle bundle;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BaseFragment baseFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.logged);
        initView();

        //获取从 MainActivity 登录界面传递过来的username
        intent = getIntent();

        //把username传递给HomeFragment，并把HomeFragment设置默认为首页
        currentUsername = intent.getStringExtra("username");
        HomeFragment homeFragment = HomeFragment.getInstance(currentUsername);
        changeFragment(homeFragment);
        ivHome.setImageResource(R.mipmap.home1);
        //baseFragment = BaseFragment.getInstance(R.id.flLogged);

        //把username传递给MineFragment
        MineFragment mineFragment = MineFragment.getInstance(currentUsername);
        setFragment(mineFragment);

        //把username传递给FindFragment
        FindFragment findFragment = FindFragment.getInstance(currentUsername);
        setFragment(findFragment);


        //四个导航的监听事件
        llHome.setOnClickListener(this);
        llFind.setOnClickListener(this);
        llNews.setOnClickListener(this);
        llMine.setOnClickListener(this);

    }

    void initView() {
        ivHome = findViewById(R.id.ivHome);
        ivFind = findViewById(R.id.ivFind);
        ivNews = findViewById(R.id.ivNews);
        ivMine = findViewById(R.id.ivMine);
        llHome = findViewById(R.id.llHome);
        llFind = findViewById(R.id.llFind);
        llNews = findViewById(R.id.llNews);
        llMine = findViewById(R.id.llMine);
    }


    @Override
    public void onClick(View v) {
        initImageView();
        switch (v.getId()) {
            case R.id.llHome:
                ivHome.setImageResource(R.mipmap.home1);
                tabHome = HomeFragment.getInstance(currentUsername);
                changeFragment(tabHome);
                break;
            case R.id.llFind:
                ivFind.setImageResource(R.mipmap.find1);
                tabFind = FindFragment.getInstance(currentUsername);
                changeFragment(tabFind);
                break;
            case R.id.llNews:
                ivNews.setImageResource(R.mipmap.news1);
                tabNews = NewsFragment.getInstance(currentUsername);
                changeFragment(tabNews);
                break;
            case R.id.llMine:
                ivMine.setImageResource(R.mipmap.mine1);
                tabMine = MineFragment.getInstance(currentUsername);
                changeFragment(tabMine);
                break;
        }
    }

    void changeFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        //replace方法的第二个参数不能使用new 类名（）；这样数据是不能传递的。
        fragmentTransaction.replace(R.id.flLogged, fragment);
        fragmentTransaction.commit();
    }

    void setFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.commit();
    }

    void initImageView() {
        ivHome.setImageResource(R.mipmap.home0);
        ivFind.setImageResource(R.mipmap.find0);
        ivNews.setImageResource(R.mipmap.news0);
        ivMine.setImageResource(R.mipmap.mine0);
    }
}
