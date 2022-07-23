package com.mesalabs.ten.update.ota.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;

import com.mesalabs.ten.core.root.utils.RootUtils;
import com.mesalabs.ten.core.utils.LogUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

public class SystemUtils {
    private static String TAG = "SystemUtils";

    private static String getSuBin() {
        if (new File("/system/xbin","su").exists()) {
            return "/system/xbin/su";
        }
        if (isDeviceRooted()) {
            return "su";
        }
        return "sh";
    }

    public static boolean isDeviceRooted() {
        return RootUtils.rootAccess();
    }

    private static boolean isUiThread() {
        return (Looper.myLooper() == Looper.getMainLooper());
    }

    public static void rebootDevice(Context context, String type) {
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            powerManager.reboot(type);
        } catch (Exception e) {
            LogUtils.e(TAG, "reboot '" + type + "' error: " + e.getMessage());
            RootUtils.runCommand("reboot "+ type);
        }
    }

    public static void rebootToRecovery(Context context) {
        rebootDevice(context, "recovery");
    }

    public static String shell(String cmd, boolean root) {
        String out = "";
        ArrayList<String> r = system(root ? getSuBin() : "sh", cmd).getStringArrayList("out");
        for(String l: r) {
            out += l+"\n";
        }
        return out;
    }

    private static Bundle system(String shell, String command) {
        if (isUiThread()) {
            LogUtils.e(shell,"Application attempted to run a shell command from the main thread");
        }
        LogUtils.d(shell,"START");

        ArrayList<String> res = new ArrayList<String>();
        ArrayList<String> err = new ArrayList<String>();
        boolean success = false;
        try {
            Process process = Runtime.getRuntime().exec(shell);
            DataOutputStream STDIN = new DataOutputStream(process.getOutputStream());
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader STDERR = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            LogUtils.i(shell, command);
            STDIN.writeBytes(command + "\n");
            STDIN.flush();
            STDIN.writeBytes("exit\n");
            STDIN.flush();

            process.waitFor();
            if (process.exitValue() == 255) {
                LogUtils.e(shell,"SU was probably denied! Exit value is 255");
                err.add("SU was probably denied! Exit value is 255");
            }

            while (STDOUT.ready()) {
                String read = STDOUT.readLine();
                LogUtils.d(shell, read);
                res.add(read);
            }
            while (STDERR.ready()) {
                String read = STDERR.readLine();
                LogUtils.e(shell, read);
                err.add(read);
            }

            process.destroy();
            success = true;
            if (err.size() > 0) {
                success = false;
            }
        } catch (IOException e) {
            LogUtils.e(shell,"IOException: "+e.getMessage());
            err.add("IOException: "+e.getMessage());
        } catch (InterruptedException e) {
            LogUtils.e(shell,"InterruptedException: "+e.getMessage());
            err.add("InterruptedException: "+e.getMessage());
        }
        LogUtils.d(shell,"END");
        Bundle r = new Bundle();
        r.putBoolean("success", success);
        r.putString("cmd", command);
        r.putString("binary", shell);
        r.putStringArrayList("out", res);
        r.putStringArrayList("error", err);
        return r;
    }

}
