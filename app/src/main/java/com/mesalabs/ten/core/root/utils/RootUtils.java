package com.mesalabs.ten.core.root.utils;

/*
 * 십 Toolbox
 *
 * Coded by BlackMesa123 @2021
 * Original code by Willi Ye & AndreiLux
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

import com.mesalabs.ten.core.root.SU;

public class RootUtils {
    private static SU su;

    public static boolean rootAccess() {
        SU su = getSU();
        su.runCommand("echo /testRoot/");
        return !su.denied;
    }

    public static boolean busyboxInstalled() {
        return existBinary("busybox") || existBinary("toybox");
    }

    private static boolean existBinary(String binary) {
        String paths;
        if (System.getenv("PATH") != null) {
            paths = System.getenv("PATH");
        } else {
            paths = "/sbin:/vendor/bin:/system/sbin:/system/bin:/system/xbin";
        }
        for (String path : paths.split(":")) {
            if (!path.endsWith("/")) path += "/";
            if (RootOperations.existFile(path + binary, false) || RootOperations.existFile(path + binary)) {
                return true;
            }
        }
        return false;
    }

    public static void chmod(String file, String permission) {
        chmod(file, permission, getSU());
    }

    public static void chmod(String file, String permission, SU su) {
        su.runCommand("chmod " + permission + " " + file);
    }

    public static void mount(boolean writeable, String mountpoint) {
        mount(writeable, mountpoint, getSU());
    }

    public static void mount(boolean writeable, String mountpoint, SU su) {
        su.runCommand(String.format("mount -o remount,%s %s %s", writeable ? "rw" : "ro", mountpoint, mountpoint));
        su.runCommand(String.format("mount -o remount,%s %s", writeable ? "rw" : "ro", mountpoint));
        su.runCommand(String.format("mount -o %s,remount %s", writeable ? "rw" : "ro", mountpoint));
    }

    public static String runScript(String text, String... arguments) {
        RootFile script = new RootFile("/data/local/tmp/grxsettingstmp.sh");
        script.mkdir();
        script.write(text, false);
        return script.execute(arguments);
    }

    public static void closeSU() {
        if (su != null) {
            su.close();
        }
        su = null;
    }

    public static String runCommand(String command) {
        return getSU().runCommand(command);
    }

    public static SU getSU() {
        return getSU(false);
    }

    public static SU getSU(boolean root) {
        if (su == null || su.closed || su.denied) {
            if (su != null && !su.closed) {
                su.close();
            }
            su = new SU(root);
        }
        return su;
    }
}
