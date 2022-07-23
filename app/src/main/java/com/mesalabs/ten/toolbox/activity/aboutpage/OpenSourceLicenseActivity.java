package com.mesalabs.ten.toolbox.activity.aboutpage;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.util.SeslMisc;

import com.google.android.material.appbar.MaterialToolbar;
import com.mesalabs.ten.core.utils.LogUtils;
import com.mesalabs.ten.toolbox.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;

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

public class OpenSourceLicenseActivity extends AppCompatActivity {
    private static final String TAG = "OpenSourceLicenseActivity";
    private ToolbarLayout mToolbarLayout;
    public WebView mWebView = null;

    @Override
    @SuppressLint("RestrictedApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten_layout_opensourcelicenseactivity);

        mToolbarLayout = findViewById(R.id.ten_opensourcelicenseactivity_toolbarlayout);
        mToolbarLayout.setNavigationButtonOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                finish();
            }
        });
        mToolbarLayout.setNavigationButtonTooltip(getString(R.string.sesl_navigate_up));
        mWebView = findViewById(R.id.ten_opensourcelicenseactivity_webview);

        if (SeslMisc.isLightTheme(this)) {
            mWebView.loadUrl("file:///android_asset/text/NOTICE.html");
        } else {
            String htmlContent = readAssetFileAsString("text/NOTICE.html");
            mWebView.setBackgroundColor(0xFF171717);
            StringBuilder data = new StringBuilder();
            data.append("<html><head><style type=\"text/css\">body{color: #ffffff; background-color: #252525;}</style></head><body>");
            data.append(htmlContent);
            data.append("</body></html>");
            mWebView.loadDataWithBaseURL(null, data.toString(), "text/html", "utf-8", null);
        }

        WebSettings wvSettings = mWebView.getSettings();
        wvSettings.setDefaultTextEncodingName("UTF-8");
        wvSettings.setSupportZoom(true);
        wvSettings.setBuiltInZoomControls(true);
        wvSettings.setDisplayZoomControls(false);
        wvSettings.setLoadWithOverviewMode(true);
        wvSettings.setUseWideViewPort(true);
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView = null;
        }

        super.onDestroy();
    }

    public final String readAssetFileAsString(String file) {
        try {
            InputStream is = getAssets().open(file);
            byte[] content = new byte[is.available()];
            is.read(content);
            is.close();
            return new String(content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LogUtils.e(TAG, e.toString());
            return "";
        }
    }
}
