package com.mesalabs.ten.core.update.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.mesalabs.ten.core.update.data.AppData;
import com.mesalabs.ten.core.update.download.AppDownload;
import com.mesalabs.ten.core.update.tasks.AppXMLParser;
import com.mesalabs.ten.core.utils.LogUtils;
import com.mesalabs.ten.toolbox.TenToolboxApp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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

public class AppUpdateUtils {
    private static final String TAG = "AppUpdateUtils";
    private static final String DOWNLOAD_DIR = "updates";
    private static final String UPDATE_XML = "https://gitlab.com/BlackMesa123/otatest/-/raw/master/testmanifest.xml";

    public static final int STATE_NO_UPDATES = 1;
    public static final int STATE_NEW_VERSION_AVAILABLE = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_CHECKING = 4;

    private Activity mActivity;
    private String mAppPackageName;
    private AppUpdateUtils.StubListener mStubListener;
    private AppData mAppData;
    private boolean mNewUpdateAvailable = false;

    public AppUpdateUtils(Activity activity, String appPackageName, AppUpdateUtils.StubListener stubListener) {
        mActivity = activity;
        mAppPackageName = appPackageName;
        mStubListener = stubListener;
    }

    public void checkUpdates() {
        new LoadAppManifest(mActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, UPDATE_XML);
    }

    public void installUpdate() {
        if (mNewUpdateAvailable) {
            String fileName = mAppPackageName + "-" + mAppData.getVersionNumber();

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mAppData.getDownloadLink()));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            fileName = fileName + ".apk";
            request.setDestinationInExternalFilesDir(mActivity, DOWNLOAD_DIR, fileName);

            DownloadManager downloadManager = (DownloadManager) mActivity.getSystemService(Context.DOWNLOAD_SERVICE);
            long mDownloadID = downloadManager.enqueue(request);
            new AppDownload(mActivity, downloadManager, mDownloadID).execute();

            LogUtils.d(TAG, "Starting download with manager ID " + mDownloadID);
        } else {
            LogUtils.e(TAG, "installUpdate: mNewUpdateAvailable is false!!!");
        }
    }

    private void postCheckUpdates(ArrayList<AppData> appsDataArr) {
        int newStatus = STATE_ERROR;

        if (appsDataArr != null && !appsDataArr.isEmpty()) {
            int currentVer = TenToolboxApp.getAppVersionCode();
            int onlineVer = 0;

            for (int i = 0; i < appsDataArr.size(); i++) {
                if (appsDataArr.get(i).getPackageName().equals(mAppPackageName)) {
                    mAppData = appsDataArr.get(i);
                    onlineVer = mAppData.getVersionNumber();
                    break;
                }
            }

            if (mAppData != null) {
                mNewUpdateAvailable = currentVer < onlineVer;
                newStatus = mNewUpdateAvailable ? STATE_NEW_VERSION_AVAILABLE : STATE_NO_UPDATES;
            }
        }

        mStubListener.onUpdateCheckCompleted(newStatus);
    }


    public interface StubListener {
        void onUpdateCheckCompleted(int status);
    }

    class LoadAppManifest extends AsyncTask<Object, Void, ArrayList<AppData>> {
        public final String TAG = "LoadAppManifest";
        private static final String MANIFEST = "app_manifest.xml";

        private Context mContext;

        public LoadAppManifest(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute(){
            // Delete any existing manifest file before we attempt to download a new one
            File manifest = new File(mContext.getFilesDir().getPath(), MANIFEST);
            if(manifest.exists()) {
                manifest.delete();
            }
        }

        @Override
        protected ArrayList<AppData> doInBackground(Object... param) {

            try {
                InputStream input = null;

                URL url = new URL((String) param[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // download the file
                input = new BufferedInputStream(url.openStream());

                OutputStream output = mContext.openFileOutput(MANIFEST, Context.MODE_PRIVATE);

                byte data[] = new byte[1024];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                // file finished downloading, parse it!
                return AppXMLParser.parse(new File(mContext.getFilesDir(), MANIFEST));
            } catch (Exception e) {
                LogUtils.d(TAG, "Exception: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<AppData> result) {
            postCheckUpdates(result);
        }
    }
}
