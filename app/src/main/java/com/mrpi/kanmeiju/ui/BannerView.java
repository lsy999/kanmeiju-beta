package com.mrpi.kanmeiju.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mrpi.kanmeiju.R;
import com.orhanobut.logger.Logger;

public class BannerView extends FrameLayout {

    private String TAG = "BANNERVIEW";
    private static int IMAGE_COUNT;
    private static int TIME_INTERVAL = 3;
    private final static boolean isAutoPlay = true;
    private Context mContext;
    private int size;
    private List<TagImageView> mImageViews;
    private ViewPager mPager;
    private int mCurrentItem = 0;
    private ScheduledExecutorService scheduledExecutorService ;
    private LinearLayout mDotView;
    private BannerPagerAdapter mAdapter;
    private SlideShowTask slideShowTask = new SlideShowTask();

    public BannerView(Context context) {
        this(context,null);
        applyStyle(context,null,0,0);
    }
    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        applyStyle(context,attrs,0,0);
    }
    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyStyle(context,attrs,defStyle,0);
    }

    protected void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_banner,null);
        addView(view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.BannerView,defStyleAttr,defStyleRes);
        TIME_INTERVAL = array.getInt(R.styleable.BannerView_time,3);
        mContext = context;
        mImageViews = new ArrayList<>();
        mDotView = (LinearLayout) view.findViewById(R.id.dot_layout);
    }

    /**
     * startPlay
     */
    private void startPlay(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(),1,TIME_INTERVAL,TimeUnit.SECONDS);
    }

    /**
     * stopPlay
     */
    public void stopPlay(){
        if(scheduledExecutorService != null){
            scheduledExecutorService.shutdown();
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                mPager.setCurrentItem(mCurrentItem);
            }catch (Exception ex){
                Log.e(TAG,ex.getMessage());
            }
        }
    };

    private  class SlideShowTask implements Runnable{
        @Override
        public void run() {
            synchronized (mPager) {
                mCurrentItem = (mCurrentItem + 1) % mImageViews.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }

    public void reFresh(int size,setTagImageView setTagImageView){
        this.size = size;
        setTagImageView(size,setTagImageView);
        mPager.getAdapter().notifyDataSetChanged();
    }

    public void removeAt(int index){
        mPager.removeViewAt(index);
        mPager.getAdapter().notifyDataSetChanged();
    }

    public void removeAll(){
        mPager.removeAllViews();
        mPager.getAdapter().notifyDataSetChanged();
    }

    public TagImageView getTagImageView(int position){
        return mImageViews.get(position);
    }
    public setTagImageView setTagImageView;

    private void initView(Context context,setTagImageView setTagImageView){
        mDotView.removeAllViews();
        for(int i = 0; i<size; i++){
            TagImageView tagImageView =  new TagImageView(context);
            setTagImageView.setImageView(tagImageView,i);
            mImageViews.add(tagImageView);
            if(i<size-1) {
                DotView dot = new DotView(context);
                mDotView.addView(dot);
            }
        }
        mPager = (ViewPager) findViewById(R.id.viewPager);
        mPager.setFocusable(true);
        mAdapter = new BannerPagerAdapter(mImageViews);
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(new BannerPageChangeListener());
        if(mPager.getAdapter()!=null){
            mPager.getAdapter().notifyDataSetChanged();
        }

    }

    public interface setTagImageView {
        public void setImageView(TagImageView tagImageView,int position);
    }

    public void setTagImageView(int size,setTagImageView setTagImageView){
        this.size = size;
        IMAGE_COUNT = size;
        initView(mContext,setTagImageView);
        mPager.getAdapter().notifyDataSetChanged();
        if(isAutoPlay){
            startPlay();
        }
        ((DotView)mDotView.getChildAt(mCurrentItem)).setDotBackground(R.drawable.dot_black);
    }



    public BannerPagerAdapter getmAdapter(){
        return (BannerPagerAdapter) mPager.getAdapter();
    }

    private class BannerPageChangeListener implements OnPageChangeListener{

        boolean isAutoPlay = false;
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if(position==size-1){
                mPager.setCurrentItem(0, false);
            }
        }

        @Override
        public void onPageSelected(int pos) {
            mCurrentItem = pos;
            for(int i=0;i < mDotView.getChildCount();i++){
                if(i == pos){
                    ((DotView)mDotView.getChildAt(i)).setDotBackground(R.drawable.dot_black);
                }else {
                    ((DotView)mDotView.getChildAt(i)).setDotBackground(R.drawable.dot_white);
                }
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case 1:
                    isAutoPlay = false;
                    break;
                case 2:
                    isAutoPlay = true;
                    break;
                case 0:
                    if (mPager.getCurrentItem() == 0 && !isAutoPlay) {
                        mPager.setCurrentItem(mPager.getAdapter().getCount() - 1);
                    }
                    if (mPager.getCurrentItem() == mPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        mPager.setCurrentItem(0);
                    }
                    break;
            }
        }

    }

    @Override
    public void destroyDrawingCache() {
        super.destroyDrawingCache();
    }

}