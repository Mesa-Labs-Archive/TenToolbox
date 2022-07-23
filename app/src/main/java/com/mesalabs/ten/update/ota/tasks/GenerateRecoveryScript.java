package com.mesalabs.ten.update.ota.tasks;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;

import com.mesalabs.ten.core.utils.PreferencesUtils;
import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.update.ota.utils.SystemUtils;

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

public class GenerateRecoveryScript extends AsyncTask<Void, String, Boolean> {
    public final String TAG = "GenerateRecoveryScript";

    private Context mContext;
    private Dialog mProgressCircle;
    private StringBuilder mScript = new StringBuilder();
    private static String SCRIPT_FILE = "/cache/recovery/openrecoveryscript";
    private static String NEW_LINE = "\n";
    private String mScriptOutput;

    public GenerateRecoveryScript(Context context) {
        mContext = context;
    }

    protected void onPreExecute() {
        // Show dialog
        mProgressCircle = new Dialog(mContext, R.style.ten_ProgressCircleDialogStyle);
        mProgressCircle.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressCircle.getWindow().setGravity(Gravity.CENTER);
        mProgressCircle.setCancelable(false);
        mProgressCircle.setCanceledOnTouchOutside(false);
        mProgressCircle.setContentView(LayoutInflater.from(mContext).inflate(R.layout.ten_view_progress_circle_dialog_layout, null));
        mProgressCircle.show();

        /*mScript.append("install "
                + PreferencesUtils.ROM.getFullFilePathName(mContext)
                + NEW_LINE);*/

        mScript.append("cmd rm -rf "
                + PreferencesUtils.ROM.getFullFilePathName(mContext)
                + NEW_LINE);

        mScript.append("reboot"
                + NEW_LINE);

        mScriptOutput = mScript.toString();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // Try create a dir in the cache folder
        // Without root
        String check = SystemUtils.shell("mkdir -p /cache/recovery/; echo $?", false);

        // If not 0, then permission was denied
        if(!check.equals("0")) {
            // Run as root
            SystemUtils.shell("mkdir -p /cache/recovery/; echo $?", true);
            SystemUtils.shell("echo \"" + mScriptOutput + "\" > " + SCRIPT_FILE + "\n", true);
        } else {
            // Permission was enabled, run without root
            SystemUtils.shell("echo \"" + mScriptOutput + "\" > " + SCRIPT_FILE + "\n", false);
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean value) {
        PreferencesUtils.setRebootForInstall(true);
        SystemUtils.rebootToRecovery(mContext);
    }
}
