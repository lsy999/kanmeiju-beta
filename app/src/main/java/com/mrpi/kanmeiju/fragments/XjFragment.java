package com.mrpi.kanmeiju.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrpi.kanmeiju.MainActivity;
import com.mrpi.kanmeiju.R;
import com.mrpi.kanmeiju.bean.Type;

/**
 * Created by mrpi on 2016/7/4.
 */
public class XjFragment extends BaseFragment {

    @Override
    protected void lazyLoad() {
        init(Type.TYPE_XJ_CODE);
    }

    private static XjFragment XjhFragment;
    public static XjFragment newInstance(){
        if(XjhFragment == null){
            XjhFragment = new XjFragment();
        }
        return XjhFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        initView();
        return mView;
    }
}
