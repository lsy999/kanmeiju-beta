package com.mrpi.kanmeiju.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mrpi.kanmeiju.R;
import com.mrpi.kanmeiju.adapter.MainAdapter;
import com.mrpi.kanmeiju.bean.Events;
import com.mrpi.kanmeiju.bean.Video;
import com.mrpi.kanmeiju.utils.LoadListener;
import com.mrpi.kanmeiju.utils.LoadVideo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mrpi on 2016/3/19.
 *
 */
public abstract class BaseFragment extends Fragment{

    protected boolean isVisible;
    public boolean isLoaded = true;
    public View mView;
    public RecyclerView mRecy;
    public MainAdapter mAdapter;
    public GridLayoutManager mGrid;
    public List<Video.Meiju> mList = new ArrayList<>();
    public SwipeRefreshLayout mSwipe;
    public int page = 1;
    private int type = 1;
    public static Context mActivity;

    public BaseFragment(){

    }

    public void init(int type,Context mActivity){
        this.type = type;
        page = 1;
        load(page,type);
        BaseFragment.mActivity = mActivity;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events e){
        switch (e.getCode()){
            case Events.VIDEO_FOLLOW:
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 可见
     */
    protected void onVisible() {
        lazyLoad();
    }
    /**
     * 不可见
     */
    protected void onInvisible() {

    }
    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected abstract void lazyLoad();


    protected View findViewById(int id){
        if(getView() == null) return mView.findViewById(id);
        return  getView().findViewById(id);
    }

    protected void initView(){

        mRecy = (RecyclerView) findViewById(R.id.recyclerView);
        mGrid = new GridLayoutManager(mActivity,2);
        mRecy.setLayoutManager(mGrid);
        mRecy.setHasFixedSize(true);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swiper_refresh);
        mAdapter = new MainAdapter(mList);
        mRecy.setAdapter(mAdapter);
        mSwipe.post(new Runnable() {
            @Override
            public void run() {
                mSwipe.setRefreshing(true);
            }
        });

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipe.setRefreshing(true);
                        page = 1;
                        load(page,type);
                    }
                },1000);
            }
        });

        mRecy.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(mAdapter!=null&&mAdapter.getItemCount()>=10){
                    try {
                        int lastItem = mGrid.findLastVisibleItemPosition();
                        if(newState == RecyclerView.SCROLL_STATE_IDLE&&lastItem+1==mAdapter.getItemCount()){
                            mSwipe.setRefreshing(true);
                            load(++page,type);
                        }
                    }catch (Exception ex){
                        Toast.makeText(mActivity, "请求错误", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    public void load(final int page, final int type){
        LoadVideo.getInstance().load("page="+page+"&type="+type, new LoadListener() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                Video video = new Video();
                video = gson.fromJson(response, Video.class);
                if(mList.size()>0) {
                    if (video.resultContent.size() > 0 && !video.resultContent.get(0).getInfoUrl().equals(mList.get(0).getInfoUrl())) {
                        mList.addAll(video.resultContent);
                    }
                }else {
                    mList.addAll(video.resultContent);
                }
                if(mAdapter!=null){
                    mAdapter.notifyDataSetChanged();
                    mSwipe.setRefreshing(false);
                }

            }

            @Override
            public void onFail(String msg) {
                load(page,type);
            }

        });
    }
}
