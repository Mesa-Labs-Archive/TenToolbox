package com.mesalabs.ten.core.update.tasks;

import com.mesalabs.ten.core.update.data.AppData;
import com.mesalabs.ten.core.utils.LogUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/*
 * 십 Toolbox
 *
 * Originally coded by MatthewBooth.
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

public class AppSAXHandler extends DefaultHandler {
    private final String TAG = "AppSAXHandler";

    private ArrayList<AppData> apps;
    private String tempVal;
    private AppData tempAddon;

    public AppSAXHandler() {
        apps = new ArrayList<AppData>();
    }

    public ArrayList<AppData> getApps() {
        return apps;
    }

    // Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // reset
        tempVal = "";
        if (qName.equalsIgnoreCase("app")) {
            // create a new instance of employee
            tempAddon = new AppData();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("app")) {
            // add it to the list
            apps.add(tempAddon);
        } else if (qName.equalsIgnoreCase("name")) {
            tempAddon.setTitle(tempVal);
            LogUtils.d(TAG, "Title = " + tempVal);
        } else if (qName.equalsIgnoreCase("package-name")) {
            tempAddon.setPackageName(tempVal);
            LogUtils.d(TAG, "Package Name = " + tempVal);
        } else if (qName.equalsIgnoreCase("version")) {
            tempAddon.setVersionNumber(Integer.parseInt(tempVal));
            LogUtils.d(TAG, "Version = " + tempVal);
        } else if (qName.equalsIgnoreCase("download-link")) {
            tempAddon.setDownloadLink(tempVal);
            LogUtils.d(TAG, "Download Link = " + tempVal);
        }
    }

}
