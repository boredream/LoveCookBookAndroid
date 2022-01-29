package com.boredream.baseapplication.view.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;

public class GridDecoration extends RecyclerView.ItemDecoration {

    private int padding;

    public GridDecoration(int padding) {
        this.padding = padding;
    }

    public GridDecoration() {
        padding = SizeUtils.dp2px(5);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(padding, padding, padding, padding);
    }
}
