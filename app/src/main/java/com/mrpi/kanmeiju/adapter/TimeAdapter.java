package com.mrpi.kanmeiju.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mrpi.kanmeiju.R;
import com.mrpi.kanmeiju.activity.DetailActivity;
import com.mrpi.kanmeiju.activity.SearchActivity;
import com.mrpi.kanmeiju.bean.Calendar;
import com.orhanobut.logger.Logger;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;

/**
 * Created by acer on 2016/7/14.
 */
public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.VH> implements FastScrollRecyclerView.SectionedAdapter {

    List<Calendar.resultContent> list = new ArrayList<>();
    Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    public TimeAdapter(List<Calendar.resultContent> list, Context context){
        this.list = list;
        mContext = context;
    }

    @Override
    public TimeAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time,parent,false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(TimeAdapter.VH holder, int position) {
        Glide.with(mContext)
                .load("http://huo360.com/"+getItem(position).getPic())
                .into(holder.mAlbum);
        holder.mEns.setText(getItem(position).getTime());
        holder.mTitle.setText(getItem(position).getName());
        holder.mSize.setText("更新至"+getItem(position).getSize());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Calendar.resultContent getItem(int position){
        return list.get(position);
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return getWeek(getItem(position).getWeek());
    }

    private String getWeek(int week){
        String str = "周";
        switch (week){
            default:
                return str += "一";
            case 1:
                return str += "一";
            case 2:
                return str += "二";
            case 3:
                return str += "三";
            case 4:
                return str += "四";
            case 5:
                return str += "五";
            case 6:
                return str += "六";
            case 7:
                return str += "末";
        }
    }


    public class VH extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mAlbum;
        private TextView mTitle;
        private CardView mCard;
        private FrameLayout mFm;
        private TextView mEns;
        private FrameLayout mSizeLay;
        private TextView mSize;

        public VH(View itemView) {
            super(itemView);
            mAlbum = (ImageView) itemView.findViewById(R.id.album);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mCard = (CardView) itemView.findViewById(R.id.item_movie);
            mFm = (FrameLayout) itemView.findViewById(R.id.state_tip);
            mEns = (TextView) itemView.findViewById(R.id.state_tip_text);
            mSize = (TextView) itemView.findViewById(R.id.ens_tip_text);
            mSizeLay = (FrameLayout) itemView.findViewById(R.id.ens_tip);
            if(onItemClickListener != null)
                itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(null,v,getAdapterPosition(),getItemId());
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
