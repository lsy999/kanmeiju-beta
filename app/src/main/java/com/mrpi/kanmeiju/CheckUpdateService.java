package com.mrpi.kanmeiju;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mrpi.kanmeiju.bean.Detail;
import com.mrpi.kanmeiju.bean.Events;
import com.mrpi.kanmeiju.dao.DaoSession;
import com.mrpi.kanmeiju.dao.Meiju;
import com.mrpi.kanmeiju.dao.MeijuDao;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by acer on 2016/7/13.
 */
public class CheckUpdateService extends Service {

    private UpdateBinder mBinder = new UpdateBinder();
    private DaoSession daoSession;
    private MeijuDao dao;
    private Context context;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("创建服务");
        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("运行命令");
        mBinder.startCheck();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("销毁服务");
    }

    class UpdateBinder extends Binder {
        public void startCheck() {
            Logger.d("检查美剧更新");
            daoSession = ((AppController)context.getApplicationContext()).getDaoSession();
            dao = daoSession.getMeijuDao();
            Task task = new Task();
            task.execute();
        }
    }

    class Task extends AsyncTask<Object,Object,Integer>{
        @Override
        protected Integer doInBackground(Object... objects) {
            for(final Meiju item:getAll()){

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,item.getUrl(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        Detail detail = gson.fromJson(response.toString(), Detail.class);
                        if(detail.getResultCode()==0){
                            int size = detail.getResultContent().getEd2k().size();
                            if(size>Integer.parseInt(item.getEpisode())){
                                item.setEpisode(String.valueOf(size));
                                item.setDate(new Date());
                                item.setHasUpdate(true);
                                update(item);
                                Toast.makeText(context, item.getName()+"更新了", Toast.LENGTH_SHORT).show();
                            }else {
                                if(zone(item.getDate())){
                                    item.setHasUpdate(false);
                                    update(item);
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                AppController.getInstance().addToRequestQueue(jsonObjectRequest);
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Events.postBannerShow();
            Events.postServiceDestroy();
        }
    }

    public List<Meiju> getAll() {
        Query query = dao.queryBuilder()
                .build();
        return (List<Meiju>) query.list();
    }

    public void update(Meiju meju){
        dao.update(meju);
    }

    public List query(String mId){
        Query query = dao.queryBuilder()
                .where(MeijuDao.Properties.VideoId.eq(mId))
                .build();
        return query.list();
    }

    public boolean zone(Date date){
        Date mDate = new Date();
        long current = mDate.getTime();
        long ago = date.getTime();
        return (current - ago) / (1000 * 60 * 60 * 24) > 2;
    }
}
