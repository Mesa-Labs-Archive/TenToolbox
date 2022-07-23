package com.mesalabs.ten.update.ota.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.io.File;

import com.mesalabs.ten.core.utils.LogUtils;
import com.mesalabs.ten.core.utils.PreferencesUtils;
import com.mesalabs.ten.update.ota.ROMUpdate;
import com.mesalabs.ten.update.ota.utils.OTAConstants;
import com.mesalabs.ten.update.ota.utils.OTAGeneralUtils;

/*
 * 십 Update
 *
 * Coded by BlackMesa123 @2021
 * Code snippets by MatthewBooth.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

public class AppReceiver extends BroadcastReceiver {
    public final String TAG = "AppReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(OTAConstants.INTENT_MANIFEST_CHECK_BACKGROUND)) {
            LogUtils.d(TAG, "Receiving background check confirmation");

            boolean updateAvailable = PreferencesUtils.Download.getUpdateAvailability();

            OTAGeneralUtils.dismissNotifications(context);
            if (updateAvailable) {
                OTAGeneralUtils.setupUpdateAvailableNotification(context);
                OTAGeneralUtils.scheduleNotification(context, !PreferencesUtils.getBgServiceEnabled());
            }
        }

        if (action.equals(OTAConstants.INTENT_START_UPDATE_CHECK)) {
            LogUtils.d(TAG, "Update check started");
            ROMUpdate update = new ROMUpdate(context, null);
            update.checkUpdates(false);
        }

        if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals("android.intent.action.QUICKBOOT_POWERON")) {
            LogUtils.d(TAG, "Boot received");
            boolean rebootForInstall = PreferencesUtils.getRebootForInstall();
            boolean backgroundCheck = PreferencesUtils.getBgServiceEnabled();
            if (rebootForInstall) {
                LogUtils.d(TAG, "Starting update install check");
                File updateFile = new File(PreferencesUtils.ROM.getFullFilePathName(context));
                OTAGeneralUtils.setupDownloadInstallNotification(context, !updateFile.exists());
                updateFile.delete();
                PreferencesUtils.setRebootForInstall(false);
                PreferencesUtils.Download.clean();
            }
            if (backgroundCheck) {
                LogUtils.d(TAG, "Starting background check alarm");
                OTAGeneralUtils.scheduleNotification(context, true);
            }
        }
    }
}

