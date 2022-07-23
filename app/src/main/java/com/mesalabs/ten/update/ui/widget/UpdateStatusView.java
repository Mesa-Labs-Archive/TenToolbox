package com.mesalabs.ten.update.ui.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.update.ota.ROMUpdate;


/*
 * 십 Update
 *
 * Coded by BlackMesa123 @2021
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

public class UpdateStatusView extends LinearLayout {
    private Context mContext;
    private int mCheckingStatus = 0;

    private LinearLayout mContainerView;
    private TextView mTextView;
    private ImageView mIconView;
    private Drawable mForegroundDrawable;
    private Drawable mSrcDrawable;
    private int mDrawableColor;

    private String mText;

    private AlphaAnimation mFastFadeInAnim;
    private AlphaAnimation mFadeInAnim;
    private AlphaAnimation mFadeOutAnim;
    private ObjectAnimator mMoveToZeroY_TV;

    public UpdateStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        init();
    }

    private void init() {
        removeAllViews();

        LayoutInflater.from(mContext).inflate(R.layout.ten_ota_view_updatestatusview_layout, this);

        mContainerView = findViewById(R.id.ten_updatestatusview_container);
        mTextView = findViewById(R.id.ten_updatestatusview_text);
        mIconView = findViewById(R.id.ten_updatestatusview_icon);

        initAnimationFields();
    }

    private void initAnimationFields() {
        mFastFadeInAnim = new AlphaAnimation(0.0f, 1.0f);
        mFastFadeInAnim.setDuration(250);
        mFadeInAnim = new AlphaAnimation(0.0f, 1.0f);
        mFadeInAnim.setDuration(1000);
        mFadeOutAnim = new AlphaAnimation(1.0f, 0.0f);
        mFadeOutAnim.setDuration(500);

        mMoveToZeroY_TV = ObjectAnimator.ofFloat(mTextView, "translationY", 0.0f);
        mMoveToZeroY_TV.setDuration(1000);
    }

    public int getCheckingStatus() {
        return mCheckingStatus;
    }

    public void setUpdateStatus(int status) {
        mCheckingStatus = status;

        switch(status) {
            case ROMUpdate.STATE_DOWNLOADED:
                mText = getResources().getString(R.string.ten_ota_update_download_complete);
                mForegroundDrawable = null;
                mSrcDrawable = getResources().getDrawable(R.drawable.ten_ota_updatestatusview_dw_complete, mContext.getTheme());
                mDrawableColor = getResources().getColor(R.color.ten_tenupdate_colorprimary, mContext.getTheme());
                break;
            case ROMUpdate.STATE_NO_UPDATES:
                mText = getResources().getString(R.string.ten_ota_no_updates_available);
                mForegroundDrawable = null;
                mSrcDrawable = getResources().getDrawable(R.drawable.ten_ota_updatestatusview_no_updates, mContext.getTheme());
                mDrawableColor = getResources().getColor(R.color.ten_tenupdate_colorprimary, mContext.getTheme());
                break;
            case ROMUpdate.STATE_NEW_VERSION_AVAILABLE:
                mText = getResources().getString(R.string.ten_ota_new_update_available);
                mForegroundDrawable = getResources().getDrawable(R.drawable.ten_ota_updatestatusview_new_update_bottom, mContext.getTheme());
                mSrcDrawable = getResources().getDrawable(R.drawable.ten_ota_updatestatusview_new_update, mContext.getTheme());
                mDrawableColor = getResources().getColor(R.color.ten_tenupdate_colorprimary, mContext.getTheme());
                break;
            case ROMUpdate.STATE_ERROR:
            default:
                mText = getResources().getString(R.string.ten_ota_updates_check_error);
                mForegroundDrawable = null;
                mSrcDrawable = getResources().getDrawable(R.drawable.ten_ota_updatestatusview_error, mContext.getTheme());
                mDrawableColor = getResources().getColor(R.color.sesl_error_color, mContext.getTheme());
                break;
        }
        setText(mText);

        if (mSrcDrawable == null) {
            disableIconView();
        } else {
            disableProgress();
        }
    }

    public void setText(String text) {
        mText = text;
        mTextView.setText(mText);
        mTextView.startAnimation(mFadeInAnim);
        if (mTextView.getLineCount() <= 1) {
            mTextView.setTranslationY(16.0f);
            mMoveToZeroY_TV.start();
        }
    }

    public void start(int status) {
        mCheckingStatus = status;

        if (status == ROMUpdate.STATE_DOWNLOADED) {
            mText = getResources().getString(R.string.ten_ota_update_download_complete);
            mForegroundDrawable = null;
            mSrcDrawable = getResources().getDrawable(R.drawable.ten_ota_updatestatusview_dw_complete, mContext.getTheme());
            mDrawableColor = getResources().getColor(R.color.ten_tenupdate_colorprimary, mContext.getTheme());

            mContainerView.setVisibility(View.VISIBLE);
            mIconView.setImageDrawable(mSrcDrawable);
            mIconView.setColorFilter(mDrawableColor, PorterDuff.Mode.SRC_IN);
        } else {
            mText = "";
            mForegroundDrawable = null;
            mSrcDrawable = null;
            mDrawableColor = 0;

            mContainerView.setVisibility(View.GONE);
        }

        setText(mText);
    }

    private void disableIconView() {
        mContainerView.setVisibility(View.GONE);
        mContainerView.setAlpha(0.0f);
    }

    private void disableProgress() {
        //mProgress.setVisibility(View.GONE);
        mContainerView.setAlpha(1.0f);
        mContainerView.setVisibility(View.VISIBLE);

        mIconView.setForeground(mForegroundDrawable);
        mIconView.setImageDrawable(mSrcDrawable);
        mIconView.setColorFilter(mDrawableColor, PorterDuff.Mode.SRC_IN);

        switch(mCheckingStatus) {
            case ROMUpdate.STATE_NO_UPDATES:
            case ROMUpdate.STATE_NEW_VERSION_AVAILABLE:
                mIconView.startAnimation(mFastFadeInAnim);
                Animatable animatable = (Animatable) mIconView.getDrawable();
                animatable.start();
                break;
            case ROMUpdate.STATE_DOWNLOADED:
            case ROMUpdate.STATE_ERROR:
            default:
                mIconView.startAnimation(mFadeInAnim);
                break;
        }
    }

}
