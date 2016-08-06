package com.mrpi.kanmeiju.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mrpi.kanmeiju.AppController;
import com.mrpi.kanmeiju.R;
import com.mrpi.kanmeiju.activity.DetailActivity;
import com.mrpi.kanmeiju.bean.Events;
import com.mrpi.kanmeiju.bean.Video;
import com.mrpi.kanmeiju.dao.Meiju;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by acer on 2016/7/13.
 */
public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.VH> {

    private List mList;
    private Context mContext;


    public FollowAdapter(List list,Context context){
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main,parent,false);
        return new VH(view);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final VH holder, int position) {
        if(getItemCount()>0) {
            String url = null;
            if(getItem(position).getLocal().length()>0){
                url = getItem(position).getLocal();
            }else {
                url = getItem(position).getPicture();
            }
            holder.mTitle.setText(getItem(position).getName());
            Glide.with(mContext)
                    .load(url)
                    .into(holder.mAlbum);
            if(getItem(position).getHasUpdate()){
                holder.mEns.setText("更新至第"+getItem(position).getEpisode()+"集");
                holder.mFm.setBackground(mContext.getResources().getDrawable(R.drawable.state_shape));
            }else {
                holder.mEns.setText("暂无更新");
                holder.mFm.setBackground(mContext.getResources().getDrawable(R.drawable.follow_bg));
            }
        }
    }

    public Meiju getItem(int position){
        return ((Meiju)mList.get(position));
    }
    private void getById(String id){

    }
    public void update(Meiju meiju){

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        private ImageView mAlbum;
        private TextView mTitle;
        private CardView mCard;
        private FrameLayout mFm;
        private TextView mEns;

        public VH(View itemView) {
            super(itemView);
            mAlbum = (ImageView) itemView.findViewById(R.id.album);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mCard = (CardView) itemView.findViewById(R.id.item_movie);
            mFm = (FrameLayout) itemView.findViewById(R.id.state_tip);
            mEns = (TextView) itemView.findViewById(R.id.state_tip_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra("url",getItem(getAdapterPosition()).getVideoId());
                    intent.putExtra("picUrl", getItem(getAdapterPosition()).getPicture());
                    intent.putExtra("title",getItem(getAdapterPosition()).getName());
                    intent.putExtra("position",getLayoutPosition());
                    AppController.updateClick(getItem(getAdapterPosition()).getVideoId());
                    mContext.startActivity(intent);
                }
            });

            if(onItemLongClickListener != null)
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        onItemLongClickListener.onItemLongClickListener(view,getAdapterPosition(),getItem(getAdapterPosition()).getVideoId());
                        return false;
                    }
                });
        }
    }

    public interface OnLongClickListener{
        public void    onItemLongClickListener(View view,int position,String id);
    }
    private OnLongClickListener onItemLongClickListener;
    public void setOnItemClickListener(OnLongClickListener onItemClickListener) {
        this.onItemLongClickListener = onItemClickListener;
    }

}
