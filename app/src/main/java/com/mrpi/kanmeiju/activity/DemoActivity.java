package com.mrpi.kanmeiju.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.mrpi.kanmeiju.R;
import com.mrpi.kanmeiju.utils.FastBlur;
import com.nineoldandroids.view.ViewHelper;
import com.orhanobut.logger.Logger;

public class DemoActivity extends AbsActivity implements ObservableScrollViewCallbacks {

    private View mImageView;
    private View mToolbarView;
    private View mListBackgroundView;
    private ObservableListView mListView;
    private int mParallaxImageHeight;
    private View mStatusBarView;
    private ImageView mAlbum;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo) ;
        initView();
    }

    @Override
    protected void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mImageView = findViewById(R.id.bg_image);
        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {

            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                blur(bitmap,mImageView);
            }
        };
        Glide.with(this)
                .load("http://img.tuku.cn/file_big/201504/024448d7ae994d4d9273ea5f3c199a5a.jpg")
                .asBitmap()
                .into(target);

        mToolbarView = findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));
        mStatusBarView = findViewById(R.id.statusHeaderView);
        mStatusBarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.nav_header_height);

        mListView = (ObservableListView) findViewById(R.id.list);
        mListView.setScrollViewCallbacks(this);
        // Set padding view for ListView. This is the flexible space.
        View paddingView = new View(this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                mParallaxImageHeight);
        paddingView.setLayoutParams(lp);

        // This is required to disable header's list selector effect
        paddingView.setClickable(true);

        mListView.addHeaderView(paddingView);
        setDummyData(mListView);

        // mListBackgroundView makes ListView's background except header view.
        mListBackgroundView = findViewById(R.id.list_background);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mListView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, -scrollY / 2);
        mStatusBarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mListBackgroundView, Math.max(0, -scrollY + mParallaxImageHeight));
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

}