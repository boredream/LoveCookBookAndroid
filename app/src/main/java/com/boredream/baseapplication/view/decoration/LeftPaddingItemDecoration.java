package com.boredream.baseapplication.view.decoration;

/*
 * Copyright (C) 2014 The Android Open Source Project
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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.boredream.baseapplication.R;

/**
 * 左边距的line divider decoration
 */
public class LeftPaddingItemDecoration extends RecyclerView.ItemDecoration {

    private GradientDrawable mDivider;

    private final Rect mBounds = new Rect();

    public LeftPaddingItemDecoration(Context context) {
        mDivider = new GradientDrawable();
        mDivider.setColor(context.getResources().getColor(R.color.divider_gray));
        mDivider.setSize(1, 1);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null || mDivider == null) {
            return;
        }

        canvas.save();
        int left;
        int right;
        //noinspection AndroidLintNewApi - NewApi lint fails to onError overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            // 边距
            left += parent.getResources().getDimension(R.dimen.activity_horizontal_margin);
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            if("no divider".equals(child.getTag())) {
                continue;
            }

            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom - mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (mDivider == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    }
}
