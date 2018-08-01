package com.aiassoft.capstone.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.aiassoft.capstone.R;
import com.aiassoft.capstone.navigation.DrawerMenu;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gvryn on 25/07/18.
 */

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //TODO: in every image add the tag android:contentDescription="movie_poster_content_description"

    Context mContext;

    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    ViewGroup mContainer;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.nav_view) android.support.design.widget.NavigationView mNavView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_drawer);
        mContext = this;

        // for fragment see https://stackoverflow.com/questions/2395769/how-to-programmatically-add-views-to-views
        mContainer = this.findViewById(R.id.layout_container);
        View.inflate(this, R.layout.activity_dashboard, mContainer);

        ButterKnife.bind(this);

        mNavView = this.findViewById(R.id.nav_view);

        setSupportActionBar(mToolbar);

        initFab();

        initDrawer();

        initNavigation();
    }

    private void initFab() {
        if (mFab != null) {
            mFab.setVisibility(View.GONE);
        }
    }

    private void initDrawer() {
        //mDrawer = findViewById(R.id.drawer_layout);
        //mDrawer.setFocusable(true);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //This is not working properly
                mNavView.requestFocus();
                /*
                if(mNavView.requestFocus()){
                    NavigationMenuView navigationMenuView = (NavigationMenuView)mNavView.getFocusedChild();
                    navigationMenuView.setPressed(true);

                    //navigationMenuView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                }
                */
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //This seems to work
                mContainer.requestFocus();
            }
        };
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void initNavigation() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);

        // Handle navigation view item clicks here.
        if (DrawerMenu.navigate(this, item.getItemId()))
            finish();

        return true;
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        if(keyCode== KeyEvent.KEYCODE_DPAD_RIGHT){
            mDrawerToggle.syncState();
            if(! mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            }
        }
        else if(keyCode== KeyEvent.KEYCODE_DPAD_LEFT){
            mDrawerToggle.syncState();
            if(mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
                return true;
            }
        }

        return super.onKeyUp(keyCode, event);
    }


}


