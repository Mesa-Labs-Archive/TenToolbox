package com.mesalabs.ten.core.ui.creditspage;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mesalabs.ten.toolbox.activity.aboutpage.CreditsActivity;
import com.mesalabs.ten.toolbox.R;

import java.util.List;

import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;
import de.dlyt.yanndroid.oneui.view.RecyclerView;

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

public class CreditsPageListAdapter extends RecyclerView.Adapter<CreditsPageListViewHolder> {
    private CreditsActivity mActivity;
    private List<CreditsListViewModel> mModel;

    public CreditsPageListAdapter(CreditsActivity activity, List<CreditsListViewModel> model) {
        mActivity = activity;
        mModel = model;
    }

    @Override
    public int getItemCount() {
        return mModel.size();
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public int getItemViewType(final int position) {
        return position + 1 == getItemCount() ? 1 : 0;
    }

    @Override
    public CreditsPageListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = viewType == 1 ? LayoutInflater.from(parent.getContext()).inflate(R.layout.ten_view_bottom_spacing_layout, parent, false) : LayoutInflater.from(parent.getContext()).inflate(R.layout.ten_view_creditspage_list_item_layout, parent, false);
        return new CreditsPageListViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(CreditsPageListViewHolder holder, final int position) {
        holder.onBindViewHolder(mModel.get(position));
        if (holder.getIsItem()) {
            holder.setItemOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    String webLink = mModel.get(position).getLibWebLink();

                    if (!webLink.isEmpty()) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(webLink));
                            mActivity.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(mActivity, "No suitable activity found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

}
