package com.mesalabs.ten.core.update.tasks;

import com.mesalabs.ten.core.update.data.AppData;
import com.mesalabs.ten.core.utils.LogUtils;

import org.xml.sax.helpers.DefaultHandler;
import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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

public class AppXMLParser extends DefaultHandler {
    private final static String TAG = "AppXMLParser";

    public static ArrayList<AppData> parse(File xmlFile) {
        ArrayList<AppData> addons = null;
        try {
            SAXParserFactory xmlReader = SAXParserFactory.newInstance();
            SAXParser saxParser = xmlReader.newSAXParser();
            AppSAXHandler saxHandler = new AppSAXHandler();
            saxParser.parse(xmlFile, saxHandler);
            addons = saxHandler.getApps();
        } catch (Exception ex) {
            LogUtils.d(TAG, "SAXXMLParser: parse() failed");
        }
        return addons;
    }
}
