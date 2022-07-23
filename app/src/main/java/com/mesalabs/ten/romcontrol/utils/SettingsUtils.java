package com.mesalabs.ten.romcontrol.utils;

import android.content.ContentResolver;
import android.provider.Settings;

import com.mesalabs.ten.core.root.utils.RootUtils;

/*
 * 십 Settings
 *
 * Coded by BlackMesa123 @2021
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

public class SettingsUtils {
    private static final String TAG = "SettingsUtils";

    // bool
    public static boolean getGlobalBool(ContentResolver resolver, String key) {
        return getGlobalBool(resolver, key, false);
    }

    public static boolean getGlobalBool(ContentResolver resolver, String key, boolean value) {
        return intToBoolean(Settings.Global.getInt(resolver, key, value ? 1 : 0));
    }

    public static boolean getSecureBool(ContentResolver resolver, String key) {
        return getSecureBool(resolver, key, false);
    }

    public static boolean getSecureBool(ContentResolver resolver, String key, boolean value) {
        return intToBoolean(Settings.Secure.getInt(resolver, key, value ? 1 : 0));
    }

    public static boolean getSystemBool(ContentResolver resolver, String key) {
        return getSystemBool(resolver, key, false);
    }

    public static boolean getSystemBool(ContentResolver resolver, String key, boolean value) {
        return intToBoolean(Settings.System.getInt(resolver, key, value ? 1 : 0));
    }

    public static void putGlobalBool(ContentResolver resolver, String key, boolean value) {
        try {
            Settings.Global.putInt(resolver, key, value ? 1 : 0);
        } catch (IllegalArgumentException | SecurityException e) {
            int i = value ? 1 : 0;
            RootUtils.getSU().runCommand("settings put global " + key + i);
        }
    }

    public static void putSecureBool(ContentResolver resolver, String key, boolean value) {
        try {
            Settings.Secure.putInt(resolver, key, value ? 1 : 0);
        } catch (IllegalArgumentException | SecurityException e) {
            int i = value ? 1 : 0;
            RootUtils.getSU().runCommand("settings put secure " + key + i);
        }
    }

    public static void putSystemBool(ContentResolver resolver, String key, boolean value) {
        try {
            Settings.System.putInt(resolver, key, value ? 1 : 0);
        } catch (IllegalArgumentException | SecurityException e) {
            int i = value ? 1 : 0;
            RootUtils.getSU().runCommand("settings put system " + key + i);
        }
    }

    // float
    public static float getGlobalFloat(ContentResolver resolver, String key) {
        return getGlobalFloat(resolver, key, 0.0f);
    }

    public static float getGlobalFloat(ContentResolver resolver, String key, float value) {
        return Settings.Global.getFloat(resolver, key, value);
    }

    public static float getSecureFloat(ContentResolver resolver, String key) {
        return getSecureFloat(resolver, key, 0.0f);
    }

    public static float getSecureFloat(ContentResolver resolver, String key, float value) {
        return Settings.Secure.getFloat(resolver, key, value);
    }

    public static float getSystemFloat(ContentResolver resolver, String key) {
        return getSystemFloat(resolver, key, 0.0f);
    }

    public static float getSystemFloat(ContentResolver resolver, String key, float value) {
        return Settings.System.getFloat(resolver, key, value);
    }

    public static void putGlobalFloat(ContentResolver resolver, String key, float value) {
        try {
            Settings.Global.putFloat(resolver, key, value);
        } catch (IllegalArgumentException | SecurityException e) {
            RootUtils.getSU().runCommand("settings put global " + key + " " + value);
        }
    }

    public static void putSecureFloat(ContentResolver resolver, String key, float value) {
        try {
            Settings.Secure.putFloat(resolver, key, value);
        } catch (IllegalArgumentException | SecurityException e) {
            RootUtils.getSU().runCommand("settings put secure " + key + " " + value);
        }
    }

    public static void putSystemFloat(ContentResolver resolver, String key, float value) {
        try {
            Settings.System.putFloat(resolver, key, value);
        } catch (IllegalArgumentException | SecurityException e) {
            RootUtils.getSU().runCommand("settings put system " + key + " " + value);
        }
    }

    // int
    public static int getGlobalInt(ContentResolver resolver, String key) {
        return getGlobalInt(resolver, key, 0);
    }

    public static int getGlobalInt(ContentResolver resolver, String key, int value) {
        return Settings.Global.getInt(resolver, key, value);
    }

    public static int getSecureInt(ContentResolver resolver, String key) {
        return getSecureInt(resolver, key, 0);
    }

    public static int getSecureInt(ContentResolver resolver, String key, int value) {
        return Settings.Secure.getInt(resolver, key, value);
    }

    public static int getSystemInt(ContentResolver resolver, String key) {
        return getSystemInt(resolver, key, 0);
    }

    public static int getSystemInt(ContentResolver resolver, String key, int value) {
        return Settings.System.getInt(resolver, key, value);
    }

    public static void putGlobalInt(ContentResolver resolver, String key, int value) {
        try {
            Settings.Global.putInt(resolver, key, value);
        } catch (IllegalArgumentException | SecurityException e) {
            RootUtils.getSU().runCommand("settings put global " + key + " " + value);
        }
    }

    public static void putSecureInt(ContentResolver resolver, String key, int value) {
        try {
            Settings.Secure.putInt(resolver, key, value);
        } catch (IllegalArgumentException | SecurityException e) {
            RootUtils.getSU().runCommand("settings put secure " + key + " " + value);
        }
    }

    public static void putSystemInt(ContentResolver resolver, String key, int value) {
        try {
            Settings.System.putInt(resolver, key, value);
        } catch (IllegalArgumentException | SecurityException e) {
            RootUtils.getSU().runCommand("settings put system " + key + " " + value);
        }
    }

    // long
    public static long getGlobalLong(ContentResolver resolver, String key) {
        return getGlobalLong(resolver, key, 0L);
    }

    public static long getGlobalLong(ContentResolver resolver, String key, long value) {
        return Settings.Global.getLong(resolver, key, value);
    }

    public static long getSecureLong(ContentResolver resolver, String key) {
        return getSecureLong(resolver, key, 0L);
    }

    public static long getSecureLong(ContentResolver resolver, String key, long value) {
        return Settings.Secure.getLong(resolver, key, value);
    }

    public static long getSystemLong(ContentResolver resolver, String key) {
        return getSystemLong(resolver, key, 0L);
    }

    public static long getSystemLong(ContentResolver resolver, String key, long value) {
        return Settings.System.getLong(resolver, key, value);
    }

    public static void putGlobalLong(ContentResolver resolver, String key, long value) {
        try {
            Settings.Global.putLong(resolver, key, value);
        } catch (IllegalArgumentException | SecurityException e) {
            RootUtils.getSU().runCommand("settings put global " + key + " " + value);
        }
    }

    public static void putSecureLong(ContentResolver resolver, String key, long value) {
        try {
            Settings.Secure.putLong(resolver, key, value);
        } catch (IllegalArgumentException | SecurityException e) {
            RootUtils.getSU().runCommand("settings put secure " + key + " " + value);
        }
    }

    public static void putSystemLong(ContentResolver resolver, String key, long value) {
        try {
            Settings.System.putLong(resolver, key, value);
        } catch (IllegalArgumentException | SecurityException e) {
            RootUtils.getSU().runCommand("settings put system " + key + " " + value);
        }
    }

    // string
    public static String getGlobalString(ContentResolver resolver, String key) {
        return Settings.Global.getString(resolver, key);
    }

    public static String getSecureString(ContentResolver resolver, String key) {
        return Settings.Secure.getString(resolver, key);
    }

    public static String getSystemString(ContentResolver resolver, String key) {
        return Settings.System.getString(resolver, key);
    }

    public static void putGlobalString(ContentResolver resolver, String key, String value) {
        try {
            Settings.Global.putString(resolver, key, value);
        } catch (IllegalArgumentException | SecurityException e) {
            RootUtils.getSU().runCommand("settings put global " + key + " " + value);
        }
    }

    public static void putSecureString(ContentResolver resolver, String key, String value) {
        try {
            Settings.Secure.putString(resolver, key, value);
        } catch (IllegalArgumentException | SecurityException e) {
            RootUtils.getSU().runCommand("settings put secure " + key + " " + value);
        }
    }

    public static void putSystemString(ContentResolver resolver, String key, String value) {
        try {
            Settings.System.putString(resolver, key, value);
        } catch (IllegalArgumentException | SecurityException e) {
            RootUtils.getSU().runCommand("settings put system " + key + " " + value);
        }
    }

    private static boolean intToBoolean(int i) {
        return i == 1;
    }

}
