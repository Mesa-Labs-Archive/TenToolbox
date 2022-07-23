package com.mesalabs.ten.toolbox.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mesalabs.ten.core.ui.utils.FragmentsUtils;
import com.mesalabs.ten.core.update.utils.AppUpdateUtils;
import com.mesalabs.ten.core.utils.PreferencesUtils;
import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.TenToolboxApp;
import com.mesalabs.ten.toolbox.fragment.drawer.MainDrawerFragment;
import com.mesalabs.ten.romcontrol.ui.bottomtab.RCTabManager;
import com.mesalabs.ten.toolbox.ui.drawer.MainDrawerTabManager;
import com.mesalabs.ten.update.activity.OTAMainActivity;

import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.menu.MenuItem;
import de.dlyt.yanndroid.oneui.sesl.support.ViewSupport;
import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;
import de.dlyt.yanndroid.oneui.widget.RoundLinearLayout;
import de.dlyt.yanndroid.oneui.widget.TabLayout;

/*
 * 십 Toolbox
 *
 * Coded by BlackMesa123 @2021
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

public class DrawerMainActivity extends AppCompatActivity {
    private static final String SP_DB = TenToolboxApp.getAppPackageName() + "_tabs";

    private static final int HOME_TAB = 0;
    private static final int RC_TAB = 2;
    private static final int OTA_TAB = 3;
    private static final int ADDONS_TAB = 5;

    private static final int RC_INNER_FRAMEWORKS_TAB = 0;


    private AppUpdateUtils mAppUpdate;
    private AppUpdateUtils.StubListener mStubListener = new AppUpdateUtils.StubListener() {
        public void onUpdateCheckCompleted(int status) {
            boolean available = status == AppUpdateUtils.STATE_NEW_VERSION_AVAILABLE;
            PreferencesUtils.setIsAppUpdateAvailable(available);
            mDrawerFragment.setAppUpdateAvailable(available);
            mToolbarLayout.setNavigationButtonBadge(available ? ToolbarLayout.N_BADGE : 0);
        }
    };
    private MainDrawerTabManager mDrawerTabManager;
    private RCTabManager mBottomTabManager;

    private FragmentManager mFragmentManager;
    private MainDrawerFragment mDrawerFragment;
    private Fragment mInflatedFragment;

    private DrawerLayout mDrawerLayout;
    private RoundLinearLayout mHomeMainParent;
    private ToolbarLayout mToolbarLayout;
    private FrameLayout mHomeFragmentContainer;
    private View mHomeDrawerDim;
    private FrameLayout mHomeDrawerContainer;
    private FrameLayout mFooter;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewSupport.semSetRoundedCorners(getWindow().getDecorView(), 0);

        // init TabsManager
        mDrawerTabManager = new MainDrawerTabManager(SP_DB);
        mDrawerTabManager.initTabPosition();
        mBottomTabManager = new RCTabManager(SP_DB);
        mBottomTabManager.initTabPosition();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String fragment = extras.getString("ten_drawer_category", "");
            String[] drawerTags = FragmentsUtils.getDrawerFragmentsTag();
            for (int i = 0; i < drawerTags.length; i++)  {
                if (fragment.equals(drawerTags[i]))
                    mDrawerTabManager.setTabPosition(i);
            }
        }

        //  init AppUpdateUtils
        mAppUpdate = new AppUpdateUtils(this, TenToolboxApp.getAppPackageName(), mStubListener);
        mAppUpdate.checkUpdates();

        // init Views
        setContentView(R.layout.ten_toolbox_activity_maindrawer_layout);

        init();
        initDrawer();
        initBottomTabs();

        onDrawerItemChanged(mDrawerTabManager.getCurrentTab(), true);
    }

    @Override
    public void onBackPressed() {
        if (!closeDrawer()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateDrawerLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        closeDrawer(false);
    }



    private void init() {
        mFragmentManager = getSupportFragmentManager();

        mHomeDrawerDim = findViewById(R.id.ten_maindraweractivity_drawer_dim);
        mDrawerLayout = findViewById(R.id.ten_maindraweractivity_drawerlayout);
        mHomeMainParent = findViewById(R.id.ten_maindraweractivity_content_main);
        mToolbarLayout = findViewById(R.id.ten_maindraweractivity_toolbarlayout);
        mHomeFragmentContainer = findViewById(R.id.ten_maindraweractivity_content_container);
        mHomeDrawerContainer = findViewById(R.id.ten_maindraweractivity_drawer_fragment);
        mFooter = findViewById(R.id.ten_maindraweractivity_content_footer);

        mToolbarLayout.setNavigationButtonIcon(getResources().getDrawable(R.drawable.ic_oui_drawer, getTheme()));
        mToolbarLayout.setNavigationButtonTooltip(getString(R.string.sesl_navigation_drawer));
        mToolbarLayout.setNavigationButtonOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                openDrawer();
            }
        });

        mToolbarLayout.inflateToolbarMenu(R.menu.ten_toolbox_menu_mainactivity);
        mToolbarLayout.setOnToolbarMenuItemClickListener(new ToolbarLayout.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.ten_menu_help) {
                    startActivity(new Intent(DrawerMainActivity.this, HelpActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void initDrawer() {
        mDrawerFragment = MainDrawerFragment.newInstance();

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.ten_maindraweractivity_drawer_fragment, mDrawerFragment);
        transaction.commit();

        mDrawerLayout.setScrimColor(getColor(android.R.color.transparent));
        mDrawerLayout.setDrawerElevation(0.0f);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (slideOffset < 0.0f) {
                    slideOffset = 0.0f;
                }

                moveHomeMainParent();
                setStatusBarAndNavigationBarColor(slideOffset);
                mHomeDrawerDim.setAlpha(slideOffset);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) { }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) { }

            @Override
            public void onDrawerStateChanged(int newState) { }

            private void moveHomeMainParent() {
                int fragmentWidth = mHomeDrawerContainer.getWidth();
                float xAxisPos = mHomeDrawerContainer.getX();
                float measuredWidth;

                if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    measuredWidth = (float) mHomeMainParent.getMeasuredWidth();
                    if (measuredWidth - xAxisPos <= measuredWidth) {
                        xAxisPos -= measuredWidth;
                    } else  {
                        xAxisPos = 0.0f;
                    }
                } else {
                    measuredWidth = (float) fragmentWidth + xAxisPos;
                    if (measuredWidth < 0.0f) {
                        xAxisPos = 0.0f;
                    } else {
                        xAxisPos = measuredWidth;
                    }
                }

                mHomeMainParent.setTranslationX(xAxisPos);
            }

            private void setStatusBarAndNavigationBarColor(float slideOffset) {
                Window window = getWindow();

                int dimColor = getResources().getColor(R.color.ten_drawer_dim_color, getTheme());
                int color = getResources().getColor(R.color.sesl4_round_and_bgcolor, getTheme());
                int scrimColor = (((int) (((float) ((Color.BLACK & dimColor) >>> 24)) * slideOffset)) << 24) | (dimColor & 0x00FFFFFF);

                if (scrimColor != 0) {
                    color = scrimColor;
                }

                window.setStatusBarColor(color);
                window.setNavigationBarColor(color);
            }
        });

        updateDrawerLayout();
    }

    private void initBottomTabs() {
        mTabLayout = findViewById(R.id.ten_maindraweractivity_tabs);
        mTabLayout.setTabMode(TabLayout.SESL_MODE_WEIGHT_AUTO);
        for (String s: FragmentsUtils.getRCFragmentsTitles()) {
            mTabLayout.addTab(mTabLayout.newTab().setText(s));
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = tab.getPosition();
                mBottomTabManager.setTabPosition(tabPosition);
                setCurrentBottomTabItem();
            }

            public void onTabUnselected(TabLayout.Tab tab) {
                //if (mToolbarLayout != null) mToolbarLayout.dismissMoreMenuPopupWindow();
            }

            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    //
    // Drawer
    //
    private int getDrawerWidth() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        int displayWidth = p.x;
        float density = getResources().getDisplayMetrics().density;
        float dpi = (float) displayWidth / density;

        double widthRate;
        if (dpi >= 1920.0F) {
            widthRate = 0.22D;
        } else if (dpi >= 960.0F && dpi < 1920.0F) {
            widthRate = 0.2734D;
        } else if (dpi >= 600.0F && dpi < 960.0F) {
            widthRate = 0.46D;
        } else if (dpi >= 480.0F && dpi < 600.0F) {
            widthRate = 0.5983D;
        } else {
            widthRate = 0.844D;
        }

        return (int) ((double) displayWidth * widthRate);
    }

    private boolean isDrawerOpen() {
        if (mDrawerLayout != null) {
            return mDrawerLayout.isDrawerOpen(GravityCompat.START);
        } else {
            return false;
        }
    }

    private void openDrawer() {
        if (!isDrawerOpen()) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private boolean closeDrawer() {
        return closeDrawer(true);
    }

    private boolean closeDrawer(boolean animate) {
        if (isDrawerOpen()) {
            mDrawerLayout.closeDrawer(GravityCompat.START, animate);
            return true;
        } else {
            return false;
        }
    }

    private void updateDrawerLayout() {
        DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) mHomeDrawerContainer.getLayoutParams();
        int drawerWidth = getDrawerWidth();
        lp.width = drawerWidth;
        if (isDrawerOpen()) {
            if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                mHomeMainParent.setTranslationX((float) (-drawerWidth));
            } else {
                mHomeMainParent.setTranslationX((float) drawerWidth);
            }

            mHomeDrawerDim.setAlpha(1.0f);

            mDrawerLayout.invalidate();
        } else {
            mHomeMainParent.setTranslationX(0.0f);
            mHomeDrawerDim.setAlpha(0.0f);
        }
    }

    //
    // BottomTab
    //
    private void setCurrentBottomTabItem() {
        if (mTabLayout.isEnabled()) {
            int tabPosition = mBottomTabManager.getCurrentTab();
            TabLayout.Tab tab = mTabLayout.getTabAt(tabPosition);
            if (tab != null) {
                tab.select();
                setFragment(true, tabPosition);
            }
        }
    }

    private void setFragment(boolean isRCFragment, int tabPosition) {
        String tabTag = isRCFragment ? FragmentsUtils.getRCFragmentsTags()[tabPosition] : FragmentsUtils.getHomeFragmentsTag()[tabPosition];
        Class tabClass = isRCFragment ? FragmentsUtils.getRCFragmentsClasses()[tabPosition] : FragmentsUtils.getHomeFragmentClass()[tabPosition];

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag(tabTag);
        if (mInflatedFragment != null) {
            transaction.hide(mInflatedFragment);
        }
        if (fragment != null) {
            mInflatedFragment = (Fragment) fragment;
            transaction.show(fragment);
        } else {
            try {
                mInflatedFragment = (Fragment) tabClass.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            transaction.add(R.id.ten_maindraweractivity_content_container, mInflatedFragment, tabTag);
        }
        transaction.commit();
    }

    //
    // Adapter methods
    //
    public MainDrawerTabManager getDrawerTabManager() {
        return mDrawerTabManager;
    }

    public void onDrawerItemChanged(int newItem) {
        onDrawerItemChanged(newItem, false);
    }

    public void onDrawerItemChanged(int newItem, boolean force) {
        if (mDrawerTabManager.getPreviousTab() != newItem || force) {
            switch (newItem){
                case HOME_TAB:
                    mToolbarLayout.setTitle(getString(R.string.ten_tentoolbox), getString(R.string.ten_tenhome));
                    mToolbarLayout.setSubtitle(getString(R.string.ten_home_main_appbar_subtitle));
                    mFooter.setVisibility(View.GONE);
                    setFragment(false, 0);
                    closeDrawer();
                    break;
                case RC_TAB:
                    mToolbarLayout.setTitle(getString(R.string.ten_tentoolbox), getString(R.string.ten_tensettings));
                    mToolbarLayout.setSubtitle(getString(R.string.ten_rc_main_appbar_subtitle));
                    mFooter.setVisibility(View.VISIBLE);
                    setCurrentBottomTabItem();
                    closeDrawer();
                    break;
                case OTA_TAB:
                    startActivity(new Intent(this, OTAMainActivity.class));
                    break;
                case ADDONS_TAB:
                    mToolbarLayout.setTitle(getString(R.string.ten_tentoolbox), getString(R.string.ten_tenworkshop));
                    mToolbarLayout.setSubtitle(getString(R.string.ten_workshop_main_appbar_subtitle));
                    mHomeFragmentContainer.setBackgroundColor(getColor(R.color.sesl4_round_and_bgcolor));
                    mFooter.setVisibility(View.GONE);
                    setFragment(false, 1);
                    closeDrawer();
                    break;
            }
        } else
            closeDrawer();
    }

}