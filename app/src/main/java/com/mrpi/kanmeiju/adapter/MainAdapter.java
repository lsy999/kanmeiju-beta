package com.mrpi.kanmeiju.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mrpi.kanmeiju.AppController;
import com.mrpi.kanmeiju.R;
import com.mrpi.kanmeiju.activity.DetailActivity;
import com.mrpi.kanmeiju.bean.Video;
import com.mrpi.kanmeiju.dao.Meiju;
import com.mrpi.kanmeiju.dao.MeijuDao;
import com.mrpi.kanmeiju.utils.TextUtils;
import com.orhanobut.logger.Logger;


import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by mrpi on 2016/3/19.
 *
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Video.Meiju> mList;
    private static MeijuDao dao;

    public MainAdapter(List<Video.Meiju> list){
        if(this.mList!=null){
            this.mList.clear();
        }
        if(dao==null){
            MainAdapter.dao = AppController.getDao();
        }
        this.mList = list;
    }

    public void init(List<Video.Meiju> list){
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String url = null;
        String infoUrl = mList.get(position).getInfoUrl();

        if(getById(getId(infoUrl))!=null&&getById(getId(infoUrl)).getLocal().length()>0){
            Logger.e("有");
            url = getById(getId(infoUrl)).getLocal();
        }else {
            url = TextUtils.getUrl(mList.get(position).getPicUrl());
        }
        holder.mTitle.setText(mList.get(position).getDescription());
        Glide.with(AppController.getContext())
                .load(url)
                .into(holder.mAlbum);
        if(!exist(getId(mList.get(position).getInfoUrl()))){
            holder.mTip.setVisibility(View.GONE);
        }else {
            holder.mTip.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public boolean exist(String mId) {
        Query query = dao.queryBuilder()
                .where(MeijuDao.Properties.VideoId.eq(mId))
                .build();
        return query.list().size() > 0;
    }
    public Meiju getById(String mId){
        Query query = dao.queryBuilder()
                .where(MeijuDao.Properties.VideoId.eq(mId))
                .build();
        if(query.list().size()>0){
            return (Meiju) query.list().get(0);
        }else {
            return null;
        }
    }

    public String getId(String url){
        int index = url.lastIndexOf("/");
        int end = url.lastIndexOf(".");
        url = url.substring(index+1,end);
        return url;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mAlbum;
        private TextView mTitle;
        private CardView mCard;
        private FrameLayout mTip;

        public ViewHolder(View itemView) {
            super(itemView);
            mAlbum = (ImageView) itemView.findViewById(R.id.album);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mCard = (CardView) itemView.findViewById(R.id.item_movie);
            mTip = (FrameLayout) itemView.findViewById(R.id.state_tip);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AppController.getActivity(), DetailActivity.class);
                    String url = mList.get(getLayoutPosition()).getInfoUrl();
                    intent.putExtra("url",getId(url));
                    intent.putExtra("picUrl", mList.get(getLayoutPosition()).getPicUrl());
                    intent.putExtra("title", mList.get(getLayoutPosition()).getDescription());
                    intent.putExtra("position",getLayoutPosition());
                    AppController.updateClick(getId(url));
                    AppController.getActivity().startActivity(intent);
                }
            });
        }
    }
}
