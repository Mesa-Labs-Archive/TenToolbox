package com.mesalabs.ten.toolbox.fragment.drawer;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.reflect.view.SeslViewReflector;

import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.activity.DrawerMainActivity;
import com.mesalabs.ten.toolbox.activity.settings.SettingsActivity;
import com.mesalabs.ten.toolbox.ui.drawer.MainDrawerListAdapter;
import com.mesalabs.ten.toolbox.ui.drawer.MainDrawerListViewModel;

import java.util.ArrayList;
import java.util.List;

import de.dlyt.yanndroid.oneui.sesl.recyclerview.LinearLayoutManager;
import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;
import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.oneui.view.Tooltip;

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

public class MainDrawerFragment extends Fragment {
    private boolean mAppUpdateAvailable = false;

    private TypedArray mDrawerIcons;
    private String [] mDrawerTitles;

    private Context mContext;
    private View mRootView;
    private ConstraintLayout mParentView;
    private ImageButton mSettingsButton;
    private TextView mSettingsBadge;
    private RecyclerView mListView;

    public static MainDrawerFragment newInstance() {
        return new MainDrawerFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.ten_toolbox_layout_main_drawer, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParentView = mRootView.findViewById(R.id.ten_maindrawer_parent);
        mSettingsButton = mRootView.findViewById(R.id.ten_maindrawer_settings_button);
        mSettingsBadge = mRootView.findViewById(R.id.ten_maindrawer_settings_badge);
        mListView = mRootView.findViewById(R.id.ten_maindrawer_recyclerview);

        if (mParentView != null) {
            mParentView.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.ten_drawer_background));
        }
        if (mSettingsButton != null) {
            Tooltip.setTooltipText(mSettingsButton, getResources().getString(R.string.action_settings));
            SeslViewReflector.semSetHoverPopupType(mSettingsButton, 1 /* SemHoverPopupWindow.TYPE_TOOLTIP */);
            mSettingsButton.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    startActivity(new Intent(mContext, SettingsActivity.class));
                }
            });
        }
        if (mSettingsBadge != null) {
            mSettingsBadge.setVisibility(mAppUpdateAvailable ? View.VISIBLE : View.GONE);
            mSettingsBadge.setText(mAppUpdateAvailable ? "N" : "");
        }
        if (mListView != null) {
            initListView();
        }
    }

    private void initListView() {
        mDrawerIcons = getResources().obtainTypedArray(R.array.ten_toolbox_main_drawer_icons);
        mDrawerTitles = getResources().getStringArray(R.array.ten_toolbox_main_drawer_titles);
        MainDrawerListAdapter adapter = new MainDrawerListAdapter((DrawerMainActivity) getActivity(), getHomeDrawerList());
        mListView.setLayoutManager(new LinearLayoutManager(mContext));
        mListView.setAdapter(adapter);
        mListView.seslSetLastRoundedCorner(false);
    }

    public void setAppUpdateAvailable(boolean available) {
        mAppUpdateAvailable = available;

        if (mSettingsBadge != null) {
            mSettingsBadge.setVisibility(mAppUpdateAvailable ? View.VISIBLE : View.GONE);
            mSettingsBadge.setText(mAppUpdateAvailable ? "N" : "");
        }
    }

    private List<MainDrawerListViewModel> getHomeDrawerList() {
        List<MainDrawerListViewModel> modelList = new ArrayList<>();

        for (int i = 0; i < mDrawerTitles.length; i++) {
            modelList.add(new MainDrawerListViewModel(!mDrawerTitles[i].isEmpty(),
                    getResources().getDrawable(mDrawerIcons.getResourceId(i, R.drawable.ten_null), mContext.getTheme()),
                    mDrawerTitles[i]));
        }

        return modelList;
    }

}
