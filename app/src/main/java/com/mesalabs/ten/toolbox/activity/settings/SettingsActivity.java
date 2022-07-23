package com.mesalabs.ten.toolbox.activity.settings;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.fragment.settings.SettingsFragment;

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

public class SettingsActivity extends AppCompatActivity {
    private ToolbarLayout mToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten_toolbox_activity_settings_layout);

        mToolbarLayout = findViewById(R.id.ten_settingsactivity_toolbarlayout);
        mToolbarLayout.setNavigationButtonOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                onBackPressed();
            }
        });
        mToolbarLayout.setNavigationButtonTooltip(getString(R.string.sesl_navigate_up));

        getSupportFragmentManager().beginTransaction().replace(R.id.ten_settingsactivity_content_container, new SettingsFragment()).commit();
    }

}
