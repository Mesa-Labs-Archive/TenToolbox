package com.mesalabs.ten.toolbox.ui.help;

import android.graphics.drawable.Drawable;

import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.activity.HelpActivity;

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

public class HelpPageUtils {
    public static Drawable[] getHelpPageImage(HelpActivity activity) {
        return new Drawable[] {
                activity.getDrawable(R.drawable.ten_help_preview_rc),
                activity.getDrawable(R.drawable.ten_help_preview_ota),
                activity.getDrawable(R.drawable.ten_help_preview_addons)};
    }

    public static String[] getHelpPageTitle(HelpActivity activity) {
        return new String[] {
                activity.getString(R.string.ten_tensettings),
                activity.getString(R.string.ten_tenupdate),
                activity.getString(R.string.ten_tenworkshop)};
    }

    public static String[] getHelpPageDescription(HelpActivity activity) {
        return new String[] {
                activity.getString(R.string.ten_help_page_rc_description),
                activity.getString(R.string.ten_help_page_ota_description),
                activity.getString(R.string.ten_help_page_addons_description)};
    }
}
