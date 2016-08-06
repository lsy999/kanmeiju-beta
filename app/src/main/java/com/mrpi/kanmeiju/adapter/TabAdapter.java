package com.mrpi.kanmeiju.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;


import java.util.List;

public class TabAdapter extends FragmentPagerAdapter{

    private List<String> mTitle;
    private List<Fragment> mFragments;

    public TabAdapter(FragmentManager fm, List<Fragment> fragmentList,List<String> listTitle) {
        super(fm);
        this.mFragments = fragmentList;
        this.mTitle = listTitle;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container,position);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position%mFragments.size());
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle.get(position);
    }
}