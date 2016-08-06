package com.mrpi.kanmeiju;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.mrpi.kanmeiju.bean.Events;
import com.mrpi.kanmeiju.dao.DaoMaster;
import com.mrpi.kanmeiju.dao.DaoSession;
import com.mrpi.kanmeiju.dao.Meiju;
import com.mrpi.kanmeiju.dao.MeijuDao;
import com.mrpi.kanmeiju.utils.BitmapCache;
import com.orhanobut.logger.Logger;

import de.greenrobot.dao.query.Query;

/**
 * Created by mrpi on 2016/7/4.
 */
public class AppController extends Application {

    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context context;
    private static AppController mInstance;
    /** A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher. */
    public static final boolean ENCRYPTED = true;

    private static DaoSession daoSession;
    private static Activity activity;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Logger.init();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "meiju-db", null);
        SQLiteDatabase db = helper.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        context = this;
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new BitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    public static MeijuDao getDao(){
        return AppController.getDaoSession().getMeijuDao();
    }
    public static Context getContext(){
        return context;
    }

    public static Activity getActivity(){
        return activity;
    }
    public static void setActivity(Activity mActivity){
        activity = mActivity;
    }

    public static boolean exist(String mId) {
        Query query = getDao().queryBuilder()
                .where(MeijuDao.Properties.VideoId.eq(mId))
                .build();
        return query.list().size() > 0;
    }

    public static Meiju getById(String id){
        Query query = getDao().queryBuilder()
                .where(MeijuDao.Properties.VideoId.eq(id))
                .build();
        if(query.list().size()>0){
            return (Meiju) query.list().get(0);
        }else {
            return null;
        }
    }

    public static void update(Meiju meiju){
        if(exist(meiju.getVideoId()))
            getDao().update(meiju);
    }

    public static void updateClick(String id){
        Meiju meiju = AppController.getById(id);
        if (meiju != null) {
            int click = meiju.getClick() + 1;
            meiju.setClick(click);
            AppController.update(meiju);
            Events.postVideoFollow(null);
        }
    }

}