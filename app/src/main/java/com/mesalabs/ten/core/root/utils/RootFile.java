package com.mesalabs.ten.core.root.utils;

import com.mesalabs.ten.core.root.SU;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

public class RootFile {
    private final String mFile;
    private SU mSU;

    public RootFile(String file) {
        mFile = file;
        mSU = RootUtils.getSU();
    }

    public RootFile(String file, SU su) {
        mFile = file;
        mSU = su;
    }

    public String getName() {
        return new File(mFile).getName();
    }

    public void mkdir() {
        mSU.runCommand("mkdir -p '" + mFile + "'");
    }

    public RootFile mv(String newPath) {
        mSU.runCommand("mv -f '" + mFile + "' '" + newPath + "'");
        return new RootFile(newPath);
    }

    public void cp(String path) {
        mSU.runCommand("cp -r '" + mFile + "' '" + path + "'");
    }

    public void write(String text, boolean append) {
        String[] array = text.split("\\r?\\n");
        if (!append) delete();
        for (String line : array) {
            mSU.runCommand("echo '" + line + "' >> " + mFile);
        }
        RootUtils.chmod(mFile, "755", mSU);
    }

    public String execute(String... arguments) {
        StringBuilder args = new StringBuilder();
        for (String arg : arguments) {
            args.append(" \"").append(arg).append("\"");
        }
        return mSU.runCommand(mFile + args.toString());
    }

    public void delete() {
        mSU.runCommand("rm -r '" + mFile + "'");
    }

    public List<String> list() {
        List<String> list = new ArrayList<>();
        String files = mSU.runCommand("ls '" + mFile + "/'");
        if (files != null) {
            // Make sure the files exists
            for (String file : files.split("\\r?\\n")) {
                if (file != null && !file.isEmpty() && RootOperations.existFile(mFile + "/" + file)) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    public List<RootFile> listFiles() {
        List<RootFile> list = new ArrayList<>();
        String files = mSU.runCommand("ls '" + mFile + "/'");
        if (files != null) {
            // Make sure the files exists
            for (String file : files.split("\\r?\\n")) {
                if (file != null && !file.isEmpty() && RootOperations.existFile(mFile + "/" + file)) {
                    list.add(new RootFile(mFile.equals("/") ? mFile + file : mFile + "/" + file, mSU));
                }
            }
        }
        return list;
    }

    public boolean isDirectory() {
        return "true".equals(mSU.runCommand("[ -d " + mFile + " ] && echo true"));
    }

    public RootFile getParentFile() {
        return new RootFile(new File(mFile).getParent(), mSU);
    }

    public RootFile getRealPath() {
        return new RootFile(mSU.runCommand("realpath \"" + mFile + "\""), mSU);
    }

    public boolean isEmpty() {
        return "false".equals(mSU.runCommand("find '" + mFile + "' -mindepth 1 | read || echo false"));
    }

    public boolean exists() {
        String output = mSU.runCommand("[ -e " + mFile + " ] && echo true");
        return output != null && output.equals("true");
    }

    public String readFile() {
        return mSU.runCommand("cat '" + mFile + "'");
    }

    @Override
    public String toString() {
        return mFile;
    }
}
