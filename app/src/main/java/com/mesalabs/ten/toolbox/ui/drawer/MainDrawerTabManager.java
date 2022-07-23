package com.mesalabs.ten.toolbox.ui.drawer;

import com.mesalabs.ten.core.utils.SharedPreferencesUtils;

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

public class MainDrawerTabManager {
    private static final String KEY = "ten_current_drawer_tab";

    private SharedPreferencesUtils sp;
    private int mSelectedTab = 0;
    private int mPreviousTab = mSelectedTab;

    public MainDrawerTabManager(String spName) {
        sp = SharedPreferencesUtils.getInstance(spName);
    }

    public int getCurrentTab() {
        return mSelectedTab;
    }

    public int getPreviousTab() {
        return mPreviousTab;
    }

    public void initTabPosition() {
        setTabPosition(getTabFromSharedPreferences());
    }

    public void setTabPosition(int position) {
        setTabPositionToSharedPreferences(position);
        mPreviousTab = mSelectedTab;
        mSelectedTab = position;
    }

    public int getTabFromSharedPreferences() {
        int position = sp.getInt(KEY, -1);
        if (position == -1) {
            return 0;
        }
        return position;
    }

    private void setTabPositionToSharedPreferences(int position) {
        sp.put(KEY, position);
    }
}
