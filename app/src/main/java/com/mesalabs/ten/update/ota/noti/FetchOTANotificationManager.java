package com.mesalabs.ten.update.ota.noti;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.update.activity.OTAMainActivity;
import com.tonyodev.fetch2.DefaultFetchNotificationManager;
import com.tonyodev.fetch2.DownloadNotification;

import org.jetbrains.annotations.NotNull;

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

public abstract class FetchOTANotificationManager extends DefaultFetchNotificationManager {
    protected FetchOTANotificationManager(@NotNull Context context) {
        super(context);
    }

    @Override
    public void createNotificationChannels(@NotNull Context context, @NotNull NotificationManager notificationManager) {
        // created in TenToolboxApp.java
    }

    @NotNull
    @Override
    public String getChannelId(int channelId, @NotNull Context context) {
        return "mesa_tenupdate_notichannel_dwnl";
    }

    @NotNull
    private String getContentTitle(@NotNull Context context, @NotNull DownloadNotification downloadNotification) {
        if (downloadNotification.isFailed())
            return context.getString(R.string.ten_ota_notification_download_failed);
        else if (downloadNotification.isPaused())
            return context.getString(R.string.ten_ota_notification_download_paused);
        else
            return context.getString(R.string.ten_ota_notification_downloading);
    }

    private String getEtaText(Context context, long etaInMilliSeconds) {
        long seconds = (etaInMilliSeconds / 1000);
        long hours = (seconds / 3600);
        seconds -= (hours * 3600);
        long minutes = (seconds / 60);
        seconds -= (minutes * 60);

        if (hours > 0)
            return context.getString(R.string.ten_ota_notification_downloading_eta_hrs, hours, minutes, seconds);
        else if (minutes > 0)
            return context.getString(R.string.ten_ota_notification_downloading_eta_min, minutes, seconds);
        else if (seconds > 0)
            return context.getString(R.string.ten_ota_notification_downloading_eta_sec, seconds);
        else
            return "";
    }

    private int getSmallIcon(@NotNull DownloadNotification downloadNotification) {
        if (downloadNotification.isFailed())
            return R.drawable.ten_ota_ic_noti_error;
        else
            return R.drawable.ten_ota_ic_noti_download;
    }

    @NotNull
    @Override
    public String getSubtitleText(@NotNull Context context, @NotNull DownloadNotification downloadNotification) {
        if (downloadNotification.isActive())
            return getEtaText(context, downloadNotification.getEtaInMilliSeconds());
        else
            return  "";
    }

    @Override
    public boolean shouldCancelNotification(@NotNull DownloadNotification downloadNotification) {
        return downloadNotification.isCompleted();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void updateNotification(@NotNull NotificationCompat.Builder notificationBuilder, @NotNull DownloadNotification downloadNotification, @NotNull Context context) {
        Intent resultIntent = new Intent(context, OTAMainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(getSmallIcon(downloadNotification))
                .setColor(context.getColor(R.color.ten_tenupdate_colorsecondary))
                .setContentTitle(getContentTitle(context, downloadNotification))
                .setContentIntent(resultPendingIntent)
                .setOngoing(downloadNotification.isOnGoingNotification())
                .setGroup(String.valueOf(downloadNotification.getGroupId()))
                .setGroupSummary(false)
                .setTimeoutAfter(0L);

        if (downloadNotification.isFailed() || downloadNotification.isCompleted()) {
            notificationBuilder.setProgress(0, 0, false);
        } else {
            boolean progressIndeterminate = downloadNotification.getProgressIndeterminate();
            int maxProgress = downloadNotification.getProgressIndeterminate() ? 0 : 100;
            int progress = Math.max(downloadNotification.getProgress(), 0);
            notificationBuilder.setProgress(maxProgress, progress, progressIndeterminate);
        }

        if (downloadNotification.isDownloading()) {
                notificationBuilder.addAction(0,
                                context.getString(R.string.ten_ota_pause),
                                getActionPendingIntent(downloadNotification, DownloadNotification.ActionType.PAUSE));

        } else if (downloadNotification.isPaused()) {
            notificationBuilder.addAction(0,
                            context.getString(R.string.ten_ota_resume),
                            getActionPendingIntent(downloadNotification, DownloadNotification.ActionType.RESUME));
        }
    }
}
