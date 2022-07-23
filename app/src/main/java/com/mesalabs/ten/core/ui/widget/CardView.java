package com.mesalabs.ten.core.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mesalabs.ten.toolbox.R;

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

public class CardView extends LinearLayout {
    boolean mIsIconView;
    boolean mIsDividerViewVisible;
    private Context mContext;

    private FrameLayout mParentView;
    private LinearLayout mContainerView;
    private ImageView mIconImageView;
    private TextView mTitleTextView;
    private TextView mSummaryTextView;
    private View mDividerView;

    private int mIconColor;
    private Drawable mIconDrawable;
    private String mTitleText;
    private String mSummaryText;

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setStyleable(attrs);
        init();
    }

    private void setStyleable(AttributeSet attrs) {
        TypedArray obtainStyledAttributes = mContext.obtainStyledAttributes(attrs, R.styleable.CardView);
        mIconDrawable = obtainStyledAttributes.getDrawable(R.styleable.CardView_IconDrawable);
        mIconColor = obtainStyledAttributes.getColor(R.styleable.CardView_IconColor, -1);
        mTitleText = obtainStyledAttributes.getString(R.styleable.CardView_TitleText);
        mSummaryText = obtainStyledAttributes.getString(R.styleable.CardView_SummaryText);
        mIsIconView = mIconDrawable != null;
        mIsDividerViewVisible = obtainStyledAttributes.getBoolean(R.styleable.CardView_isDividerViewVisible, false);
        obtainStyledAttributes.recycle();
    }

    private void init() {
        removeAllViews();

        if (mIsIconView) {
            inflate(mContext, R.layout.ten_view_cardview_icon_item_layout, this);

            mIconImageView = findViewById(R.id.ten_cardview_icon);
            mIconImageView.setImageDrawable(mIconDrawable);
            if (mIconColor != -1)
                mIconImageView.getDrawable().setTint(mIconColor);
        } else {
            inflate(mContext, R.layout.ten_view_cardview_item_layout, this);
        }

        mParentView = findViewById(R.id.ten_cardview_main_container);

        mContainerView = findViewById(R.id.ten_cardview_container);

        mTitleTextView = findViewById(R.id.ten_cardview_title);
        mTitleTextView.setText(mTitleText);

        mSummaryTextView = findViewById(R.id.ten_cardview_summary);
        if (mSummaryText != null && !mSummaryText.isEmpty()) {
            mSummaryTextView.setText(mSummaryText);
            mSummaryTextView.setVisibility(View.VISIBLE);
        }

        mDividerView = findViewById(R.id.ten_cardview_divider);
        MarginLayoutParams lp = (MarginLayoutParams) mDividerView.getLayoutParams();
        lp.setMarginStart(mIsIconView ?
                getResources().getDimensionPixelSize(R.dimen.ten_cardview_divider_margin_start_end)
                        + getResources().getDimensionPixelSize(R.dimen.ten_cardview_icon_size)
                        + getResources().getDimensionPixelSize(R.dimen.ten_cardview_icon_margin_end)
                : getResources().getDimensionPixelSize(R.dimen.ten_cardview_divider_margin_start_end));
        lp.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.ten_cardview_divider_margin_start_end));
        mDividerView.setVisibility(mIsDividerViewVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setFocusable(enabled);
        setClickable(enabled);
        mParentView.setEnabled(enabled);
        mContainerView.setAlpha(enabled ? 1.0f : 0.4f);
    }

    public Drawable getIconDrawable() {
        return mIconDrawable;
    }

    public void setIconDrawable(Drawable d) {
        mIconDrawable = d;
        mIconImageView.setImageDrawable(mIconDrawable);
        init();
    }

    public int getIconColor() {
        return mIconColor;
    }

    public void setIconColor(int color) {
        mIconColor = color;
        mIconImageView.getDrawable().setTint(mIconColor);
    }

    public String getTitleText() {
        return mTitleText;
    }

    public void setTitleText(String title) {
        mTitleText = title;
        mTitleTextView.setText(mTitleText);
    }

    public String getSummaryText() {
        return mSummaryText;
    }

    public void setSummaryText(String text) {
        if (text == null)
            text = "";

        mSummaryText = text;
        mSummaryTextView.setText(mSummaryText);
        if (mSummaryText.isEmpty())
            mSummaryTextView.setVisibility(View.GONE);
        else
            mSummaryTextView.setVisibility(View.VISIBLE);
    }

    public void setDividerVisible(boolean visible) {
        mDividerView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
