package com.mesalabs.ten.toolbox.activity.aboutpage;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.mesalabs.ten.core.update.utils.AppUpdateUtils;
import com.mesalabs.ten.core.utils.LogUtils;
import com.mesalabs.ten.core.utils.StateUtils;
import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.TenToolboxApp;

import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;
import de.dlyt.yanndroid.oneui.widget.ProgressBar;

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
 *
 * ULTRA-MEGA-PRIVATE SOURCE CODE. SHARING TO DEVKINGS TEAM
 * EXTERNALS IS PROHIBITED AND WILL BE PUNISHED WITH ANAL ABUSE.
 */

public class AboutActivity extends AppCompatActivity {
    private static final String TAG = "AboutActivity";
    private static final String KEY_UPDATE_STATE = "update_state";

    private Context mContext;
    private int mCheckingStatus = AppUpdateUtils.STATE_CHECKING;
    private AppUpdateUtils mAppUpdate;
    private AppUpdateUtils.StubListener mStubListener = new AppUpdateUtils.StubListener() {
        public void onUpdateCheckCompleted(int status) {
            checkForUpdatesCompleted(status);
        }
    };

    private ToolbarLayout mToolbarLayout;
    private LinearLayout mMainLayout;
    private LinearLayout mAppInfoLayout;
    private ScrollView mScrollView;
    private LinearLayout mAppInfoContainer;
    private View mTopSpacing;
    private TextView mAppNameTextView;
    private TextView mAppVersionTextView;
    private TextView mOptionalTextView;
    private ProgressBar mProgress;
    private TextView mStatusText;
    private AppCompatButton mStatusButton;
    private View mMiddleSpacing;
    private LinearLayout mButtonsContainer;
    private AppCompatButton mCreditsButton;
    private AppCompatButton mOpenSourceButton;
    private View mBottomSpacing;
    private LinearLayout mButtonsContainerLand;
    private AppCompatButton mCreditsButtonLand;
    private AppCompatButton mOpenSourceButtonLand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten_layout_aboutactivity);

        mContext = this;
        mAppUpdate = new AppUpdateUtils(this, TenToolboxApp.getAppPackageName(), mStubListener);

        mToolbarLayout = findViewById(R.id.ten_aboutactivity_toolbarlayout);
        mToolbarLayout.findViewById(R.id.toolbar_layout_app_bar).setBackgroundColor(getResources().getColor(R.color.splash_background, getTheme()));
        mToolbarLayout.findViewById(R.id.toolbar_layout_collapsing_toolbar_layout).setBackgroundColor(getResources().getColor(R.color.splash_background, getTheme()));
        mToolbarLayout.setNavigationButtonOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                onBackPressed();
            }
        });
        mToolbarLayout.setNavigationButtonTooltip(getString(R.string.sesl_navigate_up));
        mToolbarLayout.inflateToolbarMenu(R.menu.ten_menu_aboutactivity);
        mToolbarLayout.setOnToolbarMenuItemClickListener(new ToolbarLayout.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(de.dlyt.yanndroid.oneui.menu.MenuItem item) {
                if (item.getItemId() == R.id.ten_menu_app_info) {
                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", TenToolboxApp.getAppPackageName(), null));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        mMainLayout = findViewById(R.id.ten_aboutactivity_main_layout);
        mAppInfoLayout = findViewById(R.id.ten_aboutactivity_app_info_layout);
        mScrollView = findViewById(R.id.ten_aboutactivity_scrollview);
        mAppInfoContainer = findViewById(R.id.ten_aboutactivity_app_info_container);

        mTopSpacing = findViewById(R.id.ten_aboutactivity_top_spacing);
        mMiddleSpacing = findViewById(R.id.ten_aboutactivity_middle_spacing);
        mBottomSpacing = findViewById(R.id.ten_aboutactivity_bottom_spacing);

        mAppNameTextView = findViewById(R.id.ten_aboutactivity_app_name_text);
        mAppVersionTextView = findViewById(R.id.ten_aboutactivity_app_version_text);
        mOptionalTextView = findViewById(R.id.ten_aboutactivity_optional_text);
        mAppNameTextView.setText(TenToolboxApp.getAppName());
        mAppVersionTextView.setText(getString(R.string.sesl_version) + " " + TenToolboxApp.getAppVersionString());
        mOptionalTextView.setText("Coded by BlackMesa123");

        mProgress = findViewById(R.id.ten_aboutactivity_progress);
        mStatusText = findViewById(R.id.ten_aboutactivity_status_text);
        mStatusButton = findViewById(R.id.ten_aboutactivity_status_button);
        mStatusButton.setCompoundDrawables(null, null, null, null);

        mButtonsContainer = findViewById(R.id.ten_aboutactivity_buttons_container);
        mCreditsButton = findViewById(R.id.ten_aboutactivity_creditsbtn);
        mOpenSourceButton = findViewById(R.id.ten_aboutactivity_opensourcebtn);
        mButtonsContainerLand = findViewById(R.id.ten_aboutactivity_buttons_container_land);
        mCreditsButtonLand = findViewById(R.id.ten_aboutactivity_creditsbtn_land);
        mOpenSourceButtonLand = findViewById(R.id.ten_aboutactivity_opensourcebtn_land);
        setOpenSourceButtonWidth(mCreditsButton);
        setOpenSourceButtonWidth(mOpenSourceButton);
        setOpenSourceButtonWidth(mCreditsButtonLand);
        setOpenSourceButtonWidth(mOpenSourceButtonLand);
        setOnClickListeners();

        if (savedInstanceState != null) {
            mCheckingStatus = savedInstanceState.getInt(KEY_UPDATE_STATE, AppUpdateUtils.STATE_CHECKING);
        }
        checkForUpdatesCompleted(mCheckingStatus);
        if (mCheckingStatus == AppUpdateUtils.STATE_CHECKING) {
            checkForUpdatesNotCompleted();
        }

        setLayoutOrientation();
        setButtonsTextSize();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        setOpenSourceButtonWidth(mCreditsButton);
        setOpenSourceButtonWidth(mOpenSourceButton);
        setOpenSourceButtonWidth(mCreditsButtonLand);
        setOpenSourceButtonWidth(mOpenSourceButtonLand);
        setLayoutOrientation();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(KEY_UPDATE_STATE, mCheckingStatus);
    }

    //
    // App Update methods
    //
    private void checkForUpdatesCompleted(int status) {
        mCheckingStatus = status;
        setCheckingStatus(true);

        if (status == AppUpdateUtils.STATE_NEW_VERSION_AVAILABLE) {
            mStatusText.setText(getString(R.string.new_version_is_available));
            mStatusButton.setText(getString(R.string.sesl_update));
            mStatusButton.setVisibility(View.VISIBLE);
        } else if (status == AppUpdateUtils.STATE_ERROR) {
            mStatusText.setText(getString(R.string.network_connect_is_not_stable));
            mStatusButton.setText(getString(R.string.retry));
            mStatusButton.setVisibility(View.VISIBLE);
        } else if (status != AppUpdateUtils.STATE_CHECKING) {
            mStatusText.setText(getString(R.string.latest_version_installed));
            mStatusButton.setVisibility(View.GONE);
        } else {
            mStatusText.setVisibility(View.GONE);
            mStatusButton.setVisibility(View.GONE);
        }

        setLayoutOrientation();
    }

    private void checkForUpdatesNotCompleted() {
        setCheckingStatus(false);
        mAppUpdate.checkUpdates();
    }

    private void setCheckingStatus(boolean status) {
        mProgress.setVisibility(status ? View.GONE : View.VISIBLE);
        mStatusText.setVisibility(status ? View.VISIBLE : View.GONE);
        mStatusButton.setVisibility(status ? View.VISIBLE : View.GONE);
    }

    //
    // Layout methods
    //
    private void setOnClickListeners() {
        mStatusButton.setOnClickListener(new OnSingleClickListener() {
            public void onSingleClick(View view) {
                if (mCheckingStatus != AppUpdateUtils.STATE_ERROR) {
                    mAppUpdate.installUpdate();
                } else if (StateUtils.isNetworkConnected(mContext)) {
                    checkForUpdatesNotCompleted();
                } else {
                    Toast.makeText(mContext, getString(R.string.ten_no_network_connection), Toast.LENGTH_LONG).show();
                }
            }
        });
        mCreditsButton.setOnClickListener(new OnSingleClickListener() {
            public void onSingleClick(View view) {
                startActivity(new Intent(mContext, CreditsActivity.class));
            }
        });
        mOpenSourceButton.setOnClickListener(new OnSingleClickListener() {
            public void onSingleClick(View view) {
                startActivity(new Intent(mContext, OpenSourceLicenseActivity.class));
            }
        });
        mCreditsButtonLand.setOnClickListener(new OnSingleClickListener() {
            public void onSingleClick(View view) {
                startActivity(new Intent(mContext, CreditsActivity.class));
            }
        });
        mOpenSourceButtonLand.setOnClickListener(new OnSingleClickListener() {
            public void onSingleClick(View view) {
                startActivity(new Intent(mContext, OpenSourceLicenseActivity.class));
            }
        });
    }

    private void setLayoutOrientation() {
        LinearLayout.LayoutParams appInfoViewLp = (LinearLayout.LayoutParams) mAppInfoLayout.getLayoutParams();
        LinearLayout.LayoutParams webLinkViewLp = (LinearLayout.LayoutParams) mButtonsContainerLand.getLayoutParams();
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        double heightPixels = getResources().getDisplayMetrics().heightPixels;

        double d = isPortrait ? 0.05d : 0.036d;
        double d2 = isPortrait ? 0.086d : 0.036d;
        int i = (int) (d2 * heightPixels);

        mTopSpacing.getLayoutParams().height = i;
        mMiddleSpacing.getLayoutParams().height = i;
        mBottomSpacing.getLayoutParams().height = (int) (heightPixels * d);

        if (isPortrait) {
            mMainLayout.setOrientation(LinearLayout.VERTICAL);
            mAppInfoLayout.setGravity(49);
            mButtonsContainer.setVisibility(View.VISIBLE);
            mButtonsContainerLand.setVisibility(View.GONE);
        } else {
            mMainLayout.setOrientation(LinearLayout.HORIZONTAL);
            appInfoViewLp.weight = 5.0f;
            webLinkViewLp.weight = 5.0f;
            mAppInfoLayout.setGravity(17);
            mAppInfoContainer.setGravity(17);
            mButtonsContainer.setVisibility(View.GONE);
            mButtonsContainerLand.setVisibility(View.VISIBLE);
            mButtonsContainerLand.setGravity(17);
        }
    }

    private void setOpenSourceButtonWidth(AppCompatButton button) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int widthPixels = displayMetrics.widthPixels;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            widthPixels /= 2;
        }
        button.setMaxWidth((int) (0.75d * widthPixels));
        button.setMinWidth((int) (widthPixels * 0.61d));
    }

    private final void setButtonsTextSize() {
        setLargeTextSize(mAppNameTextView, (float) getResources().getDimensionPixelSize(R.dimen.about_page_app_name_text_size));
        setLargeTextSize(mStatusButton, (float) getResources().getDimensionPixelSize(R.dimen.about_page_status_button_text_size));

        float dimensionPixelSize = (float) getResources().getDimensionPixelSize(R.dimen.about_page_secondary_text_size);
        setLargeTextSize(mAppVersionTextView, dimensionPixelSize);
        setLargeTextSize(mStatusText, dimensionPixelSize);

        dimensionPixelSize = (float) getResources().getDimensionPixelSize(R.dimen.about_page_optional_text_size);
        setLargeTextSize(mOptionalTextView, dimensionPixelSize);

        dimensionPixelSize = (float) getResources().getDimensionPixelSize(R.dimen.about_page_button_text_size);
        setLargeTextSize(mCreditsButton, dimensionPixelSize);
        setLargeTextSize(mOpenSourceButton, dimensionPixelSize);
        setLargeTextSize(mCreditsButtonLand, dimensionPixelSize);
        setLargeTextSize(mOpenSourceButtonLand, dimensionPixelSize);
    }

    private void setLargeTextSize(TextView textView, float size) {
        if (textView != null) {
            float fontScale = mContext.getResources().getConfiguration().fontScale;
            float fixSize = size / fontScale;
            LogUtils.d(TAG, "setLargeTextSize fontScale : " + fontScale + ", " + size + ", " + fixSize);
            if (fontScale > 1.3f) {
                fontScale = 1.3f;
            }

            textView.setTextSize(0, (float) Math.ceil(fixSize * fontScale));
        }
    }
}