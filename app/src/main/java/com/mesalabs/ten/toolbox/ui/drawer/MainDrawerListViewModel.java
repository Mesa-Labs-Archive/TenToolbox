package com.mesalabs.ten.toolbox.ui.drawer;

import android.graphics.drawable.Drawable;

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

public class MainDrawerListViewModel {
    private boolean mIsItem;
    private Drawable mIcon;
    private String mText;

    public MainDrawerListViewModel(boolean isItem, Drawable icon, String text) {
        mIsItem = isItem;

        if (mIsItem) {
            mIcon = icon;
            mText = text;
        } else {
            mIcon = null;
            mText = "";
        }
    }

    public boolean getIsItem() {
        return mIsItem;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public String getText() {
        return mText;
    }

}