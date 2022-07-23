package com.mesalabs.ten.toolbox.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mesalabs.ten.core.utils.PreferencesUtils;
import com.mesalabs.ten.core.utils.SystemInfoUtils;
import com.mesalabs.ten.core.utils.WifiAdbUtils;
import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.activity.WifiAdbInnerActivity;
import com.mesalabs.ten.update.activity.OTAMainActivity;
import com.mesalabs.ten.update.ota.ROMUpdate;

import de.dlyt.yanndroid.oneui.layout.PreferenceFragment;
import de.dlyt.yanndroid.oneui.preference.LayoutPreference;
import de.dlyt.yanndroid.oneui.preference.Preference;
import de.dlyt.yanndroid.oneui.preference.SwitchPreferenceScreen;
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
 */

public class HomePreferenceFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {
    private static final String KEY_STATUS_CARD = "ten_home_status_card";
    //private static final String KEY_HELP_CARD = "ten_home_help";
    private static final String KEY_DEVICEINFO_DEVICE_NAME = "ten_home_info_device";
    private static final String KEY_DEVICEINFO_MANUFACTURING_DATE = "ten_home_info_manufacturing";
    private static final String KEY_DEVICEINFO_ROM_VERSION = "ten_home_info_rom";
    private static final String KEY_DEVICEINFO_ANDROID_VERSION = "ten_home_info_android";
    private static final String KEY_DEVICEINFO_ONEUI_VERSION = "ten_home_info_oneui";
    private static final String KEY_DEVICEINFO_KERNEL_BUILD = "ten_home_info_kernel";
    private static final String KEY_DEVICEINFO_BOOTLOADER_BUILD = "ten_home_info_bootloader";
    private static final String KEY_DEVICEINFO_MODEM_BUILD = "ten_home_info_modem";
    private static final String KEY_DEVICEINFO_BUILD_VERSION = "ten_home_info_build";
    private static final String KEY_DEVICEINFO_TEMPERATURE = "ten_home_info_temperature";
    private static final String KEY_DEVICEINFO_UPTIME = "ten_home_info_uptime";
    private static final String KEY_WIFI_ADB = "ten_wifi_adb";
    private AlphaAnimation mFadeInAnim;
    private ROMUpdate mROMUpdate;
    private ROMUpdate.StubListener mROMStubListener = this::postROMUpdatesCheck;

    private Context mContext;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mTemperaturePref.setSummary(SystemInfoUtils.getFormattedHwTemp());
            mUpTimePref.setSummary(SystemInfoUtils.getDeviceUpTime());
            mHandler.postDelayed(this, 1000);
        }
    };

    private LinearLayout mStatusCard;
    private ProgressBar mStatusCardProgress;
    private TextView mStatusCardText;
    private Preference mTemperaturePref;
    private Preference mUpTimePref;
    private SwitchPreferenceScreen mWifiAdbPref;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

        //  init ROMUpdate
        mROMUpdate = new ROMUpdate(context, mROMStubListener);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R.xml.ten_toolbox_preference_home);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initPreferences();

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        cm.registerNetworkCallback(builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build(),
                new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        setWifiAdbAllowed(true);
                    }

                    @Override
                    public void onLost(Network network) {
                        setWifiAdbAllowed(false);
                    }
                }
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mWifiAdbPref != null) {
            if (!WifiAdbUtils.isWifiAdbAllowed()) {
                WifiAdbUtils.setWifiAdb(false);
                mWifiAdbPref.setEnabled(false);
            }
            mWifiAdbPref.setChecked(WifiAdbUtils.isWifiAdbEnabled());
            mWifiAdbPref.setSummary(WifiAdbUtils.getWifiAdbPreferenceSummary(mContext));
            mWifiAdbPref.seslSetSummaryColor(getColoredSummaryColor(WifiAdbUtils.isWifiAdbEnabled()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
        mHandler.removeMessages(2);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.post(mRunnable);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setBackgroundColor(getResources().getColor(R.color.item_background_color, mContext.getTheme()));
        getListView().seslSetLastRoundedCorner(false);

        init();

        if (PreferencesUtils.Download.getDownloadFinished())
            postROMUpdatesCheck(ROMUpdate.STATE_DOWNLOADED);
        else
            mROMUpdate.checkUpdates(true);
    }

    private void init() {
        mFadeInAnim = new AlphaAnimation(0.0f, 1.0f);
        mFadeInAnim.setDuration(500);

        LayoutPreference statusCardPref = (LayoutPreference) findPreference(KEY_STATUS_CARD);
        mStatusCard = statusCardPref.findViewById(R.id.ten_home_status_card);
        mStatusCardProgress = statusCardPref.findViewById(R.id.ten_home_status_progress);
        mStatusCardText = statusCardPref.findViewById(R.id.ten_home_status_text);
    }

    private void initPreferences() {
        Preference deviceName = findPreference(KEY_DEVICEINFO_DEVICE_NAME);
        deviceName.setSummary(SystemInfoUtils.getDeviceNameFromBootloader());

        Preference manufacturingDate = findPreference(KEY_DEVICEINFO_MANUFACTURING_DATE);
        manufacturingDate.setSummary(SystemInfoUtils.getManufacturingDate());

        mTemperaturePref = findPreference(KEY_DEVICEINFO_TEMPERATURE);

        mUpTimePref = findPreference(KEY_DEVICEINFO_UPTIME);

        Preference romVersion = findPreference(KEY_DEVICEINFO_ROM_VERSION);
        romVersion.setSummary(SystemInfoUtils.getROMVersion());

        Preference androidVersion = findPreference(KEY_DEVICEINFO_ANDROID_VERSION);
        androidVersion.setSummary(SystemInfoUtils.getAndroidVersion());

        Preference oneUiVersion = findPreference(KEY_DEVICEINFO_ONEUI_VERSION);
        oneUiVersion.setSummary(SystemInfoUtils.getOneUIVersion());

        Preference kernelBuild = findPreference(KEY_DEVICEINFO_KERNEL_BUILD);
        kernelBuild.setSummary(SystemInfoUtils.getKernelBuild());

        Preference bootloaderBuild = findPreference(KEY_DEVICEINFO_BOOTLOADER_BUILD);
        bootloaderBuild.setSummary(SystemInfoUtils.getBootloaderBuildVersion());

        Preference modemBuild = findPreference(KEY_DEVICEINFO_MODEM_BUILD);
        modemBuild.setSummary(SystemInfoUtils.getModemBuildVersion());

        Preference buildVersion = findPreference(KEY_DEVICEINFO_BUILD_VERSION);
        buildVersion.setSummary(SystemInfoUtils.getBuildNumber());

        mWifiAdbPref = (SwitchPreferenceScreen) findPreference(KEY_WIFI_ADB);
        mWifiAdbPref.setOnPreferenceChangeListener(this);
        mWifiAdbPref.setOnPreferenceClickListener(this);
    }

    private void postROMUpdatesCheck(int status) {
        mStatusCardProgress.setVisibility(View.GONE);
        mStatusCardText.setVisibility(View.VISIBLE);
        if (status == ROMUpdate.STATE_NEW_VERSION_AVAILABLE || status == ROMUpdate.STATE_DOWNLOADED)
            mStatusCardText.setText(getString(R.string.ten_ota_new_update_available));
        else
            mStatusCardText.setText(getString(R.string.ten_home_status_text));
        mStatusCardText.startAnimation(mFadeInAnim);

        if (PreferencesUtils.Download.getUpdateAvailability()) {
            mStatusCard.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    startActivity(new Intent(getActivity(), OTAMainActivity.class));
                }
            });
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case KEY_WIFI_ADB:
                WifiAdbUtils.setWifiAdb((Boolean) newValue);
                mWifiAdbPref.setSummary(WifiAdbUtils.getWifiAdbPreferenceSummary(mContext));
                preference.seslSetSummaryColor(getColoredSummaryColor(WifiAdbUtils.isWifiAdbEnabled()));
                return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case KEY_WIFI_ADB:
                startActivity(new Intent(mContext, WifiAdbInnerActivity.class));
                return true;
        }
        return false;
    }

    private ColorStateList getColoredSummaryColor(boolean enabled) {
        if (enabled) {
            TypedValue textColor = new TypedValue();
            getContext().getTheme().resolveAttribute(enabled ? R.attr.colorPrimaryDark : android.R.attr.textColorSecondary, textColor, true);

            int[][] states = new int[][] {
                    new int[] {android.R.attr.state_enabled},
                    new int[] {-android.R.attr.state_enabled}
            };
            int[] colors = new int[] {
                    Color.argb(0xff, Color.red(textColor.data), Color.green(textColor.data), Color.blue(textColor.data)),
                    Color.argb(0x4d, Color.red(textColor.data), Color.green(textColor.data), Color.blue(textColor.data))
            };
            return new ColorStateList(states, colors);
        } else
            return getContext().getResources().getColorStateList(R.color.sesl_secondary_text, getContext().getTheme());
    }

    private void setWifiAdbAllowed(boolean allowed) {
        getActivity().runOnUiThread(() -> {
            WifiAdbUtils.setWifiAdb(allowed && WifiAdbUtils.isWifiAdbEnabled());
            mWifiAdbPref.setEnabled(allowed);
        });
    }
}
