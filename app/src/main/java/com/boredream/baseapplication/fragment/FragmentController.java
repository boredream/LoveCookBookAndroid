package com.boredream.baseapplication.fragment;


import android.widget.RadioGroup;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.boredream.baseapplication.base.BaseActivity;
import com.boredream.baseapplication.base.BaseFragment;

import java.util.ArrayList;

/**
 * fragment切换控制器, 初始化时直接add全部fragment, 然后利用show和hide进行切换控制
 */
public class FragmentController {

    private RadioGroup rg;
    private int containerId;
    private FragmentManager fm;
    private ArrayList<BaseFragment> fragments;

    public FragmentController(BaseActivity activity, RadioGroup rg, int containerId, ArrayList<BaseFragment> fragments) {
        this.rg = rg;
        this.containerId = containerId;
        this.fragments = fragments;
        this.fm = activity.getSupportFragmentManager();
        initFragment();
    }

    public void initFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        for (int i = 0; i < fragments.size(); i++) {
            BaseFragment fragment = fragments.get(i);
            ft.add(containerId, fragment, String.valueOf(i));
            ft.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
        }
        ft.commitAllowingStateLoss();

        rg.setOnCheckedChangeListener((group, checkedId) ->
                showFragment(rg.indexOfChild(rg.findViewById(checkedId))));
    }

    public void showFragment(int position) {
        hideFragments();
        BaseFragment fragment = fragments.get(position);
        FragmentTransaction ft = fm.beginTransaction();
        ft.setMaxLifecycle(fragment, Lifecycle.State.RESUMED);
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }

    public void hideFragments() {
        FragmentTransaction ft = fm.beginTransaction();
        for (BaseFragment fragment : fragments) {
            if (fragment != null) {
                ft.hide(fragment);
            }
        }
        ft.commitAllowingStateLoss();
    }

    public BaseFragment getFragment(int position) {
        return fragments.get(position);
    }

}