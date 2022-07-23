package com.mesalabs.ten.core.ui.creditspage;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

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

public class CreditsPageListViewHolder extends RecyclerView.ViewHolder {
    private boolean mIsItem;

    private LinearLayout mParentView;
    private ShapeableImageView mLibIconView;
    private TextView mLibNameTextView;
    private TextView mLibSummaryTextView;

    CreditsPageListViewHolder(View itemView, int viewType) {
        super(itemView);

        mIsItem = viewType == 0;

        if (mIsItem)  {
            mParentView = (LinearLayout) itemView;
            mLibIconView = mParentView.findViewById(android.R.id.icon);
            mLibNameTextView = mParentView.findViewById(android.R.id.title);
            mLibSummaryTextView = mParentView.findViewById(android.R.id.summary);
        }
    }

    public void onBindViewHolder(CreditsListViewModel viewModel) {
        if (mIsItem)  {
            mLibIconView.setImageDrawable(viewModel.getLibIcon());
            mLibIconView.setShapeAppearanceModel(mLibIconView.getShapeAppearanceModel().toBuilder().setAllCorners(CornerFamily.ROUNDED, 60.0f).build());
            mLibNameTextView.setText(viewModel.getLibName());
            if (!viewModel.getLibDescription().isEmpty())  {
                mLibSummaryTextView.setVisibility(View.VISIBLE);
                mLibSummaryTextView.setText(viewModel.getLibDescription());
            }
        }
    }

    public boolean getIsItem() {
        return mIsItem;
    }

    public void setItemOnClickListener(View.OnClickListener ocl) {
        if (mIsItem)  {
            mParentView.setOnClickListener(ocl);
        }
    }

}
