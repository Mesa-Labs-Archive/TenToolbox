package com.mesalabs.ten.core.ui.creditspage;

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

public class CreditsListViewModel {
    private boolean mIsItem;
    private Drawable mLibIcon;
    private String mLibName;
    private String mLibDescription;
    private String mLibWebLink;

    public CreditsListViewModel() {
        mIsItem = false;
        mLibIcon = null;
        mLibName = "";
        mLibDescription = "";
        mLibWebLink = "";
    }

    public CreditsListViewModel(Drawable icon, String name, String desc, String webLink) {
        mIsItem = true;
        mLibIcon = icon;
        mLibName = name;
        mLibDescription = desc;
        mLibWebLink = webLink;
    }

    public boolean getIsItem() {
        return mIsItem;
    }

    public Drawable getLibIcon() {
        return mLibIcon;
    }

    public String getLibName() {
        return mLibName;
    }

    public String getLibDescription() {
        return mLibDescription;
    }

    public String getLibWebLink() {
        return mLibWebLink;
    }

}
