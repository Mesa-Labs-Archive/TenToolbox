package com.mesalabs.ten.toolbox.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.mesalabs.ten.core.base.SwitchBarFragmentActivity;
import com.mesalabs.ten.core.utils.WifiAdbUtils;
import com.mesalabs.ten.toolbox.R;
import com.mesalabs.ten.toolbox.fragment.home.WifiAdbInnerFragment;

import de.dlyt.yanndroid.oneui.widget.Switch;
import de.dlyt.yanndroid.oneui.widget.SwitchBar;

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

public class WifiAdbInnerActivity extends SwitchBarFragmentActivity
        implements SwitchBar.OnSwitchChangeListener {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mSwitchBarLayout.setTitle(getString(R.string.ten_wifi_adb));
        mSwitchBar.setEnabled(WifiAdbUtils.isWifiAdbAllowed());

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
    protected Fragment getFragmentClass() {
        return new WifiAdbInnerFragment();
    }

    @Override
    protected SwitchBar.OnSwitchChangeListener getSwitchBarListener() {
        return this;
    }

    @Override
    protected boolean getSwitchBarDefaultStatus() {
        return WifiAdbUtils.isWifiAdbEnabled();
    }

    @Override
    public void onSwitchChanged(Switch switchCompat, boolean checked) {
        WifiAdbUtils.setWifiAdb(checked);
        ((WifiAdbInnerFragment) mInflatedFragment).onSwitchBarChanged(checked);
    }

    private void setWifiAdbAllowed(boolean allowed) {
        runOnUiThread(() -> {
            WifiAdbUtils.setWifiAdb(allowed && WifiAdbUtils.isWifiAdbEnabled());
            onSwitchChanged(null, allowed && WifiAdbUtils.isWifiAdbEnabled());
            mSwitchBar.setChecked(allowed && WifiAdbUtils.isWifiAdbEnabled());
            mSwitchBar.setEnabled(allowed);
        });
    }
}
