package com.mesalabs.ten.core.base;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mesalabs.ten.toolbox.R;

import de.dlyt.yanndroid.oneui.layout.SwitchBarLayout;
import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.widget.SwitchBar;

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

public class SwitchBarFragmentActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    protected Fragment mInflatedFragment;
    protected SwitchBarLayout mSwitchBarLayout;
    protected SwitchBar mSwitchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ten_base_layout_switchbarfragmentactivity);

        mSwitchBarLayout = findViewById(R.id.ten_fragmentactivity_switchbarlayout);
        mSwitchBar = mSwitchBarLayout.getSwitchBar();

        initSwitchBarBehaviour();
        inflateFragment();
    }

    private void inflateFragment() {
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag("root");
        if (mInflatedFragment != null) {
            transaction.hide(mInflatedFragment);
        }
        if (fragment != null) {
            mInflatedFragment = fragment;
            transaction.show(fragment);
        } else {
            mInflatedFragment = getFragmentClass();
            transaction.add(R.id.ten_fragmentactivity_container, mInflatedFragment, "root");
        }
        transaction.commit();
        mFragmentManager.executePendingTransactions();
    }

    private void initSwitchBarBehaviour() {
        mSwitchBar.setChecked(getSwitchBarDefaultStatus());
        mSwitchBar.addOnSwitchChangeListener(getSwitchBarListener());
    }

    protected Fragment getFragmentClass() {
        return null;
    }

    protected SwitchBar.OnSwitchChangeListener getSwitchBarListener() {
        return null;
    }

    protected boolean getSwitchBarDefaultStatus() {
        return false;
    }
}
