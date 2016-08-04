package com.mrpi.kanmeiju;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mrpi.kanmeiju.activity.DemoActivity;
import com.mrpi.kanmeiju.activity.FollowActivity;
import com.mrpi.kanmeiju.activity.SearchActivity;
import com.mrpi.kanmeiju.activity.TimeActivity;
import com.mrpi.kanmeiju.adapter.TabAdapter;
import com.mrpi.kanmeiju.bean.Events;
import com.mrpi.kanmeiju.dao.DaoSession;
import com.mrpi.kanmeiju.dao.MeijuDao;
import com.mrpi.kanmeiju.fragments.HotFragment;
import com.mrpi.kanmeiju.fragments.KhFragment;
import com.mrpi.kanmeiju.fragments.MvFragment;
import com.mrpi.kanmeiju.fragments.XjFragment;
import com.mrpi.kanmeiju.fragments.XyFragment;
import com.mrpi.kanmeiju.utils.ACache;
import com.mrpi.kanmeiju.utils.SuggestionProvider;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem searchItem;
    private SearchRecentSuggestions suggestions;
    private SearchView searchView;
    private DrawerLayout mDrawer;
    private ViewPager mPager;
    private TabLayout mTab;

    private static Context context;
    private ACache mCache;
    private DaoSession daoSession;
    private static MeijuDao dao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initView();
        startService();
        AppController.setActivity(this);
        daoSession = AppController.getDaoSession();
        dao = daoSession.getMeijuDao();
        mCache = ACache.get(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1
            );
        }


        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            if(mCache.getAsString("init")==null){
                showDialog();
            }
            mCache.put("init","1");
        }
    }

    private void showDialog(){
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setTitle("提示");
        dialog.setMessage(R.string.license);
        dialog.setNegativeButton("了解", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mCache.getAsString("init")==null){
                        showDialog();
                    }
                    mCache.put("init",1);
                } else {

                }
            }
        }
    }

    protected void startService(){
        Intent startIntent = new Intent(this, CheckUpdateService.class);
        bindService(startIntent,connection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private CheckUpdateService.UpdateBinder mBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {}
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (CheckUpdateService.UpdateBinder) service;
            mBinder.startCheck();
        }
    };

    private void initView(){
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
        mPager = (ViewPager) findViewById(R.id.vp_view);
        mTab = (TabLayout) findViewById(R.id.tab);
        initTab();
    }

    private void initTab(){
        List<Fragment> mFragmentList = new ArrayList<>();
        List<String> tabTitle = new ArrayList<>();
        mFragmentList.add(HotFragment.newInstance(context));
        mFragmentList.add(XyFragment.newInstance(this, Appconfig.TYPE_XY));
        mFragmentList.add(KhFragment.newInstance(context, Appconfig.TYPE_KH));
        mFragmentList.add(XjFragment.newInstance(context, Appconfig.TYPE_XJ));
        mFragmentList.add(MvFragment.newInstance(context, Appconfig.TYPE_MV));

        tabTitle.add("热门推荐");
        tabTitle.add("悬疑");
        tabTitle.add("科幻");
        tabTitle.add("喜剧");
        tabTitle.add("热门影片");

        mPager.setOffscreenPageLimit(5);
        TabAdapter mAdapter = new TabAdapter(getSupportFragmentManager(), mFragmentList, tabTitle);
        mPager.setAdapter(mAdapter);
        mTab.setupWithViewPager(mPager);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showSearch(false);
        Bundle extras = intent.getExtras();
        String userQuery = String.valueOf(extras.get(SearchManager.USER_QUERY));
        String query = String.valueOf(extras.get(SearchManager.QUERY));
        suggestions.saveRecentQuery(userQuery,"");
        intent.setClass(this,SearchActivity.class);
        intent.putExtra("key",query);
        startActivity(intent);
    }

    protected void showSearch(boolean visible) {
        try {
            if (visible)
                MenuItemCompat.expandActionView(searchItem);
            else
                MenuItemCompat.collapseActionView(searchItem);
        }catch (Exception ex){
            Logger.d(ex.getMessage());
        }

    }

    /**
     * Called when the hardware search button is pressed
     */
    @Override
    public boolean onSearchRequested() {
        showSearch(true);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        searchItem = menu.add(android.R.string.search_go);
        searchItem.setIcon(R.drawable.ic_search_white_24dp);
        MenuItemCompat.setActionView(searchItem, searchView);
        MenuItemCompat.setShowAsAction(searchItem,
                MenuItemCompat.SHOW_AS_ACTION_ALWAYS | MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
           startActivity(FollowActivity.class);
        } else if (id == R.id.nav_gallery) {
            startActivity(TimeActivity.class);
        }else {
            startActivity(DemoActivity.class);
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void startActivity(Class clazz){
        Intent intent = new Intent(this,clazz);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events e){
        switch (e.getCode()){
            case Events.SERVICE_DESTROY:
                unbindService(connection);
                stopService(new Intent(this,CheckUpdateService.class));
                break;
            case Events.SERVICE_START:
                startService();
                break;
        }
    }
}
