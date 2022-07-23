package com.mesalabs.ten.core.ui.creditspage;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.mesalabs.ten.toolbox.activity.aboutpage.CreditsActivity;

import de.dlyt.yanndroid.oneui.sesl.recyclerview.LinearLayoutManager;
import de.dlyt.yanndroid.oneui.sesl.utils.SeslRoundedCorner;
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

public class CreditsPageListItemDecoration extends RecyclerView.ItemDecoration {
    private CreditsActivity mListActivity;
    private SeslRoundedCorner mSeslListRoundedCorner;
    private SeslRoundedCorner mSeslRoundedCornerBottom;
    private Drawable mDivider;
    private int mDividerHeight;

    public CreditsPageListItemDecoration(CreditsActivity activity) {
        mListActivity = activity;

        mSeslListRoundedCorner = new SeslRoundedCorner(mListActivity.getContext(), true);
        mSeslListRoundedCorner.setRoundedCorners(3);

        mSeslRoundedCornerBottom = new SeslRoundedCorner(mListActivity.getContext(), true);
        mSeslRoundedCornerBottom.setRoundedCorners(12);
    }

    @Override
    public void seslOnDispatchDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        super.seslOnDispatchDraw(canvas, recyclerView, state);

        int childCount = getChildCount(recyclerView);
        int width = recyclerView.getWidth();

        // draw divider for each item
        for (int i = 0; i < childCount; i++) {
            View childAt = recyclerView.getChildAt(i);
            int y = ((int) childAt.getY()) + childAt.getHeight();

            if (mDivider != null) {
                mDivider.setBounds(0, y, width, mDividerHeight + y);
                mDivider.draw(canvas);
            }
        }

        mSeslListRoundedCorner.drawRoundedCorner(canvas);
        mSeslRoundedCornerBottom.drawRoundedCorner(recyclerView.getChildAt(childCount), canvas);
    }

    private int getChildCount(RecyclerView recyclerView) {
        final LinearLayoutManager layoutmanager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (!((CreditsPageListViewHolder) recyclerView.findViewHolderForLayoutPosition(layoutmanager.findLastVisibleItemPosition())).getIsItem()) {
            return recyclerView.getChildCount() - 2;
        } else {
            return recyclerView.getChildCount() - 1;
        }
    }

    public void setDivider(Drawable d) {
        mDivider = d;
        mDividerHeight = d.getIntrinsicHeight();
        mListActivity.getListView().invalidateItemDecorations();
    }

}
