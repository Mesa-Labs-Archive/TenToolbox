package com.mesalabs.ten.update.fragment.home;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.update.activity.ChangelogActivity;
import com.mesalabs.ten.update.ui.widget.ChangelogView;
import com.mesalabs.ten.update.ui.widget.DownloadProgressView;

import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;

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

public class DownloadProgressFragment extends Fragment {
    private FragmentActivity mActivity;
    private Context mContext;

    private View mRootView;
    private LinearLayout mContainer;
    private DownloadProgressView downloadProgressView;
    private ChangelogView changelogView;
    private TextView preInstallWarningText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
        mContext = mActivity.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.ten_ota_fragment_download_otamain, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LayoutTransition itemLayoutTransition = new LayoutTransition();
        Animator scaleDown = ObjectAnimator.ofPropertyValuesHolder(mContainer, PropertyValuesHolder.ofFloat("alpha", 1, 0));
        Animator scaleUp = ObjectAnimator.ofPropertyValuesHolder(mContainer, PropertyValuesHolder.ofFloat("alpha", 0, 1));
        itemLayoutTransition.setAnimator(LayoutTransition.APPEARING, scaleUp);
        itemLayoutTransition.setDuration(LayoutTransition.APPEARING, 650);
        itemLayoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        itemLayoutTransition.setDuration(LayoutTransition.CHANGE_APPEARING, 150);
        itemLayoutTransition.setInterpolator(LayoutTransition.CHANGE_APPEARING, new FastOutSlowInInterpolator());
        itemLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING, scaleDown);
        itemLayoutTransition.setDuration(LayoutTransition.DISAPPEARING, 80);

        mContainer = mRootView.findViewById(R.id.ten_ota_mainactivity_download_container);
        mContainer.setLayoutTransition(itemLayoutTransition);

        downloadProgressView = mRootView.findViewById(R.id.ten_ota_mainactivity_download_dpv);

        changelogView = mRootView.findViewById(R.id.ten_ota_mainactivity_download_cv);
        changelogView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                startActivity(new Intent(mContext, ChangelogActivity.class));
            }
        });
        changelogView.start();

        preInstallWarningText = mRootView.findViewById(R.id.ten_ota_mainactivity_download_warning);
    }

    public DownloadProgressView getDownloadProgressView() {
        return downloadProgressView;
    }

    public TextView getPreInstallWarningTextView() {
        return preInstallWarningText;
    }
}
