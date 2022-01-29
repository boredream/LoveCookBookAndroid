package com.boredream.baseapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.boredream.baseapplication.R;

import butterknife.ButterKnife;

public class FabView extends RelativeLayout {

    public FabView(Context context) {
        super(context);
    }

    public FabView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context, attrs);
    }

    public FabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_fab, this);
        ButterKnife.bind(this);
    }

}