package com.mrpi.kanmeiju.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mrpi.kanmeiju.R;

/**
 * Created by acer on 2016/7/15.
 */
public class DotView extends FrameLayout {

    private View mDot;
    public DotView(Context context) {
        super(context);
        applyStyle(context,null,0,0);
    }

    public DotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyStyle(context,attrs,0,0);
    }

    public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyStyle(context,attrs,defStyleAttr,0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DotView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyStyle(context,attrs,defStyleAttr,defStyleRes);
    }
    protected void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dot,null);
        addView(view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        mDot = (View) view.findViewById(R.id.dot_image);
    }

    public void setDotBackground(int id){
        mDot.setBackgroundResource(id);
    }
}
