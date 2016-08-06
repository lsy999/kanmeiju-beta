package com.mrpi.kanmeiju.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.mrpi.kanmeiju.ui.BannerView;
import com.orhanobut.logger.Logger;

import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by acer on 2016/7/17.
 */
public class HotAdapter extends RecyclerView.Adapter<HotAdapter.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private List<Video.Meiju> mList;
    private static MeijuDao dao;
    private View mHeaderView;

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null) return TYPE_NORMAL;
        if(position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    public HotAdapter(List<Video.Meiju> list){
        if(this.mList!=null){
            this.mList.clear();
        }
        if(dao==null){
            HotAdapter.dao = AppController.getDao();
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
        if(mHeaderView != null && viewType == TYPE_HEADER) return new ViewHolder(mHeaderView);
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE_HEADER){
            return;
        }
        final int pos = getRealPosition(holder);
        if(holder instanceof ViewHolder) {
            ((ViewHolder)holder).mTitle.setText(mList.get(pos).getDescription());
            final Uri uri;
            if (mList.get(pos).getPicUrl().startsWith("http"))
                uri = Uri.parse(mList.get(pos).getPicUrl());
            else
                uri = Uri.parse("http://kanmeiju.net" + mList.get(pos).getPicUrl());
            Glide.with(AppController.getContext())
                    .load(uri)
                    .into(((ViewHolder)holder).mAlbum);
            if (!exist(getId(mList.get(pos).getInfoUrl()))) {
                ((ViewHolder)holder).mTip.setVisibility(View.GONE);
            } else {
                ((ViewHolder)holder).mTip.setVisibility(View.VISIBLE);
            }
        }

    }
    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }



    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }
    public View getHeaderView() {
        return mHeaderView;
    }

    public List<Meiju> query(){
        Query query = AppController.getDao().queryBuilder()
                .build();
        return ((List<Meiju>) query.list());
    }
    @Override
    public int getItemCount() {
        return mHeaderView == null ? mList.size() : mList.size() + 1;
    }
    public boolean exist(String mId) {
        Query query = dao.queryBuilder()
                .where(MeijuDao.Properties.VideoId.eq(mId))
                .build();
        return query.list().size() > 0;
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
        private BannerView mBanner;
        public int getRealPosition() {
            int position = getAdapterPosition();
            return mHeaderView == null ? position : position - 1;
        }
        public ViewHolder(final View itemView) {
            super(itemView);
            if(itemView == mHeaderView) return;
            mAlbum = (ImageView) itemView.findViewById(R.id.album);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mCard = (CardView) itemView.findViewById(R.id.item_movie);
            mTip = (FrameLayout) itemView.findViewById(R.id.state_tip);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AppController.getActivity(), DetailActivity.class);
                    String url = mList.get(getRealPosition()).getInfoUrl();
                    intent.putExtra("url", getId(url));
                    intent.putExtra("picUrl", mList.get(getRealPosition()).getPicUrl());
                    intent.putExtra("title", mList.get(getRealPosition()).getDescription());
                    intent.putExtra("position", getRealPosition());
                    AppController.updateClick(getId(url));
                    AppController.getActivity().startActivity(intent);
                }
            });

        }
    }
}