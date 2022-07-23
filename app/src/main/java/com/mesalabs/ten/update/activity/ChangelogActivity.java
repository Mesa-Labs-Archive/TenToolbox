package com.mesalabs.ten.update.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.util.SeslMisc;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mesalabs.ten.core.utils.LogUtils;
import com.mesalabs.ten.core.utils.PreferencesUtils;
import com.mesalabs.ten.core.utils.StateUtils;
import com.mesalabs.ten.toolbox.R;

import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.sesl.appbar.SamsungAppBarLayout;
import de.dlyt.yanndroid.oneui.sesl.support.WindowManagerSupport;
import de.dlyt.yanndroid.oneui.sesl.utils.ReflectUtils;
import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;
import de.dlyt.yanndroid.oneui.widget.RoundNestedScrollView;

/*
 * 십 Update
 *
 * Coded by BlackMesa123 @2021
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

public class ChangelogActivity extends AppCompatActivity {
    private boolean isLightBgColor;
    private int mInsetTop;

    private Context mContext;
    private ToolbarLayout mToolbarLayout;
    private ImageView mAppBarBackground;
    private RoundNestedScrollView mNestedScrollView;
    private FrameLayout mProgressContainer;
    private FrameLayout mMainContainer;
    private TextView mChangelogTitle;
    private TextView mChangelogDate;
    private View mDivider;
    private TextView mContentText;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        // init
        if (!StateUtils.isNetworkConnected(mContext)) {
            Toast.makeText(mContext, R.string.ten_no_network_connection, Toast.LENGTH_LONG).show();
            finish();
        }

        setContentView(R.layout.ten_ota_activity_changelogactivity_layout);

        mToolbarLayout = findViewById(R.id.ten_changelogactivity_toolbarlayout);
        mToolbarLayout.getAppBarLayout().addOnOffsetChangedListener(new CustomAppBarListener());
        mToolbarLayout.setNavigationButtonIcon(getResources().getDrawable(R.drawable.ic_oui_back, getTheme()));
        mToolbarLayout.setNavigationButtonOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                onBackPressed();
            }
        });
        mToolbarLayout.setNavigationButtonTooltip(getString(R.string.sesl_navigate_up));

        isLightBgColor = SeslMisc.isLightTheme(mContext);

        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.sesl_appbar_height_proportion, outValue, true);

        if (outValue.getFloat() == 0.0f)
            setPortraitFullscreen();
        else
            setLandFullscreen();

        mAppBarBackground = findViewById(R.id.ten_changelogactivity_appbar_bg);
        mNestedScrollView = findViewById(R.id.ten_changelogactivity_nestedscroll);
        mProgressContainer = findViewById(R.id.ten_changelogactivity_progress_container);
        mMainContainer = findViewById(R.id.ten_changelogactivity_main_container);
        mChangelogTitle = findViewById(R.id.ten_changelogactivity_main_title_view);
        mChangelogDate = findViewById(R.id.ten_changelogactivity_main_date_view);
        mDivider = findViewById(R.id.ten_changelogactivity_main_divider);
        mContentText = findViewById(R.id.ten_changelogactivity_main_context_text);

        new LoadChangelogTask().execute(PreferencesUtils.ROM.getChangelogHeaderImgUrl(), PreferencesUtils.ROM.getChangelogUrl());
    }

    private void setLandFullscreen() {
        if (!WindowManagerSupport.isMultiWindowMode(this)) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(getColor(android.R.color.transparent));

            ViewCompat.setOnApplyWindowInsetsListener(mToolbarLayout, (v, insets) -> {
                mInsetTop = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
                setAppBarHeight(((ToolbarLayout) v).getAppBarLayout(), mInsetTop);
                setToolbarMarginTop(((ToolbarLayout) v).getToolbar(), mInsetTop);
                return WindowInsetsCompat.CONSUMED;
            });
        }
    }

    private void setPortraitFullscreen() {
        if (!WindowManagerSupport.isMultiWindowMode(this)) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            ReflectUtils.genericInvokeMethod(params, "semAddExtensionFlags", 1 /* WindowManager.LayoutParams.SEM_EXTENSION_FLAG_RESIZE_FULLSCREEN_WINDOW_ON_SOFT_INPUT */);
            getWindow().setAttributes(params);
        }
    }

    private void setLightStatusBar(boolean enable) {
        View decorView = getWindow().getDecorView();
        int flags = decorView.getSystemUiVisibility();

        if (enable) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        decorView.setSystemUiVisibility(flags);
    }

    private void setAppBarHeight(View view, int insetTop) {
        float appBarHeight = mToolbarLayout.getAppBarLayout().seslGetHeightProPortion();
        int newHeight = (int) (appBarHeight * getResources().getDisplayMetrics().heightPixels + insetTop);
        if (appBarHeight != 0.0f && view.getHeight() != newHeight) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.height = newHeight;
        }
    }

    private void setToolbarMarginTop(View view, int marginTop) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.setMargins(0, marginTop, 0, 0);
    }



    private class CustomAppBarListener implements SamsungAppBarLayout.OnOffsetChangedListener {
        private boolean previousState;

        @Override
        public void onOffsetChanged(SamsungAppBarLayout layout, int verticalOffset) {
            int layoutPosition = Math.abs(layout.getTop());
            float alphaRange = ((float) mToolbarLayout.findViewById(R.id.toolbar_layout_collapsing_toolbar_layout).getHeight()) * 0.17999999f;
            float titleAlpha = 255.0f - ((100.0f / alphaRange) * (((float) layoutPosition) - 0.0f));

            if (titleAlpha < 0.0f) {
                titleAlpha = 0.0f;
            } else if (titleAlpha > 255.0f) {
                titleAlpha = 255.0f;
            }

            boolean expanded = titleAlpha > 0.0f;
            onStateChanged(expanded);
            previousState = expanded;

            if (mProgressContainer != null && mProgressContainer.getVisibility() == View.VISIBLE) {
                int totalScrollRange = layout.getTotalScrollRange();
                int inputMethodWindowVisibleHeight = (int) ReflectUtils.genericInvokeMethod(InputMethodManager.class, mContext.getSystemService(INPUT_METHOD_SERVICE), "getInputMethodWindowVisibleHeight");

                if (totalScrollRange != 0) {
                    mProgressContainer.setTranslationY(((float) (Math.abs(verticalOffset) - totalScrollRange)) / 2.0f);
                } else if (WindowManagerSupport.isMultiWindowMode(ChangelogActivity.this)) {
                    mProgressContainer.setTranslationY(0.0f);
                } else {
                    mProgressContainer.setTranslationY(((float) (Math.abs(verticalOffset) - inputMethodWindowVisibleHeight)) / 2.0f);
                }
            }
        }

        @SuppressLint("RestrictedApi")
        public void onStateChanged(boolean expanded) {
            if (previousState != expanded)
                setLightStatusBar(expanded ? isLightBgColor : SeslMisc.isLightTheme(mContext));
        }
    }

    private class LoadChangelogTask extends AsyncTask<String, Void, Bitmap> {
        String title;
        String date;
        String content = "";

        @Override
        protected Bitmap doInBackground(String... urls) {
            String headerBgUrl = urls[0];
            String changelogUrl = urls[1];
            Bitmap mBg = null;
            String line;

            try {
                InputStream in = new URL(headerBgUrl).openStream();
                mBg = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                LogUtils.e("Exception", e.getMessage());
            }

            title = PreferencesUtils.ROM.getRomName() + " v" + PreferencesUtils.ROM.getVersionName();
            date = String.valueOf(PreferencesUtils.ROM.getBuildNumber());
            try {
                date = DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "d/MM/yyyy"), new SimpleDateFormat("yyyyMMdd").parse(date)).toString();
            } catch (ParseException ignored) { }

            try {
                InputStream in = new URL(changelogUrl).openConnection().getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                while ((line = br.readLine()) != null) {
                    content += line + "\n";
                }
                br.close();
            } catch (Exception e) {
                LogUtils.e("Exception", e.getMessage());
            }

            return mBg;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                mAppBarBackground.setImageBitmap(result);
                isLightBgColor = isColorLight(getDominantColor(result));
                setLightStatusBar(isLightBgColor);
            } else {
                LogUtils.e("DownloadHeaderImageTask", "result is null!!!");
            }
            mNestedScrollView.setBackgroundColor(mContext.getResources().getColor(R.color.item_background_color, getTheme()));

            mProgressContainer.setVisibility(View.GONE);
            mMainContainer.setVisibility(View.VISIBLE);
            mChangelogTitle.setText(title);
            mChangelogDate.setText(date);
            mDivider.setVisibility(View.VISIBLE);
            mContentText.setText(content);
            mToolbarLayout.setExpanded(true, true);
        }

        private int getDominantColor(Bitmap bitmap) {
            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
            final int color = newBitmap.getPixel(0, 0);
            newBitmap.recycle();
            return color;
        }

        private boolean isColorLight(int color) {
            return ColorUtils.calculateLuminance(color) > 0.5;
        }
    }
}
