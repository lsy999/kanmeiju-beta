package com.mrpi.kanmeiju.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrpi.kanmeiju.MainActivity;
import com.mrpi.kanmeiju.R;


public class MainFragment extends BaseFragment {


    public static Context mActivity;
    public int type = 1;

    public MainFragment() {

    }

    public static MainFragment newInstance(Context activity,int type) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        mActivity = activity;
        fragment.type = type;
        return fragment;
    }

    @Override
    protected void lazyLoad() {
        init(this.type,mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        initView();
        return mView;
    }

}
