package com.mrpi.kanmeiju.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.mrpi.kanmeiju.R;
import com.mrpi.kanmeiju.activity.BaseActivity;

import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by mrpi on 2016/3/23.
 */
public class Ed2kAdapter extends RecyclerView.Adapter<Ed2kAdapter.Ed2kViewHolder> {

    private BaseActivity mActivity;
    private List<String> mEd2ks;

    public Ed2kAdapter(List<String> ed2ks, BaseActivity activity){
        this.mEd2ks = ed2ks;
        this.mActivity = activity;
    }

    @Override
    public Ed2kViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_ed2k,parent,false);
        return new Ed2kViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Ed2kViewHolder holder, int position) {
        holder.mEd2k.setText(String.valueOf(position+1));
        if(mEd2ks.size()-1==position){
            holder.mIsNew.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mEd2ks.size();
    }

    public class Ed2kViewHolder extends RecyclerView.ViewHolder {
        public TextView mEd2k;
        public TextView mIsNew;

        public Ed2kViewHolder(View itemView) {
            super(itemView);
            mEd2k = (TextView) itemView.findViewById(R.id.ed2k);
            mIsNew = (TextView) itemView.findViewById(R.id.news);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });
        }

        private void showDialog(){
            final MaterialDialog dialog = new MaterialDialog(mActivity);
            dialog.setTitle("选择操作");
            View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_chose_app,null);
            Button btn_xunlei = (Button) view.findViewById(R.id.btn_xunlei);
            Button btn_baidu = (Button) view.findViewById(R.id.btn_baidu);
            Button btn_all = (Button) view.findViewById(R.id.btn_xunlei_all);

            btn_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (String url:mEd2ks) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            mActivity.startActivity(intent);
                        }catch (Exception ex){
                            Toast.makeText(mActivity, mActivity.getString(R.string.tip_no_app), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            btn_baidu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        PackageManager packageManager = mActivity.getPackageManager();
                        Intent intent;
                        intent = packageManager.getLaunchIntentForPackage("com.baidu.netdisk");
                        intent.setData(Uri.parse(mEd2ks.get(getLayoutPosition())));
                        mActivity.startActivity(intent);
                    }catch (Exception ex){
                        Toast.makeText(mActivity, "百度云启动失败", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            btn_xunlei.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(mEd2ks.get(getLayoutPosition())));
                        mActivity.startActivity(intent);
                    }catch (Exception ex){
                        Toast.makeText(mActivity, mActivity.getString(R.string.tip_no_app), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialog.setContentView(view);
            dialog.setNegativeButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.setPositiveButton("复制下载链接", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager manager = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    manager.setText(mEd2ks.get(getLayoutPosition()));
                    Toast.makeText(mActivity,mActivity.getString(R.string.tip_clip), Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }
    }
}
