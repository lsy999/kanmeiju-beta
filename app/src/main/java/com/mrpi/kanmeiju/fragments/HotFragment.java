package com.mrpi.kanmeiju.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mrpi.kanmeiju.AppController;
import com.mrpi.kanmeiju.Appconfig;
import com.mrpi.kanmeiju.R;
import com.mrpi.kanmeiju.activity.DetailActivity;
import com.mrpi.kanmeiju.adapter.HotAdapter;
import com.mrpi.kanmeiju.bean.Events;
import com.mrpi.kanmeiju.bean.Video;
import com.mrpi.kanmeiju.dao.Meiju;
import com.mrpi.kanmeiju.dao.MeijuDao;
import com.mrpi.kanmeiju.ui.BannerPagerAdapter;
import com.mrpi.kanmeiju.ui.BannerView;
import com.mrpi.kanmeiju.ui.SpaceItemDecoration;
import com.mrpi.kanmeiju.ui.TagImageView;
import com.mrpi.kanmeiju.ui.TypeView;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.Query;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by acer on 2016/7/15.
 */
public class HotFragment extends Fragment implements BannerView.setTagImageView{


    private BannerView mBanner;
    protected boolean isVisible;
    public boolean isLoaded = true;
    public View mView;
    public RecyclerView mRecy;
    public HotAdapter mAdapter;
    public GridLayoutManager mGrid;
    public List<Video.Meiju> mList = new ArrayList<>();
    private SwipeRefreshLayout mSwipe;

    public HotFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_hot, container, false);
        initView();
        showLoading();
        return mView;
    }


    MaterialDialog dialog;
    public void showLoading(){
        dialog = new MaterialDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_loading,null);
        dialog.setContentView(view);
        dialog.show();
    }
    public void dismisDialog(){
        dialog.dismiss();
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

    public List<Meiju> query(){
        Query query = AppController.getDao().queryBuilder()
                .orderAsc(MeijuDao.Properties.Click)
                .limit(8)
                .build();
        return ((List<Meiju>) query.list());
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
            case Events.BANNER_SHOW:
                mList.clear();
                loadHot();
                break;
            case Events.BANNER_HIDE:
                mAdapter.setHeaderView(null);
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
    protected  void lazyLoad(){
        loadHot();
    }

    protected View findViewById(int id){
        if(getView() == null) return mView.findViewById(id);
        return  getView().findViewById(id);
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void initView(){
        mRecy = (RecyclerView) findViewById(R.id.recyclerView);
        mGrid = new GridLayoutManager(getActivity(),2);
        mRecy.setLayoutManager(mGrid);
        mRecy.setFocusable(false);
        mAdapter = new HotAdapter(mList);
        mRecy.setAdapter(mAdapter);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swiper_refresh);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipe.setRefreshing(true);
                        loadHot();
                    }
                },1000);
            }
        });
    }

    public void loadHot(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Appconfig.URL_HOT, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                Video video = new Video();
                video = gson.fromJson(response.toString(), Video.class);
                if(mList.size()>0) {
                    if (video.resultContent.size() > 0 && !video.resultContent.get(0).getInfoUrl().equals(mList.get(0).getInfoUrl())) {
                        mList.addAll(video.resultContent);
                    }
                }else {
                    mList.addAll(video.resultContent);
                }
                mAdapter.notifyDataSetChanged();
                if(query().size()>3&&mAdapter.getHeaderView()==null){
                    setHeader(mRecy);
                }
                mSwipe.setRefreshing(false);
                dismisDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipe.setRefreshing(false);
                dismisDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
    private void setHeader(RecyclerView view) {
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.item_header, view, false);
        initBanner(header);
        mAdapter.setHeaderView(header);
    }
    private void initBanner(View view){
        mBanner = (BannerView) view.findViewById(R.id.banner);
        mBanner.setVisibility(View.GONE);
        if(query().size()>3){
            mBanner.setTagImageView(query().size(),this);
            mBanner.setVisibility(View.VISIBLE);
        }
        mBanner.getmAdapter().setOnItemSelectListener(new BannerPagerAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelected(int position) {
                Intent intent = new Intent(AppController.getActivity(), DetailActivity.class);
                intent.putExtra("url",query().get(position).getVideoId());
                intent.putExtra("picUrl", query().get(position).getPicture());
                intent.putExtra("title",query().get(position).getName());
                intent.putExtra("position",position);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setImageView(TagImageView tagImageView, int position) {
        Meiju meiju = (Meiju)query().get(position);
        if(meiju.getLocal().length()>0){
            tagImageView.setImage(meiju.getLocal());
        }else {
            tagImageView.setImage(meiju.getPicture());
        }
        tagImageView.setState(meiju.getHasUpdate());
        if(meiju.getHasUpdate()){
            tagImageView.setTag("更新了");
        }else {
            tagImageView.setTag("没有更新");
        }
    }

}
