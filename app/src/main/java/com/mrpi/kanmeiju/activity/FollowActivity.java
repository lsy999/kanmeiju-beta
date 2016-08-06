package com.mrpi.kanmeiju.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.mrpi.kanmeiju.AppController;
import com.mrpi.kanmeiju.R;
import com.mrpi.kanmeiju.adapter.FollowAdapter;
import com.mrpi.kanmeiju.bean.Events;
import com.mrpi.kanmeiju.dao.Meiju;
import com.mrpi.kanmeiju.dao.MeijuDao;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.Query;
import me.drakeet.materialdialog.MaterialDialog;
import me.iwf.photopicker.PhotoPicker;

public class FollowActivity extends BaseActivity {

    public RecyclerView mRecy;
    public FollowAdapter mAdapter;
    public GridLayoutManager mGrid;
    public List mList = new ArrayList<>();
    public SwipeRefreshLayout mSwipe;
    private int position = 0;
    private String mVId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events e){
        switch (e.getCode()){
            case Events.VIDEO_UPDATE:
                e.getTag();
                break;
            case Events.VIDEO_FOLLOW:
                update();
                break;
        }
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
        Logger.d(query);
        startActivity(intent);
    }

    @Override
    protected void initView() {
        initToolbar();
        mRecy = (RecyclerView) findViewById(R.id.recyclerView);
        mGrid = new GridLayoutManager(this,2);
        mRecy.setLayoutManager(mGrid);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swiper_refresh);
        mAdapter = new FollowAdapter(mList,this);
        mRecy.setAdapter(mAdapter);
        update();
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
        mAdapter.setOnItemClickListener(new FollowAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClickListener(View view, int index,String id) {
                position = index;
                mVId = id;
                showChosePicDialog();
            }
        });
    }

    private void update(){
        mList.clear();
        Query query = dao.queryBuilder()
                .orderDesc(MeijuDao.Properties.Click)
                .build();
        mList.addAll(query.list());
        mAdapter.notifyDataSetChanged();
        mSwipe.setRefreshing(false);
    }

    MaterialDialog dialog;
    private void showChosePicDialog(){
        dialog = new MaterialDialog(this);
        dialog.setTitle("操作");
        dialog.setMessage("选择本地图片作为封面?");
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismisChosePicDialog();
            }
        });
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .start(mBaseActivity, PhotoPicker.REQUEST_CODE);
                dismisChosePicDialog();
            }
        });
        dialog.show();
    }

    private void dismisChosePicDialog(){
        dialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Logger.d(photos.get(0));
                Meiju meiju = (Meiju) query(mVId).get(0);
                meiju.setLocal(photos.get(0));
                AppController.update(meiju);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

}
