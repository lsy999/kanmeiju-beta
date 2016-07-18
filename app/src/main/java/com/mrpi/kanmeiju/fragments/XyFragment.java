package com.mrpi.kanmeiju.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrpi.kanmeiju.R;

/**
 * Created by mrpi on 2016/7/4.
 */
public class XyFragment extends BaseFragment {
    @Override
    protected void lazyLoad() {

    }

    public Context mActivity;
    public int type = 1;

    public XyFragment() {

    }

    public static XyFragment newInstance(Context activity,int type) {
        XyFragment fragment = new XyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mActivity = activity;
        fragment.type = type;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        initView();
        init(this.type,this.mActivity);
        return mView;
    }
}
