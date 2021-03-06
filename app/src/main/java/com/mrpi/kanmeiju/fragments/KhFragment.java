package com.mrpi.kanmeiju.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrpi.kanmeiju.MainActivity;
import com.mrpi.kanmeiju.R;

/**
 * Created by mrpi on 2016/7/4.
 */
public class KhFragment extends BaseFragment{

    @Override
    protected void lazyLoad() {
        init(this.type,mActivity);
    }

    public static Context mActivity;
    public int type = 1;

    public KhFragment() {

    }

    public static KhFragment newInstance(Context activity,int type) {
        KhFragment fragment = new KhFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        mActivity = activity;
        fragment.type = type;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        initView();
        return mView;
    }
}
