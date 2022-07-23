package com.mesalabs.ten.core.root.utils;

import android.content.Context;

import com.mesalabs.ten.core.root.SU;
import com.mesalabs.ten.core.utils.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
 * 십 Toolbox
 *
 * Coded by BlackMesa123 @2021
 * Original code by Willi Ye & AndreiLux
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

public class RootOperations {
    private static final String TAG = "SU.RootOperations";

    public static boolean existFile(String file) {
        return existFile(file, false);
    }

    public static boolean existFile(String file, boolean root) {
        return existFile(file, root ? RootUtils.getSU() : null);
    }

    public static boolean existFile(String file, SU su) {
        return su == null ? new File(file).exists() : new RootFile(file, su).exists();
    }

    public static String readAssetFile(Context context, String file) {
        InputStream input = null;
        BufferedReader buf = null;
        try {
            StringBuilder s = new StringBuilder();
            input = context.getAssets().open(file);
            buf = new BufferedReader(new InputStreamReader(input));

            String str;
            while ((str = buf.readLine()) != null) {
                s.append(str).append("\n");
            }
            return s.toString().trim();
        } catch (IOException e) {
            LogUtils.e(TAG, "Unable to read " + file);
        } finally {
            try {
                if (input != null) input.close();
                if (buf != null) buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String readFile(String file) {
        return readFile(file, false);
    }

    public static String readFile(String file, boolean root) {
        return readFile(file, root ? RootUtils.getSU() : null);
    }

    public static String readFile(String file, SU su) {
        if (su != null) {
            return new RootFile(file, su).readFile();
        }

        BufferedReader buf = null;
        try {
            buf = new BufferedReader(new FileReader(file));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = buf.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            return stringBuilder.toString().trim();
        } catch (IOException ignored) {
        } finally {
            try {
                if (buf != null) buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
