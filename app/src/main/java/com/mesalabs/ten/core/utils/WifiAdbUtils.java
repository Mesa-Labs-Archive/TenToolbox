package com.mesalabs.ten.core.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.mesalabs.ten.core.root.SU;
import com.mesalabs.ten.core.root.utils.RootUtils;
import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.TenToolboxApp;

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

public class WifiAdbUtils {
    private static final String SET_CMD = "setprop service.adb.tcp.port ";
    private static final String START_ADB_CMD = "start adbd";
    private static final String STOP_ADB_CMD = "stop adbd";

    public static boolean isWifiAdbAllowed() {
        ConnectivityManager cm = (ConnectivityManager) TenToolboxApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
    }

    public static String getWifiAdbPreferenceSummary(Context context) {
        return isWifiAdbEnabled() ? getWifiIpAddress() + ":5555" : context.getString(R.string.ten_disabled);
    }

    public static String getWifiIpAddress() {
        WifiManager wifimanager = (WifiManager) TenToolboxApp.getAppContext().getSystemService(Context.WIFI_SERVICE);
        return Formatter.formatIpAddress(wifimanager.getDhcpInfo().ipAddress);
    }

    public static boolean isWifiAdbEnabled() {
        return PreferencesUtils.getIsWifiAdbEnabled();
    }

    public static void setWifiAdb(boolean enabled) {
        if (isWifiAdbEnabled() != enabled)  {
            String adbPort = enabled ? "5555" : "-1";

            boolean shouldUseRoot = false;
            try {
                String sharedUserId = TenToolboxApp.getAppContext()
                        .getPackageManager()
                        .getPackageInfo(TenToolboxApp.getAppPackageName(), 0).sharedUserId;
                shouldUseRoot = sharedUserId == null || !sharedUserId.equals("android.uid.system");
            } catch (PackageManager.NameNotFoundException ignored) { }

            SU su = RootUtils.getSU(shouldUseRoot);
            su.runCommand(SET_CMD + adbPort);
            su.runCommand(STOP_ADB_CMD);
            su.runCommand(START_ADB_CMD);
            su.close();

            PreferencesUtils.setIsWifiAdbEnabled(enabled);
        }

        PreferencesUtils.setIsWifiAdbAllowed(isWifiAdbAllowed());
    }
}
