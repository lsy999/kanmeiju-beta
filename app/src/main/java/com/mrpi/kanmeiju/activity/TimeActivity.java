package com.mrpi.kanmeiju.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mrpi.kanmeiju.AppController;
import com.mrpi.kanmeiju.Appconfig;
import com.mrpi.kanmeiju.R;
import com.mrpi.kanmeiju.adapter.TimeAdapter;
import com.mrpi.kanmeiju.bean.Calendar;
import com.orhanobut.logger.Logger;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;

public class TimeActivity extends BaseActivity {


    public FastScrollRecyclerView mRecy;
    public TimeAdapter mAdapter;
    public GridLayoutManager mGrid;
    public List<Calendar.resultContent> mList = new ArrayList<>();
    public SwipeRefreshLayout mSwipe;
    public List<Calendar.resultContent> instance = new ArrayList<>();
    private TextView mTimeView;
    private FrameLayout mFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
    }

    @Override
    protected void initView() {
        initToolbar();
        mRecy = (FastScrollRecyclerView) findViewById(R.id.recyclerView);
        mGrid = new GridLayoutManager(this,1);
        mRecy.setLayoutManager(mGrid);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swiper_refresh);
        mAdapter = new TimeAdapter(mList,this);
        mRecy.setAdapter(mAdapter);
        mTimeView = (TextView) findViewById(R.id.text_time);
        mFrame = (FrameLayout) findViewById(R.id.timeParent);
        showLoading();
        update();
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
        final ShowBoxTime showBoxTime = new ShowBoxTime(3000,1000);
        mRecy.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(mAdapter.getItemCount()>0) {
                    final int fistItem = mGrid.findFirstCompletelyVisibleItemPosition();
                    if(mAdapter.getItem(fistItem).getTime()!=null) {
                        mTimeView.setText(mAdapter.getItem(fistItem).getTime());
                        showBoxTime.start();
                    }
                }
            }
        });

        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.setClass(mBaseActivity,SearchActivity.class);
                intent.putExtra("key",getKey(mList.get(i).getName()));
                mBaseActivity.startActivity(intent);
            }
        });
    }


    private String getKey(String string){
        return string.substring(0,3);
    }

    private void update(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Appconfig.URL_TIME, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                Calendar calendar = new Calendar();
                mList.clear();
                calendar = gson.fromJson(response.toString(),Calendar.class);
                mList.addAll(calendar.getResultContent());
                mAdapter.notifyDataSetChanged();
                Logger.d(calendar.getResultContent().size());
                mSwipe.setRefreshing(false);
                dismisDialog();
                instance.addAll(calendar.getResultContent());
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


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showSearch(false);
        Bundle extras = intent.getExtras();
        String userQuery = String.valueOf(extras.get(SearchManager.USER_QUERY));
        String query = String.valueOf(extras.get(SearchManager.QUERY));
        suggestions.saveRecentQuery(userQuery,"");
        Logger.d(query);
        search(query);
    }

    private void search(String query){
        List<Calendar.resultContent> list = new ArrayList<>();
        list.addAll(filterList(mList,query));
        mList.clear();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    private List<Calendar.resultContent> filterList(List<Calendar.resultContent> list,String key){
        List<Calendar.resultContent> temp = new ArrayList<>();
        if(key.trim().length()>0){
            for(int i=0;i<list.size();i++){
                if(list.get(i).getName().contains(key)){
                    temp.add(list.get(i));
                }
            }
            return temp;
        }else {
            temp.addAll(instance);
           return temp;
        }
    }
    /**
     * 隐藏搜索框
     */
    public class ShowBoxTime extends CountDownTimer {

        public ShowBoxTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            if(mFrame.getVisibility() == View.GONE)
                mFrame.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish() {
            mFrame.setVisibility(View.GONE);
        }
    }

}
