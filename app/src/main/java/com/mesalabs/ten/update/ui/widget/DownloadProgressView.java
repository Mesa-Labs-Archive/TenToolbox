package com.mesalabs.ten.update.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mesalabs.ten.toolbox.R;

import pl.tajchert.waitingdots.DotsTextView;

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

public class DownloadProgressView extends FrameLayout {
    private Context mContext;

    private TextView mTitleTextView;
    private DotsTextView mDotsTextView;
    private ProgressBar mProgressBar;
    private TextView mDescTextView;

    private int mProgress = 0;

    public DownloadProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        removeAllViews();

        LayoutInflater.from(mContext).inflate(R.layout.ten_ota_view_downloadprogressview_layout, this);

        mTitleTextView = findViewById(R.id.ten_ota_downloadprogressview_progress_title);
        mDotsTextView = findViewById(R.id.ten_ota_downloadprogressview_progress_titledots);
        mProgressBar = findViewById(R.id.ten_ota_downloadprogressview_progress_bar);
        mDescTextView = findViewById(R.id.ten_ota_downloadprogressview_progress_body);

        setProgress(0);
    }

    public void setDownloadCompleteStatus() {
        mTitleTextView.setText(R.string.ten_ota_downloadprogressview_download_complete);
        if (mDotsTextView.isPlaying()) mDotsTextView.hideAndStop();
        mProgressBar.setAlpha(0.4f);
        mDescTextView.setVisibility(GONE);
    }

    public void setDownloadFailedStatus() {
        mTitleTextView.setText(R.string.ten_ota_downloadprogressview_download_failed);
        if (mDotsTextView.isPlaying()) mDotsTextView.hideAndStop();
        mProgressBar.getProgressDrawable().setColorFilter(mContext.getColor(R.color.sesl_error_color), android.graphics.PorterDuff.Mode.SRC_IN);
        mProgressBar.setAlpha(1.0f);
        mDescTextView.setVisibility(GONE);
    }

    public void setCheckingMD5Status() {
        mTitleTextView.setText(R.string.ten_ota_downloadprogressview_md5check);
        mProgressBar.setAlpha(0.4f);
        mDescTextView.setVisibility(GONE);
    }

    public void setPausedStatus(boolean paused) {
        mDescTextView.setVisibility(paused ? GONE : VISIBLE);
        mTitleTextView.setText(paused ? R.string.ten_ota_downloadprogressview_download_pause : R.string.ten_ota_downloadprogressview_downloading);
        if (paused) {
            if (mDotsTextView.isPlaying()) mDotsTextView.hideAndStop();
        } else {
            if (mDotsTextView.isHide()) mDotsTextView.showAndPlay();
        }
        mProgressBar.setAlpha(paused ? 0.4f : 1.0f);
        setTimeLeftText("");
    }

    public void setWaitingForNetworkStatus(boolean paused) {
        mDescTextView.setVisibility(paused ? GONE : VISIBLE);
        mTitleTextView.setText(paused ? R.string.ten_ota_downloadprogressview_waiting_for_network : R.string.ten_ota_downloadprogressview_downloading);
        mProgressBar.setAlpha(paused ? 0.4f : 1.0f);
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
        mTitleTextView.setText(R.string.ten_ota_downloadprogressview_downloading);
        if (mDotsTextView.isHide()) mDotsTextView.showAndPlay();
        mProgressBar.setProgress(mProgress, false);
    }

    public void setTimeLeftText(String string) {
        if (string != null && !string.isEmpty())
            mDescTextView.setText(getResources().getString(R.string.ten_ota_downloadprogressview_time_left, string));
        else
            mDescTextView.setText("");
    }
}
