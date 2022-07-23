package com.mesalabs.ten.update.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.mesalabs.ten.core.ui.widget.CardView;
import com.mesalabs.ten.core.utils.SystemInfoUtils;
import com.mesalabs.ten.toolbox.R;

import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;

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

public class FirmwareInfoActivity extends AppCompatActivity {
    private ToolbarLayout mToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ten_ota_activity_firmwareinfo_layout);

        mToolbarLayout = findViewById(R.id.ten_firmwareinfoactivity_toolbarlayout);
        mToolbarLayout.setNavigationButtonOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                onBackPressed();
            }
        });
        mToolbarLayout.setNavigationButtonTooltip(getString(R.string.sesl_navigate_up));

        init();
    }

    private void init()  {
        // ROM Version
        CardView rom = findViewById(R.id.ten_ota_firmwareinfo_card_rom);
        setFwInfoCardSummary(rom, SystemInfoUtils.getROMVersion());
        // Android Version
        CardView android = findViewById(R.id.ten_ota_firmwareinfo_card_android);
        setFwInfoCardSummary(android, SystemInfoUtils.getAndroidVersion());
        // OneUI Version
        CardView oneui = findViewById(R.id.ten_ota_firmwareinfo_card_oneui);
        setFwInfoCardSummary(oneui, SystemInfoUtils.getOneUIVersion());
        // Kernel
        CardView kernel = findViewById(R.id.ten_ota_firmwareinfo_card_kernel);
        setFwInfoCardSummary(kernel, SystemInfoUtils.getKernelBuild());
        // Build Number
        CardView bn = findViewById(R.id.ten_ota_firmwareinfo_card_build);
        setFwInfoCardSummary(bn, SystemInfoUtils.getBuildNumber());
        // Security Patch
        CardView sp = findViewById(R.id.ten_ota_firmwareinfo_card_patch);
        setFwInfoCardSummary(sp, SystemInfoUtils.getSecurityPatchVersion());
    }

    private void setFwInfoCardSummary(CardView card, String summary) {
        if (summary != null) {
            card.setSummaryText(summary);
        }
    }

}
