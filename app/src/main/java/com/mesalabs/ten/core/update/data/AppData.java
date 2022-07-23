package com.mesalabs.ten.core.update.data;

/*
 * 십 Toolbox
 *
 * Coded by MatthewBooth.
 * All rights reserved to their respective owners.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * ULTRA-MEGA-PRIVATE SOURCE CODE. SHARING TO DEVKINGS TEAM
 * EXTERNALS IS PROHIBITED AND WILL BE PUNISHED WITH ANAL ABUSE.
 */

public class AppData {
    private String mTitle;
    private String mPackageName;
    private int mVersion;
    private String mDownloadLink;

    public void setTitle(String input) {
        mTitle = input;
    }

    public void setPackageName(String input) {
        mPackageName = input;
    }

    public void setVersionNumber(int input) {
        mVersion = input;
    }

    public void setDownloadLink(String input) {
        mDownloadLink = input;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public int getVersionNumber() {
        return mVersion;
    }

    public String getDownloadLink() {
        return mDownloadLink;
    }
}
