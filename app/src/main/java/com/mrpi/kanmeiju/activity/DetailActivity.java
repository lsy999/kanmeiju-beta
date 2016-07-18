package com.mrpi.kanmeiju.activity;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.mrpi.kanmeiju.AppController;
import com.mrpi.kanmeiju.Appconfig;
import com.mrpi.kanmeiju.R;
import com.mrpi.kanmeiju.adapter.Ed2kAdapter;
import com.mrpi.kanmeiju.bean.Detail;
import com.mrpi.kanmeiju.bean.Events;
import com.mrpi.kanmeiju.dao.Meiju;
import com.mrpi.kanmeiju.dao.MeijuDao;
import com.mrpi.kanmeiju.ui.FullyGridLayoutManager;
import com.mrpi.kanmeiju.utils.AppBarStateChangeListener;
import com.mrpi.kanmeiju.utils.FastBlur;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.Query;

public class DetailActivity extends BaseActivity implements View.OnClickListener{

    private ImageView mBackground;
    private ImageView mAlbum;
    private TextView mStatus;
    private TextView mLarger;
    private String mId;
    private String mTitleStr;
    private TextView mDesc;
    private Uri uri;
    private RecyclerView mListEd2k;
    private Ed2kAdapter mAdapter;
    private Boolean isLager = false;
    private MeijuDao dao;
    private String desc;
    private String mVideoUrl;

    private FloatingActionButton mFb;
    private CollapsingToolbarLayout mCollBar;
    private AppBarLayout mAppBar;
    private CardView mAlbum_layout;
    private boolean isScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        daoSession = ((AppController)getApplication()).getDaoSession();
        dao = daoSession.getMeijuDao();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events e){
        switch (e.getCode()){

        }
    }
    public void addMeiju(){
        Meiju meiju = new Meiju();
        meiju.setName(mTitleStr);
        meiju.setFollow(true);
        meiju.setPicture(String.valueOf(uri));
        Date date = new Date();
        meiju.setDate(date);
        meiju.setDesc(desc);
        meiju.setEpisode(String.valueOf(mAdapter.getItemCount()));
        meiju.setUrl(mVideoUrl);
        meiju.setVideoId(mId);
        meiju.setHasUpdate(false);
        meiju.setClick(0);
        meiju.setLocal("");
        if(!exist(mId)){
            long count = dao.insert(meiju);
            if(count>0){
                Toast.makeText(DetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
            }
        }else {
            delete(meiju);
            Toast.makeText(DetailActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
        }

        if(exist(mId)){
            mFb.setImageResource(R.drawable.ic_star_white_18dp);
        }else {
            mFb.setImageResource(R.drawable.ic_star_border_white_18dp);
        }

        if(getSize()>3){
            Events.postBannerShow();
        }else {
            Events.postBannerHide();
        }
    }


    @Override
    protected void initView() {
        try {
            showLoading();
            Intent intent = getIntent();
            mId = intent.getStringExtra("url");
            String mPicUrl = intent.getStringExtra("picUrl");
            mTitleStr = intent.getStringExtra("title");
            TextView mTitle = (TextView) findViewById(R.id.title);

            initEd2kView();
            if(mPicUrl.startsWith("http"))
                uri = Uri.parse(mPicUrl);
            else
                uri = Uri.parse("http://kanmeiju.net"+ mPicUrl);


            mStatus = (TextView) findViewById(R.id.status);

            mLarger = (TextView) findViewById(R.id.know_more);
            mDesc = (TextView) findViewById(R.id.desc);
            mAlbum = (ImageView) findViewById(R.id.album);
            mBackground = (ImageView) findViewById(R.id.background);
            mToolBar = (Toolbar) findViewById(R.id.toolbar);
            mFb = (FloatingActionButton) findViewById(R.id.fab);
            mFb.setOnClickListener(this);
            mCollBar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            mAppBar = (AppBarLayout) findViewById(R.id.app_bar);
            mAlbum_layout = (CardView) findViewById(R.id.album_layout);

            mTitle.setText(mTitleStr);
            setSupportActionBar(mToolBar);

            if(getSupportActionBar()!=null) {
                mActionBar = getSupportActionBar();
                mActionBar.setTitle("");
                mActionBar.setDisplayHomeAsUpEnabled(true);
                mActionBar.setDefaultDisplayHomeAsUpEnabled(true);
            }
            mLarger.setOnClickListener(this);
            mVideoUrl = Appconfig.URL_DT+mId;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, mVideoUrl, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Detail detail = gson.fromJson(response.toString(), Detail.class);
                    if(detail.getResultCode()==0){
                        desc = detail.getResultContent().getDetail();
                        setDescription(desc);
                        setAlbum();
                        setBackground();
                        setEd2kAdapter(detail.getResultContent().getEd2k());
                        setInfo(detail.getResultContent().getInfo());
                        dismisDialog();
                        mFb.setClickable(true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismisDialog();
                    mFb.setClickable(false);
                }
            });
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);
        }catch (Exception ignored){
            Logger.e(ignored.getMessage());
        }

        if(exist(mId)){
            mFb.setImageResource(R.drawable.ic_star_white_18dp);
        }else {
            mFb.setImageResource(R.drawable.ic_star_border_white_18dp);
        }
    }

    private void delete(Meiju meiju){
        List list = query(meiju.getVideoId());
        dao.delete(((Meiju) list.get(0)));
    }

    private void initEd2kView(){
        mListEd2k = (RecyclerView) findViewById(R.id.list_item);
        FullyGridLayoutManager mGrid = new FullyGridLayoutManager(this, 5);
        mListEd2k.setHasFixedSize(true);
        mListEd2k.setNestedScrollingEnabled(false);
        mListEd2k.setLayoutManager(mGrid);
    }

    public void setInfo(List<String> infos) {
        String s = "";
        for (String str:infos){
            s +=str+"\n";
        }
        mStatus.setText(s);

    }


    public void setEd2kAdapter(List<String> ed2ks) {
        mAdapter = new Ed2kAdapter(ed2ks,mBaseActivity);
        mListEd2k.setAdapter(mAdapter);
    }


    public void setAlbum() {
        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {

            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                mAlbum.setImageBitmap(bitmap);
            }
        };
        Glide.with(this)
                .load(uri)
                .asBitmap()
                .into(target);
    }

    public void setBackground() {
        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {

            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                mBackground.setAlpha(400);
                mBackground.setImageBitmap(FastBlur.doBlur(bitmap,120,false));
            }
        };
        Glide.with(this)
                .load(uri)
                .asBitmap()
                .into(target);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void blur(Bitmap bkg, View view){
        long startMs = System.currentTimeMillis();
        float radius = 20;
        float scaleFactor = 8;
        Bitmap overlay = Bitmap.createBitmap((int)(view.getMeasuredWidth()/scaleFactor), (int)(view.getMeasuredHeight()/scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        overlay = FastBlur.doBlur(overlay, (int)radius, true);
        ((ImageView)view).setImageBitmap(overlay);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showSearch(false);
        Bundle extras = intent.getExtras();
        String userQuery = String.valueOf(extras.get(SearchManager.USER_QUERY));
        String query = String.valueOf(extras.get(SearchManager.QUERY));
        suggestions.saveRecentQuery(userQuery,"");
        intent.setClass(this,SearchActivity.class);
        intent.putExtra("key",query);
        startActivity(intent);
    }

    public void setDescription(String description) {
        mDesc.setText(description.trim());
    }
    public BaseActivity getActivity() {
        return mBaseActivity;
    }
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.know_more:
                if(isLager){
                    mDesc.setMaxLines(3);
                    isLager = false;
                    mLarger.setText(getString(R.string.know_more));
                }else {
                    mDesc.setMaxLines(999);
                    mLarger.setText(getString(R.string.tip_up));
                    isLager = true;
                }
                break;
            case R.id.fab:
                addMeiju();
                Events.postVideoFollow(null);
                break;
        }
    }

    private int getSize(){
        Query query = dao.queryBuilder()
                .build();
        return query.list().size();
    }

}
