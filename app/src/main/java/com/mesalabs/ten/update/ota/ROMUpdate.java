package com.mesalabs.ten.update.ota;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.mesalabs.ten.core.utils.LogUtils;
import com.mesalabs.ten.core.utils.PreferencesUtils;
import com.mesalabs.ten.core.utils.TenCoreException;
import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.TenToolboxApp;
import com.mesalabs.ten.update.activity.OTAMainActivity;
import com.mesalabs.ten.update.ota.noti.FetchOTANotificationManager;
import com.mesalabs.ten.update.ota.tasks.ROMXMLParser;
import com.mesalabs.ten.update.ota.utils.OTAConstants;
import com.mesalabs.ten.update.ota.utils.OTAGeneralUtils;
import com.mesalabs.ten.update.ui.widget.DownloadProgressView;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.HttpUrlConnectionDownloader;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;
import com.tonyodev.fetch2core.Downloader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

public class ROMUpdate {
    public static final int STATE_NO_UPDATES = 1;
    public static final int STATE_NEW_VERSION_AVAILABLE = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_CHECKING = 4;
    public static final int STATE_DOWNLOADED = 5;

    private Context mContext;
    private StubListener mStubListener;
    private boolean mIsRunningInApp = true;
    private boolean mNewUpdateAvailable = false;

    public ROMUpdate(Context context, StubListener stubListener) {
        mContext = context;
        mStubListener = stubListener;
    }

    public void checkUpdates(boolean inApp) {
        if (!PreferencesUtils.Download.getDownloadFinished()) {
            PreferencesUtils.ROM.clean();
            mIsRunningInApp = inApp;
            new LoadUpdateManifest(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void postCheckUpdates() {
        int newStatus = STATE_ERROR;

        if (!PreferencesUtils.ROM.getRomName().equals("null")) {
            mNewUpdateAvailable = PreferencesUtils.Download.getUpdateAvailability();
            newStatus = mNewUpdateAvailable ? STATE_NEW_VERSION_AVAILABLE : STATE_NO_UPDATES;

            OTAGeneralUtils.dismissNotifications(mContext);

            if (!mIsRunningInApp) {
                if (mNewUpdateAvailable) {
                    OTAGeneralUtils.setupUpdateAvailableNotification(mContext);
                }
                OTAGeneralUtils.scheduleNotification(mContext, PreferencesUtils.getBgServiceEnabled());
            }
        }

        if (mStubListener != null)
            mStubListener.onUpdateCheckCompleted(newStatus);
    }


    public interface StubListener {
        void onUpdateCheckCompleted(int status);
    }


    class LoadUpdateManifest extends AsyncTask<Void, Void, Void> {
        private final String TAG = "LoadUpdateManifest";
        private static final String MANIFEST = "update_manifest.xml";

        private Context mContext;


        public LoadUpdateManifest(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            if (!mIsRunningInApp) {
                OTAGeneralUtils.dismissNotifications(mContext);
            }

            File manifest = new File(mContext.getFilesDir().getPath(), MANIFEST);
            if (manifest.exists()) {
                manifest.delete();
            }
        }

        @Override
        protected Void doInBackground(Void... v) {
            try {
                InputStream input = null;

                URL url = new URL(OTAConstants.OTA_MANIFEST_URL);
                URLConnection connection = url.openConnection();
                connection.connect();

                input = new BufferedInputStream(url.openStream());
                OutputStream output = mContext.openFileOutput(MANIFEST, Context.MODE_PRIVATE);

                byte[] data = new byte[1024];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                ROMXMLParser parser = new ROMXMLParser();
                parser.parse(new File(mContext.getFilesDir(), MANIFEST));
            } catch (Exception e) {
                LogUtils.d(TAG, "Exception: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Intent intent;
            if (!mIsRunningInApp) {
                intent = new Intent(OTAConstants.INTENT_MANIFEST_CHECK_BACKGROUND);
            } else {
                intent = new Intent(OTAConstants.INTENT_MANIFEST_LOADED);

            }

            mContext.sendBroadcast(intent);
            super.onPostExecute(result);

            postCheckUpdates();
        }
    }



    public static class Download {
        private final static String TAG = "ROMUpdate.Download";

        private OTAMainActivity mActivity;
        private Fetch mFetch;

        private boolean mIsDwnPaused;

        public Download(OTAMainActivity activity) {
            mActivity = activity;
        }

        public void switchActivity(OTAMainActivity newActivity) {
            mActivity = newActivity;
        }

        public void startDownload() {
            String url = PreferencesUtils.ROM.getDownloadUrl();
            String file = PreferencesUtils.ROM.getFullFilePathName(mActivity);

            PreferencesUtils.Download.setIsDownloadOnGoing(true);

            FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(mActivity)
                    .enableLogging(TenToolboxApp.isDebugBuild())
                    .setDownloadConcurrentLimit(1)
                    .setHttpDownloader(new HttpUrlConnectionDownloader(Downloader.FileDownloaderType.SEQUENTIAL))
                    .setNotificationManager(new FetchOTANotificationManager(mActivity) {
                        @NotNull
                        @Override
                        public Fetch getFetchInstanceForNamespace(@NotNull String s) {
                            return mFetch;
                        }
                    })
                    .build();
            mFetch = Fetch.Impl.getInstance(fetchConfiguration);


            final Request request = new Request(url, file);
            request.setDownloadOnEnqueue(true);
            request.setPriority(Priority.HIGH);
            request.setNetworkType(PreferencesUtils.getNetworkType() == 0 ? NetworkType.ALL : NetworkType.WIFI_ONLY);

            mFetch.enqueue(request, updatedRequest -> {
                FetchListener fetchListener = new FetchListener() {
                    @Override
                    public void onWaitingNetwork(@NotNull com.tonyodev.fetch2.Download download) {
                        DownloadProgressView dpv = mActivity.getDownloadFragment().getDownloadProgressView();
                        dpv.setWaitingForNetworkStatus(true);
                        mActivity.animateBottomDownloadButton(false, false);
                    }

                    @Override
                    public void onStarted(@NotNull com.tonyodev.fetch2.Download download, @NotNull List<? extends DownloadBlock> downloadBlocks, int totalBlocks) {
                        DownloadProgressView dpv = mActivity.getDownloadFragment().getDownloadProgressView();
                        dpv.setWaitingForNetworkStatus(false);
                        mActivity.animateBottomDownloadButton(true, false);
                    }

                    @Override
                    public void onResumed(@NotNull com.tonyodev.fetch2.Download download) {
                        mIsDwnPaused = false;
                        DownloadProgressView dpv = mActivity.getDownloadFragment().getDownloadProgressView();
                        dpv.setPausedStatus(false);
                        mActivity.animateBottomDownloadButton(true, false);
                    }

                    @Override
                    public void onRemoved(@NotNull com.tonyodev.fetch2.Download download) { }

                    @Override
                    public void onQueued(@NotNull com.tonyodev.fetch2.Download download, boolean waitingOnNetwork) { }

                    @Override
                    public void onProgress(@NotNull com.tonyodev.fetch2.Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
                        if (PreferencesUtils.Download.getDownloadID() == download.getId()) {
                            DownloadProgressView dpv = mActivity.getDownloadFragment().getDownloadProgressView();

                            int progress = download.getProgress();
                            if (progress < 0) progress = 0;
                            if (progress < dpv.getProgress()) {
                                Toast.makeText(mActivity, mActivity.getString(R.string.ten_download_failed), Toast.LENGTH_LONG).show();
                            }
                            dpv.setProgress(progress);

                            String hms = String.format(mActivity.getResources().getConfiguration().getLocales().get(0), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(etaInMilliSeconds),
                                    TimeUnit.MILLISECONDS.toMinutes(etaInMilliSeconds) % TimeUnit.HOURS.toMinutes(1),
                                    TimeUnit.MILLISECONDS.toSeconds(etaInMilliSeconds) % TimeUnit.MINUTES.toSeconds(1));
                            dpv.setTimeLeftText(hms);
                        }
                    }

                    @Override
                    public void onPaused(@NotNull com.tonyodev.fetch2.Download download) {
                        mIsDwnPaused = true;
                        DownloadProgressView dpv = mActivity.getDownloadFragment().getDownloadProgressView();
                        dpv.setPausedStatus(true);
                        mActivity.animateBottomDownloadButton(true, true);
                    }

                    @Override
                    public void onError(@NotNull com.tonyodev.fetch2.Download download, @NotNull Error error, @Nullable Throwable throwable) {
                        mActivity.onErrorROMUpdateDownload();
                    }

                    @Override
                    public void onDownloadBlockUpdated(@NotNull com.tonyodev.fetch2.Download download, @NotNull DownloadBlock downloadBlock, int totalBlocks) { }

                    @Override
                    public void onDeleted(@NotNull com.tonyodev.fetch2.Download download) { }

                    @Override
                    public void onCompleted(@NotNull com.tonyodev.fetch2.Download download) {
                        new MD5Check(mActivity).execute();
                    }

                    @Override
                    public void onCancelled(@NotNull com.tonyodev.fetch2.Download download) {
                        PreferencesUtils.Download.clean();
                    }

                    @Override
                    public void onAdded(@NotNull com.tonyodev.fetch2.Download download) { }
                };
                mFetch.addListener(fetchListener);
            }, error -> {
                throw new TenCoreException(error.toString());
            });

            PreferencesUtils.Download.setDownloadID(request.getId());

            mActivity.onPostROMUpdateDownload();
            mActivity.animateBottomDownloadButton(false, false);
        }

        public void cancelDownload() {
            String file = PreferencesUtils.ROM.getFullFilePathName(mActivity);
            int mDownloadID = PreferencesUtils.Download.getDownloadID();
            OTAGeneralUtils.deleteFile(new File(file));
            if (mFetch != null) {
                mFetch.remove(mDownloadID);
                mFetch.close();
            }
            PreferencesUtils.Download.clean();
        }

        public void pauseDownload() {
            int mDownloadID = PreferencesUtils.Download.getDownloadID();
            mFetch.pause(mDownloadID);
        }

        public void resumeDownload() {
            int mDownloadID = PreferencesUtils.Download.getDownloadID();
            mFetch.resume(mDownloadID);
        }

        public void resumeDownloadeing() {
            mActivity.onPostROMUpdateDownload();
            mActivity.animateBottomDownloadButton(true, mIsDwnPaused);
        }

        public void swapActivity(OTAMainActivity newActivity) {
            mActivity = newActivity;
        }
    }

    static class MD5Check extends AsyncTask<Object, Boolean, Boolean>{
        private final String TAG = "MD5Check";
        private OTAMainActivity mActivity;
        private DownloadProgressView mDPV;
        private File mUpdatePkg;

        private MD5Check(OTAMainActivity activity) {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            mDPV = mActivity.getDownloadFragment().getDownloadProgressView();
            mDPV.setCheckingMD5Status();
            mActivity.animateBottomDownloadButton(false, false);
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            mUpdatePkg = new File(PreferencesUtils.ROM.getFullFilePathName(mActivity.getApplicationContext()));
            String md5Remote = PreferencesUtils.ROM.getMd5().trim();
            return checkMD5(md5Remote, mUpdatePkg);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            PreferencesUtils.Download.setIsDownloadOnGoing(false);
            PreferencesUtils.Download.setDownloadFinished(result);

            if (result) {
                if (TenToolboxApp.isAppInBackground()) {
                    OTAGeneralUtils.setupDownloadCompletedNotification(mActivity, true);
                    mActivity.switchToFragment(OTAMainActivity.MAIN_PAGE_FRAGMENT);
                } else {
                    mDPV.setDownloadCompleteStatus();
                    mActivity.getDownloadFragment().getPreInstallWarningTextView().setVisibility(View.VISIBLE);
                    mActivity.animateBottomInstallButton(true);
                }
            } else {
                if (TenToolboxApp.isAppInBackground()) {
                    OTAGeneralUtils.setupDownloadCompletedNotification(mActivity, false);
                } else {
                    mDPV.setDownloadFailedStatus();
                    Toast.makeText(mActivity, mActivity.getString(R.string.ten_ota_download_failed_md5), Toast.LENGTH_SHORT).show();
                }
                OTAGeneralUtils.deleteFile(mUpdatePkg);

                mActivity.switchToFragment(OTAMainActivity.MAIN_PAGE_FRAGMENT);
            }

            super.onPostExecute(result);
        }

        private boolean checkMD5(String md5, File file) {
            if (TextUtils.isEmpty(md5) || file == null) {
                LogUtils.e(TAG, "MD5 string empty or updateFile null");
                return false;
            }

            String calculatedDigest = calculateMD5(file);
            if (calculatedDigest == null) {
                LogUtils.e(TAG, "calculatedDigest null");
                return false;
            }

            LogUtils.v(TAG, "Calculated digest: " + calculatedDigest + ", Manifest digest: " + md5);

            return calculatedDigest.equalsIgnoreCase(md5);
        }

        String calculateMD5(File updateFile) {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                LogUtils.e(TAG, e.toString());
                return null;
            }

            InputStream is;
            try {
                is = new FileInputStream(updateFile);
            } catch (FileNotFoundException e) {
                LogUtils.e(TAG, e.toString());
                return null;
            }

            byte[] buffer = new byte[8192];
            int read;
            try {
                while ((read = is.read(buffer)) > 0) {
                    digest.update(buffer, 0, read);
                }
                byte[] md5sum = digest.digest();
                BigInteger bigInt = new BigInteger(1, md5sum);
                String output = bigInt.toString(16);
                // Fill to 32 chars
                output = String.format("%32s", output).replace(' ', '0');
                return output;
            } catch (IOException e) {
                throw new TenCoreException(e.toString());
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    LogUtils.e(TAG, e.toString());
                }
            }
        }
    }
}
