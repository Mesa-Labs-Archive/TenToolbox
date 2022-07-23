package com.mesalabs.ten.core.update.download;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mesalabs.ten.core.utils.LogUtils;
import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.TenToolboxApp;
import com.mesalabs.ten.core.update.content.GenericFileProvider;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import de.dlyt.yanndroid.oneui.dialog.ProgressDialog;

/*
 * 십 Toolbox
 *
 * Coded by BlackMesa123 @2021
 * Code snippets by MatthewBooth.
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

public class AppDownload extends AsyncTask<Void, Integer, String> {
    private final String TAG = "AppDownload";

    private Context mContext;
    private DownloadManager mDownloadManager;
    private long mDownloadID;
    private ProgressDialog mLoadingDialog;
    private boolean mIsRunning = true;

    public AppDownload(Context context, DownloadManager downloadManager, long downloadID) {
        mContext = context;
        mDownloadManager = downloadManager;
        mDownloadID = downloadID;
    }

    @Override
    protected void onPreExecute() {
        mLoadingDialog = new ProgressDialog(mContext);
        mLoadingDialog.setIndeterminate(false);
        mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setTitle(mContext.getString(R.string.ten_pleasewait));
        mLoadingDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, mContext.getString(R.string.sesl_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                downloadManager.remove(mDownloadID);
            }
        });
        mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                downloadManager.remove(mDownloadID);
            }
        });
        mLoadingDialog.show();
    }

    @Override
    protected void onCancelled() {
        mIsRunning = false;
    }

    @Override
    protected String doInBackground(Void... params) {
        String downloadFileUrl = "";
        int previousValue = 0;

        while(mIsRunning) {
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(mDownloadID);

            Cursor cursor = mDownloadManager.query(q);
            cursor.moveToFirst();

            try {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    downloadFileUrl = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    mIsRunning = false;
                } else if (status == DownloadManager.STATUS_FAILED) {
                    mIsRunning = false;
                } else if (status != DownloadManager.STATUS_RUNNING) {
                    mIsRunning = false;
                }

                final int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                final int bytesInTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                final int progressPercent = (int) ((bytesDownloaded * 100L) / bytesInTotal);

                if (progressPercent != previousValue) {
                    // Only publish every 1%, to reduce the amount of work being done.
                    publishProgress(progressPercent, bytesDownloaded, bytesInTotal);
                    previousValue = progressPercent;
                }
            } catch (CursorIndexOutOfBoundsException | ArithmeticException e) {
                LogUtils.e(TAG, e.toString());
                mIsRunning = false;
            }
            cursor.close();
        }

        mLoadingDialog.dismiss();
        return downloadFileUrl;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        LogUtils.d(TAG, "Updating Progress - " + progress[0] + "%");
        mLoadingDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null || result.isEmpty()) {
            Toast.makeText(mContext, R.string.ten_download_failed, Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(Intent.ACTION_VIEW);

        File file;
        try {
            file = new File(new URI(result));
        } catch (URISyntaxException e) {
            LogUtils.e(TAG, e.toString());
            return;
        }
        Uri data = GenericFileProvider.getUriForFile(mContext, TenToolboxApp.getAppPackageName() +".provider", file);

        intent.setDataAndType(data, "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }
}
