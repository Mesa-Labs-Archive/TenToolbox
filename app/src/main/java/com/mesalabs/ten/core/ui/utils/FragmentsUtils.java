package com.mesalabs.ten.core.ui.utils;

import com.mesalabs.ten.romcontrol.fragment.SettingsTestFragment;
import com.mesalabs.ten.toolbox.fragment.home.HomePreferenceFragment;
import com.mesalabs.ten.update.fragment.home.DownloadProgressFragment;
import com.mesalabs.ten.update.fragment.home.MainCardsFragment;

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

public class FragmentsUtils {
    // Drawer
    public static String[] getDrawerFragmentsTag() {
        return new String[] {
                "info",
                "",
                "rc",
                "sammy",
                "",
                "add"};
    }

    // Home
    public static Class[] getHomeFragmentClass() {
        return new Class[] {
                HomePreferenceFragment.class,
                SettingsTestFragment.class};
    }

    public static String[] getHomeFragmentsTag() {
        return new String[] {
                "ten_fragment_home",
                "ten_fragment_addons"};
    }

    // RC
    public static String[] getRCFragmentsTitles() {
        return new String[] {
                "Frameworks",
                "ammelo",};
    }

    public static Class[] getRCFragmentsClasses() {
        return new Class[] {
                SettingsTestFragment.class,
                SettingsTestFragment.class};
    }

    public static String[] getRCFragmentsTags() {
        return new String[] {
                "ten_fragment_rc_fw",
                "ten_fragment_rc_melo"};
    }

    // OTA
    public static Class[] getOTAFragmentsClasses() {
        return new Class[] {
                // OTAMainActivity.MAIN_PAGE_FRAGMENT = 0
                MainCardsFragment.class,
                // OTAMainActivity.DOWNLOAD_PROGRESS_FRAGMENT = 1
                DownloadProgressFragment.class};
    }

    public static String[] getOTAFragmentsTags() {
        return new String[] {
                // OTAMainActivity.MAIN_PAGE_FRAGMENT = 0
                "ten_ota_fragment_maincards",
                // OTAMainActivity.DOWNLOAD_PROGRESS_FRAGMENT = 1
                "ten_ota_fragment_download"};
    }
}
