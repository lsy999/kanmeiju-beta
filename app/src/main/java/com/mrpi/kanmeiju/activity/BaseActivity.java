package com.mrpi.kanmeiju.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mrpi.kanmeiju.AppController;
import com.mrpi.kanmeiju.R;
import com.mrpi.kanmeiju.dao.DaoSession;
import com.mrpi.kanmeiju.dao.MeijuDao;
import com.mrpi.kanmeiju.utils.SuggestionProvider;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.Query;
import me.drakeet.materialdialog.MaterialDialog;
import me.iwf.photopicker.PhotoPicker;


/**
 * Created by mrpi on 2016/3/19.
 *
 */

public abstract class BaseActivity extends AppCompatActivity{

    public Toolbar mToolBar;
    public BaseActivity mBaseActivity;
    public boolean isSearch;
    public ActionBar mActionBar;
    public MeijuDao dao;
    public DaoSession daoSession;
    public MenuItem searchItem;
    public SearchRecentSuggestions suggestions;
    public SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseActivity = this;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        daoSession = ((AppController)getApplication()).getDaoSession();
        dao = daoSession.getMeijuDao();
        initView();
        initSearch();
    }

    public void initToolbar(){
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mActionBar = getSupportActionBar();
    }

    protected abstract void initView();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean exist(String mId) {
        Query query = dao.queryBuilder()
                .where(MeijuDao.Properties.VideoId.eq(mId))
                .build();
        return query.list().size() > 0;
    }

    public List query(String mId){
        Query query = dao.queryBuilder()
                .where(MeijuDao.Properties.VideoId.eq(mId))
                .build();
        return query.list();
    }

    MaterialDialog dialog;
    public void showLoading(){
        dialog = new MaterialDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading,null);
        dialog.setContentView(view);
        dialog.show();
    }
    public void dismisDialog(){
        dialog.dismiss();
    }

    public void initSearch(){
        suggestions = new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(1000);
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView
                .findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    showSearch(false);
            }
        });
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchAutoComplete, 0);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        searchView.setSubmitButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        searchItem = menu.add(android.R.string.search_go);
        searchItem.setIcon(R.drawable.ic_search_white_24dp);
        MenuItemCompat.setActionView(searchItem, searchView);
        MenuItemCompat.setShowAsAction(searchItem,
                MenuItemCompat.SHOW_AS_ACTION_ALWAYS | MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        return true;
    }


    protected void showSearch(boolean visible) {
        if (visible)
            MenuItemCompat.expandActionView(searchItem);
        else
            MenuItemCompat.collapseActionView(searchItem);
    }

    /**
     * Called when the hardware search button is pressed
     */
    @Override
    public boolean onSearchRequested() {
        showSearch(true);
        return false;
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Logger.d(photos.get(0));
            }
        }
    }

}
