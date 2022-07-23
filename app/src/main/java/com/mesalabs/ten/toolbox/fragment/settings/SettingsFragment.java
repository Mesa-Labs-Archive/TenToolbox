package com.mesalabs.ten.toolbox.fragment.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.View;

import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.TenToolboxApp;
import com.mesalabs.ten.core.utils.PreferencesUtils;
import com.mesalabs.ten.update.ota.utils.OTAGeneralUtils;

import de.dlyt.yanndroid.oneui.layout.PreferenceFragment;
import de.dlyt.yanndroid.oneui.preference.DropDownPreference;
import de.dlyt.yanndroid.oneui.preference.ListPreference;
import de.dlyt.yanndroid.oneui.preference.Preference;
import de.dlyt.yanndroid.oneui.preference.SwitchPreferenceCompat;

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

public class SettingsFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {
    public static final String KEY_TEMPERATURE_UNIT = "ten_temp_unit";
    private static final String KEY_BACKGROUND_SERVICE = "ten_ota_background_service";
    private static final String KEY_BACKGROUND_SERVICE_FREQUENCY = "ten_ota_background_service_check";
    private static final String KEY_NOTIFICATION_SOUND = "ten_ota_notification_sound";
    private static final String KEY_NETWORK_TYPE = "ten_ota_network_type";
    private static final String KEY_ABOUT_PAGE = "ten_about_page";

    private long mLastClickTime = 0L;

    @Override
    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R.xml.ten_toolbox_preference_settings);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        DropDownPreference tempUnitPref = (DropDownPreference) findPreference(KEY_TEMPERATURE_UNIT);
        tempUnitPref.seslSetSummaryColor(getColoredSummaryColor(true));

        SwitchPreferenceCompat bgServicePref = (SwitchPreferenceCompat) findPreference(KEY_BACKGROUND_SERVICE);
        bgServicePref.setOnPreferenceChangeListener(this);

        ListPreference bgServiceFreqPref = (ListPreference) findPreference(KEY_BACKGROUND_SERVICE_FREQUENCY);
        bgServiceFreqPref.seslSetSummaryColor(getColoredSummaryColor(true));
        bgServiceFreqPref.setOnPreferenceChangeListener(this);

        Preference bgServiceNotiSoundPref = findPreference(KEY_NOTIFICATION_SOUND);
        bgServiceNotiSoundPref.seslSetSummaryColor(getColoredSummaryColor(true));
        bgServiceNotiSoundPref.setOnPreferenceClickListener(this);

        DropDownPreference networkTypePref = (DropDownPreference) findPreference(KEY_NETWORK_TYPE);
        networkTypePref.seslSetSummaryColor(getColoredSummaryColor(true));
        networkTypePref.setEnabled(!PreferencesUtils.Download.getIsDownloadOnGoing());

        Preference aboutActivityPref = findPreference(KEY_ABOUT_PAGE);
        if (PreferencesUtils.getIsAppUpdateAvailable()) {
            aboutActivityPref.setWidgetLayoutResource(R.layout.ten_view_preference_badge_layout);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().seslSetLastRoundedCorner(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        String ringtoneSummary = "";

        if (TenToolboxApp.isOTANotificationsSilent()) {
            NotificationChannel mainNotiChannel = ((NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE)).getNotificationChannel(PreferencesUtils.getOTAMainNotificationChannelName());
            Uri value = mainNotiChannel.getSound();

            if (value != null)
                ringtoneSummary = value.toString();
        }

        PreferencesUtils.setBgServiceNotificationSound(ringtoneSummary);
        setRingtoneSummary();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case KEY_BACKGROUND_SERVICE:
                PreferencesUtils.setBgServiceEnabled((boolean) newValue);
                OTAGeneralUtils.setBackgroundCheck(getContext(), (boolean) newValue);
                return true;
            case KEY_BACKGROUND_SERVICE_FREQUENCY:
                PreferencesUtils.setBgServiceCheckFrequency((String) newValue);
                OTAGeneralUtils.scheduleNotification(getContext(), PreferencesUtils.getBgServiceEnabled());
                return true;
        }

        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 600L) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (preference.getKey()) {
            case KEY_NOTIFICATION_SOUND:
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, TenToolboxApp.getAppPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, PreferencesUtils.getOTAMainNotificationChannelName());
                startActivity(intent);
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

    private void setRingtoneSummary() {
        String title = getString(R.string.ten_settings_notification_sound_silent);
        String value = PreferencesUtils.getBgServiceNotificationSound();
        if (!value.equals("")) {
            Ringtone ringtone = RingtoneManager.getRingtone(getContext(), Uri.parse(value));
            title = ringtone.getTitle(getContext());
        }

        Preference bgServiceNotiSoundPref = findPreference(KEY_NOTIFICATION_SOUND);
        bgServiceNotiSoundPref.setSummary(title);
    }

}