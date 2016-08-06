package com.mrpi.kanmeiju.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mrpi.kanmeiju.AppController;
import com.mrpi.kanmeiju.Appconfig;
import com.mrpi.kanmeiju.R;
import com.mrpi.kanmeiju.adapter.MainAdapter;
import com.mrpi.kanmeiju.bean.Video;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    private String mKey;
    public RecyclerView mRecycler;
    public SwipeRefreshLayout mSwipe;
    public GridLayoutManager mStaggered;
    public MainAdapter mAdapter;
    public List<Video.Meiju> mList = new ArrayList<>();
    public int page;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void initView() {
        initToolbar();
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mRecycler = (RecyclerView) findViewById(R.id.list_item);
        assert mRecycler != null;
        mRecycler.setHasFixedSize(false);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mStaggered = new GridLayoutManager(mBaseActivity,2);
        mRecycler.  setLayoutManager(mStaggered);
        mAdapter = new MainAdapter(mList,getApplicationContext());
        mRecycler.setAdapter(mAdapter);
        mSwipe.post(new Runnable() {
            @Override
            public void run() {
                mSwipe.setRefreshing(true);
            }
        });
        Intent intent = getIntent();
        mKey = intent.getStringExtra("key");
        load(mKey,1);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        load(mKey,page);
                        mSwipe.setRefreshing(false);
                    }
                },1000);
            }
        });
        mRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(mAdapter!=null&&count>=10){
                    try {
                        int lastItem = mStaggered.findLastVisibleItemPosition();
                        if(newState == RecyclerView.SCROLL_STATE_IDLE&&lastItem+1==mAdapter.getItemCount()){
                            mSwipe.setRefreshing(true);
                            load(mKey,++page);
                        }
                    }catch (Exception ex){
                        // Toast.makeText(mBaseActivity, "error", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Snackbar.make(recyclerView,"没有更多了",Snackbar.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showSearch(false);
        Bundle extras = intent.getExtras();
        String userQuery = String.valueOf(extras.get(SearchManager.USER_QUERY));
        String query = String.valueOf(extras.get(SearchManager.QUERY));
        suggestions.saveRecentQuery(userQuery,"");
        page = 1;
        Logger.d(query);
        mKey = query;
        mSwipe.setRefreshing(true);
        mList.clear();
        load(mKey,page);
    }

    private void load(String key, int page){
        String url = Appconfig.URL_SH+"key="+key+"&page="+page;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
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
                    if(video.resultContent.size()==0){
                        Toast.makeText(SearchActivity.this, "没有结果", Toast.LENGTH_SHORT).show();
                    }
                    mList.addAll(video.resultContent);
                }
                count = video.resultContent.size();
                mAdapter.notifyDataSetChanged();
                mSwipe.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipe.setRefreshing(false);
                Toast.makeText(SearchActivity.this, "没有结果", Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

}
