/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oluwafemi.medmanager.util;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;

/**
 * Created by phemi-mint on 4/15/18.
 */

public class FabVisibilityListener extends RecyclerView.OnScrollListener {

    private FloatingActionButton fab;
    private Activity mActivity;

    public FabVisibilityListener(FloatingActionButton mFab) {
        this.mActivity = mActivity;
        fab = mFab;
    }


    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (recyclerView.getAdapter().getItemCount() < 4 && !fab.isShown()) {
            fab.show();
        }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy < -5 && !fab.isShown()) {
            fab.show();
        } else if(dy > 5 && fab.isShown()){
            fab.hide();
        } else if(dy == 1 && !fab.isShown()){
            fab.show();
        }
    }
}
