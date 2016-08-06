package com.mrpi.kanmeiju.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mrpi.kanmeiju.R;

/**
 * Created by acer on 2016/7/15.
 */
public class TagImageView extends FrameLayout {

    private ImageView mAlbum;
    private Context mContext;
    private FrameLayout frameLayout;
    private TextView textView;
    private ProgressBar mProgress;

    public TagImageView(Context context) {
        super(context);
        applyStyle(context,null,0,0);
    }

    public TagImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyStyle(context,attrs,0,0);
    }

    public TagImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyStyle(context,attrs,defStyleAttr,0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyStyle(context,attrs,defStyleAttr,defStyleRes);
    }

    protected void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_type,null);
        addView(view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.TagImageView,defStyleAttr,defStyleRes);
        mAlbum = (ImageView) view.findViewById(R.id.type_image);
        mProgress = (ProgressBar) view.findViewById(R.id.image_progress);
        frameLayout = (FrameLayout) view.findViewById(R.id.type_bg);
        textView = (TextView) view.findViewById(R.id.type_tip_text);
        boolean state = a.getBoolean(R.styleable.TagImageView_state,true);
        setTagBackground(state);
    }

    public void setImage(String url){
        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                mAlbum.setImageBitmap(bitmap);
                mProgress.setVisibility(GONE);
            }
        };
        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .into(target);
    }

    private void setTagBackground(boolean state){
        if(state){
            frameLayout.setBackgroundResource(R.drawable.state_shape);
        }else {
            frameLayout.setBackgroundResource(R.drawable.follow_bg);
        }
    }

    public void setTag(String tag){
        textView.setText(tag);
    }

    public void setTag(int res){
        textView.setText(res);
    }

    public void setState(boolean state){
        setTagBackground(state);
    }

    public void destory(){
        BitmapDrawable drawable = (BitmapDrawable)mAlbum.getDrawable();
        Bitmap bmp = drawable.getBitmap();
        if (null != bmp && !bmp.isRecycled()){
            bmp.recycle();
            bmp = null;
        }
    }
}
