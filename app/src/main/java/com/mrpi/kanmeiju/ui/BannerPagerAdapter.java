package com.mrpi.kanmeiju.ui;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class BannerPagerAdapter extends PagerAdapter {

    private List<TagImageView> list;
    public BannerPagerAdapter(List<TagImageView> list){
        this.list = list;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView(list.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ((ViewPager)container).addView(list.get(position));
        list.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemSelected(position);
            }
        });
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public interface OnItemSelectListener {
        public void onItemSelected(int position);
    }
    private OnItemSelectListener listener;
    public void setOnItemSelectListener(OnItemSelectListener listener){
        this.listener = listener;
    }
}