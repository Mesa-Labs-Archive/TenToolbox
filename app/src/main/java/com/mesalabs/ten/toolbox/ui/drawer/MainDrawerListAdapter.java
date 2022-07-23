package com.mesalabs.ten.toolbox.ui.drawer;

import static com.mesalabs.ten.toolbox.ui.drawer.MainDrawerListViewHolder.LIST_ITEM;
import static com.mesalabs.ten.toolbox.ui.drawer.MainDrawerListViewHolder.DIVIDER;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.activity.DrawerMainActivity;

import java.util.List;

import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;
import de.dlyt.yanndroid.oneui.view.RecyclerView;

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

public class MainDrawerListAdapter extends RecyclerView.Adapter<MainDrawerListViewHolder> {
    private int mCurrentItem;
    private DrawerMainActivity mActivity;
    private List<MainDrawerListViewModel> mModel;

    public MainDrawerListAdapter(DrawerMainActivity activity, List<MainDrawerListViewModel> model) {
        mActivity = activity;
        mModel = model;
        mCurrentItem = activity.getDrawerTabManager().getTabFromSharedPreferences();
    }

    @Override
    public int getItemCount() {
        return mModel.size();
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public int getItemViewType(final int position) {
        if (mModel.get(position).getIsItem()) return LIST_ITEM;
        return DIVIDER;
    }

    @Override
    public MainDrawerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int resId = 0;

        switch (viewType) {
            case LIST_ITEM:
                resId = R.layout.ten_toolbox_view_drawer_list_item_layout;
                break;
            case DIVIDER:
                resId = R.layout.ten_toolbox_view_drawer_divider_layout;
                break;
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
        return new MainDrawerListViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(MainDrawerListViewHolder holder, final int position) {
        holder.onBindViewHolder(mModel.get(position));
        if (holder.isListItem()) {
            holder.setItemOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    if (position != 3) {
                        notifyItemChanged(mCurrentItem);
                        mCurrentItem = position;
                        mActivity.getDrawerTabManager().setTabPosition(position);
                        notifyItemChanged(position);
                    }
                    mActivity.onDrawerItemChanged(position);
                }
            });

            setSelected(holder, mCurrentItem == position);
        }
    }

    private void setSelected(MainDrawerListViewHolder holder, boolean selected) {
        int normalColor = mActivity.getResources().getColor(R.color.ten_drawer_list_item_color, mActivity.getTheme());
        int selectedColor = mActivity.getResources().getColor(R.color.ten_drawer_list_item_selected_color, mActivity.getTheme());

        holder.getIcon().setImageTintList(ColorStateList.valueOf(selected ? selectedColor : normalColor));
        holder.getTitleText().setTextColor(selected ? selectedColor : normalColor);
        holder.getTitleText().setTypeface(Typeface.create(holder.getTitleText().getTypeface(), selected ? Typeface.BOLD : Typeface.NORMAL));
    }
}
