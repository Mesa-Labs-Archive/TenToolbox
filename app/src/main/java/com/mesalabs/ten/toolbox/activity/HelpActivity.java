package com.mesalabs.ten.toolbox.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.SeslViewPager;

import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.ui.help.HelpPageUtils;
import com.mesalabs.ten.toolbox.ui.help.HelpViewPagerAdapter;
import com.mesalabs.ten.toolbox.ui.help.HelpViewPagerModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;

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

public class HelpActivity extends AppCompatActivity {
    private boolean mIsRTL;
    protected List<HelpViewPagerModel> mHelpPageInfos = new ArrayList();
    protected HelpViewPagerAdapter mAdapter;

    private Context mContext;
    private ToolbarLayout mToolbarLayout;
    private SeslViewPager mViewPager;
    private ImageView mPageLeftBtn;
    private TextView mPageNumber;
    private ImageView mPageRightBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten_toolbox_activity_help_layout);

        mContext = this;
        mIsRTL = getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        mHelpPageInfos.clear();

        mToolbarLayout = findViewById(R.id.ten_helpactivity_toolbarlayout);
        mToolbarLayout.setNavigationButtonOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                onBackPressed();
            }
        });
        mToolbarLayout.setNavigationButtonTooltip(getString(R.string.sesl_navigate_up));

        init();
        initViewPager();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        mIsRTL = newConfig.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        if (mAdapter != null) {
            int currentItem = mViewPager.getCurrentItem();
            mViewPager.setAdapter(mAdapter);
            mViewPager.setCurrentItem(currentItem);
        }
        mPageLeftBtn.setScaleX(mIsRTL ? -1.0f : 1.0f);
        mPageRightBtn.setScaleX(mIsRTL ? -1.0f : 1.0f);

        super.onConfigurationChanged(newConfig);
    }


    private void init() {
        mViewPager = findViewById(R.id.ten_helpactivity_viewpager);
        mPageLeftBtn = findViewById(R.id.ten_helpactivity_page_btn_left);
        mPageNumber = findViewById(R.id.ten_helpactivity_page_number);
        mPageRightBtn = findViewById(R.id.ten_helpactivity_page_btn_right);
    }

    private void initViewPager() {
        for (int i = 0; i < 3; i++) {
            mHelpPageInfos.add(new HelpViewPagerModel(HelpPageUtils.getHelpPageImage(this)[i],
                    HelpPageUtils.getHelpPageTitle(this)[i],
                    HelpPageUtils.getHelpPageDescription(this)[i]));
        }

        mViewPager.setSoundEffectsEnabled(false);
        mAdapter = new HelpViewPagerAdapter(mContext, mHelpPageInfos);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(new SeslViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) { }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                updatePageMark(position);
            }
        });
        updatePageMark(0);

        mPageLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItem = mViewPager.getCurrentItem();
                if (currentItem > 0) {
                    currentItem--;
                    mViewPager.setCurrentItem(currentItem);
                } else if (currentItem == 0) {
                    mViewPager.setCurrentItem(currentItem);
                }
            }
        });
        mPageRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItem = mViewPager.getCurrentItem() + 1;
                mViewPager.setCurrentItem(currentItem);
            }
        });
        if (mIsRTL) {
            mPageLeftBtn.setScaleX(-1.0f);
            mPageRightBtn.setScaleX(-1.0f);
        }
    }

    private void updatePageMark(int currentPage) {
        if (Locale.getDefault().getLanguage().startsWith("ar")) {
            mPageNumber.setText(String.format(Locale.getDefault(),
                    "%d",
                    Integer.valueOf(mHelpPageInfos.size())) + "/" + String.format(Locale.getDefault(),
                    "%d",
                    Integer.valueOf(currentPage + 1)));
        } else {
            mPageNumber.setText((currentPage + 1) + "/" + mHelpPageInfos.size());
        }

        if (currentPage == 0) {
            mPageLeftBtn.setVisibility(View.INVISIBLE);
        } else {
            mPageLeftBtn.setVisibility(View.VISIBLE);
        }
        if (currentPage + 1 == mHelpPageInfos.size()) {
            mPageRightBtn.setVisibility(View.INVISIBLE);
        } else {
            mPageRightBtn.setVisibility(View.VISIBLE);
        }
    }
}
