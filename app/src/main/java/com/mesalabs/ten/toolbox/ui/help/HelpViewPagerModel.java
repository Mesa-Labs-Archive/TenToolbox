package com.mesalabs.ten.toolbox.ui.help;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

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

public class HelpViewPagerModel implements Serializable {
    private Drawable mImage;
    private String mTitle;
    private String mDescription;

    public HelpViewPagerModel(Drawable image, String title, String description) {
        mImage = image;
        mTitle = title;
        mDescription = description;
    }

    public Drawable getImage() {
        return mImage;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }
}
