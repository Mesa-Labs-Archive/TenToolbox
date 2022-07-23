package com.mesalabs.ten.core.utils;

import com.mesalabs.ten.core.root.SU;
import com.mesalabs.ten.core.root.utils.RootFile;
import com.mesalabs.ten.core.root.utils.RootUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
 */

public class FileUtils {
    public static boolean existFile(String file) {
        return existFile(file, false);
    }

    public static boolean existFile(String file, boolean root) {
        return existFile(file, root ? RootUtils.getSU() : null);
    }

    public static boolean existFile(String file, SU su) {
        return su == null ? new File(file).exists() : new RootFile(file, su).exists();
    }

    public static String readFile(String file) {
        return readFile(file, false);
    }

    public static String readFile(String file, boolean root) {
        return readFile(file, root ? RootUtils.getSU() : null);
    }

    public static String readFile(String file, SU su) {
        if (su != null) {
            return new RootFile(file, su).readFile();
        }

        BufferedReader buf = null;
        try {
            buf = new BufferedReader(new FileReader(file));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = buf.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            return stringBuilder.toString().trim();
        } catch (IOException ignored) {
        } finally {
            try {
                if (buf != null) buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
