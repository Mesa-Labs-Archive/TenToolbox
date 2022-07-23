package com.mesalabs.ten.core.utils;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.SystemClock;
import android.system.Os;
import android.system.StructUtsname;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import com.mesalabs.ten.core.root.utils.RootOperations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class SystemInfoUtils {
    private static final String TAG = "SystemInfoUtils";
    private static final String ROM_VERSION = "1.0";
    private static String mCPUNode;
    private static int mCPUOffSet;
    private static String mGPUNode;
    private static int mGPUOffSet;
    private static String mBatteryNode;

    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getBootloaderBuildVersion() {
        return Build.BOOTLOADER;
    }

    public static String getBuildNumber() {
        return (PropUtils.get("ro.build.id", "") + "." + Build.VERSION.INCREMENTAL);
    }

    public static String getDeviceNameFromBootloader() {
        String bootloader = getBootloaderBuildVersion();
        if (bootloader.startsWith("G950"))
            return "Galaxy S8";
        if (bootloader.startsWith("G955"))
            return "Galaxy S8+";
        if (bootloader.startsWith("N950"))
            return "Galaxy Note8";
        return "Unknown";
    }

    public static String getDeviceUpTime() {
        return DateUtils.formatElapsedTime(SystemClock.elapsedRealtime() / 1000);
    }

    public static String getFormattedHwTemp() {
        /*ensureTemperatureSysFs();

        boolean useFahrenheit = PreferencesUtils.getTempUnit() == 1;
        double cpuTemp = (double) Integer.parseInt(FileUtils.readFile(mCPUNode)) / mCPUOffSet;
        double gpuTemp = (double) Integer.parseInt(FileUtils.readFile(mGPUNode)) / mGPUOffSet;
        double batteryTemp = (double) Integer.parseInt(FileUtils.readFile(mBatteryNode)) / 10.0d;*/
        boolean useFahrenheit = PreferencesUtils.getTempUnit() == 1;
        double cpuTemp = 36d;
        double gpuTemp = 36d;
        double batteryTemp = 36d;

        if (useFahrenheit){
            cpuTemp = celsiusToFahrenheit(cpuTemp);
            gpuTemp = celsiusToFahrenheit(gpuTemp);
            batteryTemp = celsiusToFahrenheit(batteryTemp);
        }

        return "CPU: " + roundToTwoDecimals(cpuTemp) + (useFahrenheit ? " °F" : " °C") + System.lineSeparator()
                + "GPU: " + roundToTwoDecimals(gpuTemp) + (useFahrenheit ? " °F" : " °C") + System.lineSeparator()
                + "Battery: " + roundToTwoDecimals(batteryTemp) + (useFahrenheit ? " °F" : " °C");
    }

    public static String getKernelBuild() {
        StructUtsname uname = Os.uname();

        if (uname == null) {
            return null;
        }

        Matcher m = Pattern.compile("(#\\d+) (?:.*?)?((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)").matcher(uname.version);
        if (!m.matches()) {
            LogUtils.e(TAG, "Regex did not match on uname version " + uname.version);
            return null;
        }
        return uname.release + "\n" + m.group(1) + " " + m.group(2);
    }

    public static String getManufacturingDate() {
        String prop = PropUtils.get("ril.rfcal_date", "");
        if (prop.isEmpty()) {
            return "Unknown";
        }
        try {
            return DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "dMMMMyyyy"), new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(prop)).toString();
        } catch (ParseException e) {
            return prop;
        }
    }

    public static String getModemBuildVersion() {
        return PropUtils.get("gsm.version.baseband", "Unknown");
    }

    public static String getOneUIVersion() {
        int prop = PropUtils.getInt("ro.build.version.sep", 0);

        if (prop != 0) {
            int oneUIversion = prop - 90000;
            return oneUIversion / 10000 + "." + (oneUIversion % 10000) / 100;
        } else
            return "Unknown";
    }

    public static String getROMVersion() {
        return ROM_VERSION;
    }

    public static String getSecurityPatchVersion() {
        String patch = Build.VERSION.SECURITY_PATCH;
        if ("".equals(patch)) {
            return null;
        }
        try {
            return DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "dMMMMyyyy"), new SimpleDateFormat("yyyy-MM-dd").parse(patch)).toString();
        } catch (ParseException e) {
            return patch;
        }
    }

    // kang from https://github.com/corsicanu/hKtweaks/blob/master/app/src/main/java/com/hades/hKtweaks/utils/Utils.java
    public static double celsiusToFahrenheit(double celsius) {
        return (9d / 5d) * celsius + 32;
    }

    public static double roundToTwoDecimals(double val) {
        BigDecimal bd = new BigDecimal(val);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // kang from https://github.com/corsicanu/hKtweaks/blob/master/app/src/main/java/com/hades/hKtweaks/utils/kernel/cpu/Temperature.java
    private static void ensureTemperatureSysFs() {
        if (mCPUNode == null) {
            for (String node : new String[] {"/sys/class/thermal/thermal_zone"}) {
                for (int i = 0; i < 30; i++) {
                    if (RootOperations.existFile(node + i)) {
                        try {
                            String type = "/cdev0/type";
                            if (RootOperations.readFile(node + i + type).contains("gpu")) {
                                mGPUNode = node + i + "/temp";
                                mGPUOffSet = (int) Math.pow(10, (double) (RootOperations.readFile(mGPUNode).length() - (RootOperations.readFile(mGPUNode).length() - 3)));
                            } else if (RootOperations.readFile(node + i + type).contains("cpufreq-0")) {
                                mCPUNode = node + i + "/temp";
                                mCPUOffSet = (int) Math.pow(10, (double) (RootOperations.readFile(mCPUNode).length() - (RootOperations.readFile(mCPUNode).length() - 3)));
                            }
                        } catch (Exception e) {
                            LogUtils.e(TAG + ".ensureTemperatureSysFs", e.toString());
                            break;
                        }
                    }
                }
            }
            mBatteryNode = "/sys/class/power_supply/battery/temp";
        }
    }
}
