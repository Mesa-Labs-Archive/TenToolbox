package com.mesalabs.ten.toolbox.ui.help;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.mesalabs.ten.toolbox.R;

import java.util.List;

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

public class HelpViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<HelpViewPagerModel> mItemList;
    private View mRootView;

    public HelpViewPagerAdapter(Context context, List<HelpViewPagerModel> list) {
        mContext = context;
        mItemList = list;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final HelpViewPagerModel pageInfo = mItemList.get(position % mItemList.size());

        if (pageInfo == null) {
            return null;
        }

        mRootView = LayoutInflater.from(mContext).inflate(R.layout.ten_toolbox_view_help_viewpager_item_layout, container, false);

        ImageView image = mRootView.findViewById(R.id.ten_help_item_image);
        image.setImageDrawable(pageInfo.getImage());

        TextView titleTextView = mRootView.findViewById(R.id.ten_help_item_title);
        titleTextView.setText(pageInfo.getTitle());

        TextView descTextView = mRootView.findViewById(R.id.ten_help_item_description);
        descTextView.setText(pageInfo.getDescription());

        container.addView(mRootView, 0);
        return mRootView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}
