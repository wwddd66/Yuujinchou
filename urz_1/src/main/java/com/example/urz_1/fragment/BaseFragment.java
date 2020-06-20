package com.example.urz_1.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.urz_1.R;

public class BaseFragment extends Fragment {
    private static FindFragment findFragment = new FindFragment();
    private static HomeFragment homeFragment = HomeFragment.getInstance("sure");
    private static MineFragment mineFragment = new MineFragment();
    private static NewsFragment newsFragment = new NewsFragment();
    private FragmentTransaction fragmentTransaction;
    private static BaseFragment baseFragment = new BaseFragment();

    public static BaseFragment getInstance(int mainid) {
        baseFragment.addFragment(mainid);
        return baseFragment;

    }

    public static FindFragment getFindFragment() {
        return findFragment;
    }

    public static HomeFragment getHomeFragment() {
        return homeFragment;
    }

    public static MineFragment getMineFragment() {
        return mineFragment;
    }

    public static NewsFragment getNewsFragment() {
        return newsFragment;
    }


    private FragmentTransaction addFragment(int id) {
        fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(id, getFindFragment());
        fragmentTransaction.add(id, getHomeFragment());
        fragmentTransaction.add(id, getMineFragment());
        fragmentTransaction.add(id, getNewsFragment());

        return fragmentTransaction;
    }

    public void showFragment(int a) {
        Fragment[] fragment = {getHomeFragment(), getMineFragment(), getNewsFragment(), getFindFragment()};
        fragmentTransaction.show(fragment[a]).commit();
        for (int i = 0; i < 4; i++) {
            if (i == a) {
            } else {
                fragmentTransaction.hide(fragment[i]).commit();
            }
        }
    }
}
