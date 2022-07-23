package com.mesalabs.ten.update.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.util.SeslMisc;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mesalabs.ten.core.ui.utils.FragmentsUtils;
import com.mesalabs.ten.core.update.utils.AppUpdateUtils;
import com.mesalabs.ten.core.utils.LogUtils;
import com.mesalabs.ten.core.utils.PreferencesUtils;
import com.mesalabs.ten.core.utils.StateUtils;
import com.mesalabs.ten.core.utils.TenCoreException;
import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.TenToolboxApp;
import com.mesalabs.ten.update.fragment.home.DownloadProgressFragment;
import com.mesalabs.ten.update.fragment.home.MainCardsFragment;
import com.mesalabs.ten.update.ota.ROMUpdate;
import com.mesalabs.ten.update.ota.tasks.GenerateRecoveryScript;
import com.mesalabs.ten.update.ota.utils.OTAGeneralUtils;

import de.dlyt.yanndroid.oneui.dialog.AlertDialog;
import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;
import de.dlyt.yanndroid.oneui.widget.SwipeRefreshLayout;

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

public class OTAMainActivity extends AppCompatActivity {
    public static int MAIN_PAGE_FRAGMENT = 0;
    public static int DOWNLOAD_PROGRESS_FRAGMENT = 1;

    private boolean mAppUpdateAvailable = false;
    private boolean mIsDownloadeing = false;
    private int newFragmentIndex;

    private Context mContext;
    private AppUpdateUtils mAppUpdate;
    private AppUpdateUtils.StubListener mAppStubListener = status -> {
        mAppUpdateAvailable = status == AppUpdateUtils.STATE_NEW_VERSION_AVAILABLE;
        PreferencesUtils.setIsAppUpdateAvailable(mAppUpdateAvailable);
    };

    private AlphaAnimation mFadeInAnim;
    private AlphaAnimation mFadeOutAnim;
    private AlphaAnimation mFadeInAnim_Dwn;
    private AlphaAnimation mFadeOutAnim_Dwn;
    private AlphaAnimation mFadeOutAnim_F;

    private Dialog mProgressCircle;
    private FragmentManager mFragmentManager;
    private Fragment mFragment;
    private FrameLayout mFragmentContainer;

    private ToolbarLayout mToolbarLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mFooterLayout;
    private AppCompatButton mFooterButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        // init
        if (!OTAGeneralUtils.isNotificationAlarmSet(mContext)) {
            OTAGeneralUtils.setBackgroundCheck(mContext, PreferencesUtils.getBgServiceEnabled());
        }

        mIsDownloadeing = PreferencesUtils.Download.getDownloadFinished() || PreferencesUtils.Download.getIsDownloadOnGoing();

        if (!mIsDownloadeing) {
            PreferencesUtils.ROM.clean();
            PreferencesUtils.Download.clean();
        }

        //  init pt.2
        mAppUpdate = new AppUpdateUtils(this, TenToolboxApp.getAppPackageName(), mAppStubListener);
        mAppUpdate.checkUpdates();

        if (TenToolboxApp.getROMUpdateDwn() != null) {
            TenToolboxApp.getROMUpdateDwn().swapActivity(this);
        } else {
            TenToolboxApp.setROMUpdateDwn(new ROMUpdate.Download(this));
        }

        // init UX
        setContentView(R.layout.ten_ota_activity_otamain_layout);

        mToolbarLayout = findViewById(R.id.ten_ota_mainactivity_toolbarlayout);
        mToolbarLayout.setTitle(getString(R.string.ten_tenupdate));
        mToolbarLayout.setNavigationButtonOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                onBackPressed();
            }
        });
        mToolbarLayout.setNavigationButtonTooltip(getString(R.string.sesl_navigate_up));

        mSwipeRefreshLayout = findViewById(R.id.ten_ota_mainactivity_swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!StateUtils.isNetworkConnected(mContext)) {
                    Toast.makeText(mContext, R.string.ten_no_network_connection, Toast.LENGTH_LONG).show();
                }
                ((MainCardsFragment) mFragment).checkForROMUpdates();
            }
        });

        initUX();
    }

    @Override
    public void onBackPressed() {
        if (PreferencesUtils.Download.getIsDownloadOnGoing()) {
            new AlertDialog.Builder(mContext)
                    .setCancelable(true)
                    .setTitle(getString(R.string.ten_ota_main_cancel_dialog_title))
                    .setMessage(getString(R.string.ten_ota_main_cancel_dialog_content))
                    .setPositiveButton(getString(R.string.ten_ok), (dialog, which) -> {
                        TenToolboxApp.getROMUpdateDwn().cancelDownload();
                        switchToFragment(MAIN_PAGE_FRAGMENT);
                    })
                    .setNegativeButton(getString(R.string.ten_cancel), null)
                    .show();
        } else if (newFragmentIndex == DOWNLOAD_PROGRESS_FRAGMENT) {
            switchToFragment(MAIN_PAGE_FRAGMENT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //ViewUtils.updateListBothSideMargin(this, mFragmentContainer);
    }

    private void initUX() {
        initAnimationFields();

        mProgressCircle = new Dialog(mContext, R.style.ten_ProgressCircleDialogStyle);
        mProgressCircle.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressCircle.getWindow().setGravity(Gravity.CENTER);
        mProgressCircle.setCancelable(false);
        mProgressCircle.setCanceledOnTouchOutside(false);
        mProgressCircle.setContentView(LayoutInflater.from(mContext).inflate(R.layout.ten_view_progress_circle_dialog_layout, null));

        mFragmentManager = getSupportFragmentManager();
        mFragmentContainer = findViewById(R.id.ten_ota_mainactivity_fragment_container);
        //ViewSupport.updateListBothSideMargin(this, mFragmentContainer);

        mFooterLayout = findViewById(R.id.ten_ota_mainactivity_footer);
        mFooterButton = findViewById(R.id.ten_ota_mainactivity_footer_btn);

        if (mIsDownloadeing) {
            onPreROMUpdateDownload();
        } else  {
            switchToFragment(MAIN_PAGE_FRAGMENT);
        }
    }

    private void initAnimationFields() {
        mFadeInAnim = new AlphaAnimation(0.0f, 1.0f);
        mFadeInAnim.setDuration(250);
        mFadeOutAnim = new AlphaAnimation(1.0f, 0.0f);
        mFadeOutAnim.setDuration(250);

        mFadeInAnim_Dwn = new AlphaAnimation(0.0f, 1.0f);
        mFadeInAnim_Dwn.setDuration(250);
        mFadeInAnim_Dwn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFragmentContainer.setAlpha(1.0f);
                mFooterLayout.setAlpha(1.0f);
                mFooterButton.startAnimation(mFadeInAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) { }
        });
        mFadeOutAnim_Dwn = new AlphaAnimation(1.0f, 0.0f);
        mFadeOutAnim_Dwn.setDuration(250);
        mFadeOutAnim_Dwn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFragmentContainer.setAlpha(0.0f);
                mFooterLayout.setAlpha(0.0f);
                mFooterLayout.setVisibility(View.VISIBLE);
                inflateFragment(newFragmentIndex);
                if (mIsDownloadeing) {
                    TenToolboxApp.getROMUpdateDwn().resumeDownloadeing();
                } else {
                    TenToolboxApp.getROMUpdateDwn().startDownload();
                }
            }
        });

        mFadeOutAnim_F = new AlphaAnimation(1.0f, 0.0f);
        mFadeOutAnim_F.setDuration(250);
        mFadeOutAnim_F.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFooterButton.startAnimation(mFadeOutAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFooterLayout.setVisibility(newFragmentIndex != 0 ? View.VISIBLE : View.GONE);
                inflateFragment(newFragmentIndex);
                mFragmentContainer.startAnimation(mFadeInAnim);
                mFooterButton.startAnimation(mFadeInAnim);
            }
        });
    }

    private void inflateFragment(int index) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag(FragmentsUtils.getOTAFragmentsTags()[index]);
        if (mFragment == null) {
            mFragment = mFragmentManager.findFragmentById(R.id.ten_ota_mainactivity_fragment_container);
        }
        if (mFragment != null) {
            transaction.hide(mFragment);
            if (mFragment instanceof DownloadProgressFragment) {
                transaction.remove(mFragment);
            }
        }

        if (fragment != null) {
            mFragment = fragment;
            transaction.show(fragment);
        } else {
            try {
                mFragment = (Fragment) FragmentsUtils.getOTAFragmentsClasses()[index].newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                throw new TenCoreException(e.toString());
            }
            transaction.add(R.id.ten_ota_mainactivity_fragment_container, mFragment, FragmentsUtils.getOTAFragmentsTags()[index]);
        }
        transaction.commit();
        mFragmentManager.executePendingTransactions();
    }

    private long getAvailableInternalMemorySize() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
    }



    public void animateBottomDownloadButton(boolean enabled, boolean paused) {
        mFooterButton.setEnabled(enabled);
        mFooterButton.setAlpha(enabled ? 1.0f : 0.4f);
        if (paused) {
            mFooterButton.setText(getString(R.string.ten_ota_resume));
            mFooterButton.setOnClickListener(v -> TenToolboxApp.getROMUpdateDwn().resumeDownload());
        } else {
            mFooterButton.setText(getString(R.string.ten_ota_pause));
            mFooterButton.setOnClickListener(v -> TenToolboxApp.getROMUpdateDwn().pauseDownload());
        }
    }

    public void animateBottomInstallButton(boolean enabled) {
        mFooterButton.setEnabled(enabled);
        mFooterButton.setAlpha(enabled ? 1.0f : 0.4f);
        mFooterButton.setText(getString(R.string.ten_ota_install_now));
        mFooterButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                new GenerateRecoveryScript(mContext).execute();
            }
        });
        mFooterButton.postDelayed(() -> mFooterButton.setPressed(true), 250);
        mFooterButton.postDelayed(() -> mFooterButton.setPressed(false), 250);
    }

    public DownloadProgressFragment getDownloadFragment() {
        if (mFragment instanceof DownloadProgressFragment) {
            return (DownloadProgressFragment) mFragment;
        } else {
            LogUtils.e("OTAMainActivity", "DownloadProgressFragment not inflated!!!");
            return (DownloadProgressFragment) mFragmentManager.findFragmentById(R.id.ten_ota_mainactivity_fragment_container);
        }
    }

    public void onPreROMUpdateDownload() {
        mProgressCircle.show();
        if (getAvailableInternalMemorySize() < PreferencesUtils.ROM.getFileSize()) {
            Toast.makeText(mContext, getString(R.string.ten_ota_no_space_left), Toast.LENGTH_LONG).show();
            mProgressCircle.dismiss();
            return;
        }
        newFragmentIndex = DOWNLOAD_PROGRESS_FRAGMENT;
        setSRLEnabled(false);
        mFragmentContainer.startAnimation(mFadeOutAnim_Dwn);
    }

    public void onPostROMUpdateDownload() {
        mProgressCircle.dismiss();
        mFragmentContainer.startAnimation(mFadeInAnim_Dwn);
    }

    public void onErrorROMUpdateDownload() {
        TenToolboxApp.getROMUpdateDwn().cancelDownload();
        Toast.makeText(mContext, getString(R.string.ten_download_failed), Toast.LENGTH_LONG).show();
        switchToFragment(MAIN_PAGE_FRAGMENT);
    }

    public void setSRLEnabled(boolean enabled) {
        mSwipeRefreshLayout.setEnabled(enabled);
    }

    public void setSRLRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    public void switchToFragment(int index) {
        newFragmentIndex = index;
        mIsDownloadeing = PreferencesUtils.Download.getDownloadFinished() || PreferencesUtils.Download.getIsDownloadOnGoing();
        setSRLEnabled(!mIsDownloadeing);
        mFragmentContainer.startAnimation(mFadeOutAnim_F);
    }

}
