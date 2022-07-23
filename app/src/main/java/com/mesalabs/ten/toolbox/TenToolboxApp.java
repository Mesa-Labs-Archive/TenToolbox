package com.mesalabs.ten.toolbox;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.mesalabs.ten.core.utils.PreferencesUtils;
import com.mesalabs.ten.update.ota.ROMUpdate;

import java.util.List;

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

public class TenToolboxApp extends Application implements LifecycleObserver {
    private static Context sAppContext;
    private static boolean sIsInBackground;
    private static ROMUpdate.Download sROMUpdateDownload;



    public static ROMUpdate.Download getROMUpdateDwn() {
        return sROMUpdateDownload;
    }

    public static void setROMUpdateDwn(ROMUpdate.Download rud) {
        sROMUpdateDownload = rud;
    }









    public static Context getAppContext() {
        return sAppContext;
    }

    public static String getAppName() {
        return sAppContext.getString(R.string.ten_tentoolbox);
    }

    public static String getAppPackageName() {
        return BuildConfig.APPLICATION_ID;
    }

    public static String getAppVersionString() {
        return BuildConfig.VERSION_NAME;
    }

    public static int getAppVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    public static boolean isAppInBackground()  {
        return sIsInBackground;
    }

    public static boolean isDebugBuild()  {
        return BuildConfig.DEBUG;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this.getApplicationContext();

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        if (PreferencesUtils.getOTAMainNotificationChannelName().equals("")) {
            createOTAMainNotificationChannel();
            createOTAMinorNotificationChannel();
        }
    }

    public static void createOTAMainNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) sAppContext.getSystemService(Context.NOTIFICATION_SERVICE);

        int randomId = (int) (Math.random() * 100 + 1);
        while (PreferencesUtils.getOTAMainNotificationChannelName().contains(Integer.toString(randomId))) {
            randomId = (int) (Math.random() * 100 + 1);
        }

        notificationManager.deleteNotificationChannel(PreferencesUtils.getOTAMainNotificationChannelName());

        PreferencesUtils.setOTAMainNotificationChannelName("ten_update_ota_notification_channel_main" + "_" + randomId);
        NotificationChannel notiMainChannel = new NotificationChannel(PreferencesUtils.getOTAMainNotificationChannelName(), sAppContext.getString(R.string.ten_ota_notification_channel_main_name), NotificationManager.IMPORTANCE_HIGH);
        notiMainChannel.setLightColor(sAppContext.getColor(R.color.ten_tentoolbox_colorprimarydark));
        notiMainChannel.enableLights(true);
        notiMainChannel.setSound(Uri.parse(PreferencesUtils.getBgServiceNotificationSound()), new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build());
        notiMainChannel.enableVibration(true);

        notificationManager.createNotificationChannel(notiMainChannel);
    }

    private void createOTAMinorNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) sAppContext.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notiDwnlChannel = new NotificationChannel("mesa_tenupdate_notichannel_dwnl", sAppContext.getString(R.string.ten_ota_notification_channel_download_name), NotificationManager.IMPORTANCE_DEFAULT);
        notiDwnlChannel.setSound(null, null);

        notificationManager.createNotificationChannel(notiDwnlChannel);
    }

    public static boolean isOTANotificationsSilent() {
        NotificationManager notificationManager = (NotificationManager) sAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!notificationManager.areNotificationsEnabled()) {
            return false;
        }
        List<NotificationChannel> channels = notificationManager.getNotificationChannels();
        for (NotificationChannel channel : channels) {
            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE || channel.getImportance() == NotificationManager.IMPORTANCE_LOW) {
                return false;
            }
        }
        return true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        sIsInBackground = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        sIsInBackground = true;
    }

}
