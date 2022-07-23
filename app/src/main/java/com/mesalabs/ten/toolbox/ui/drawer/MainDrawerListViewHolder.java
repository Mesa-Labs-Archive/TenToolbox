package com.mesalabs.ten.toolbox.ui.drawer;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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

public class MainDrawerListViewHolder extends RecyclerView.ViewHolder {
    public static final int LIST_ITEM = 0;
    public static final int DIVIDER = 1;

    private boolean mIsListItem;

    private FrameLayout mParentView;
    private ImageView mIcon;
    private TextView mTitle;

    MainDrawerListViewHolder(View itemView) {
        this(itemView, LIST_ITEM);
    }

    MainDrawerListViewHolder(View itemView, int viewType) {
        super(itemView);

        mIsListItem = viewType == LIST_ITEM;

        if (mIsListItem)  {
            mParentView = (FrameLayout) itemView;
            mIcon = mParentView.findViewById(android.R.id.icon);
            mTitle = mParentView.findViewById(android.R.id.title);
        }
    }

    public void onBindViewHolder(MainDrawerListViewModel viewModel) {
        if (mIsListItem)  {
            mIcon.setImageDrawable(viewModel.getIcon());
            mTitle.setText(viewModel.getText());
        }
    }

    public FrameLayout getItemView() {
        return mParentView;
    }

    public ImageView getIcon() {
        return mIcon;
    }

    public TextView getTitleText() {
        return mTitle;
    }

    public boolean isListItem() {
        return mIsListItem;
    }

    public void setItemOnClickListener(View.OnClickListener ocl) {
        if (mIsListItem)  {
            mParentView.setOnClickListener(ocl);
        }
    }

}
