package com.boredream.baseapplication.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.dialog.BottomListSelectedDialog;
import com.boredream.baseapplication.entity.SettingItem;
import com.boredream.baseapplication.listener.OnSelectedListener;
import com.boredream.baseapplication.net.GlideHelper;
import com.boredream.baseapplication.utils.DateUtils;
import com.boredream.baseapplication.utils.DialogUtils;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingItemView extends RelativeLayout {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.iv_right_arrow)
    ImageView ivRightArrow;

    public ImageView getIvLeft() {
        return ivLeft;
    }

    public ImageView getIvRight() {
        return ivRight;
    }

    public TextView getTvRight() {
        return tvRight;
    }

    public SettingItemView(Context context) {
        super(context);
        initView(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_setting_item, this);
        ButterKnife.bind(this);

        View dividerView = new View(context);
        dividerView.setBackgroundResource(R.color.divider_gray);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        params.leftMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        params.addRule(ALIGN_PARENT_BOTTOM);
        dividerView.setLayoutParams(params);
        addView(dividerView);

        if (attrs == null) {
            return;
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
        int icon = ta.getResourceId(R.styleable.SettingItemView_icon, -1);
        int level = ta.getInt(R.styleable.SettingItemView_level, 0);
        String name = ta.getString(R.styleable.SettingItemView_name);
        String rightText = ta.getString(R.styleable.SettingItemView_rightText);
        boolean showRightArrow = ta.getBoolean(R.styleable.SettingItemView_showRightArrow, false);
        setData(new SettingItem(icon == -1 ? null : icon, name, rightText, showRightArrow));

        if (level == 1) {
            // small
            tvName.setTextSize(14);
            tvRight.setTextSize(12);
        }
    }

    public void setName(String str) {
        tvName.setText(str);
    }

    public void setText(String str) {
        setRightText(str);
    }

    public String getText() {
        return tvRight.getText().toString();
    }

    public void setData(SettingItem data) {
        tvName.setText(data.getName());
        setIcon(data.getIcon());
        setRightText(data.getRightText());
        setRightImage(data.isShowRightImage(), data.getRightImage(), data.getRightImageDefault());
        ivRightArrow.setVisibility(data.isShowRightArrow() ? View.VISIBLE : View.GONE);
    }

    public void setIcon(Integer icon) {
        if (icon != null) {
            ivLeft.setVisibility(View.VISIBLE);
            ivLeft.setImageResource(icon);
        } else {
            ivLeft.setVisibility(View.GONE);
        }
    }

    public void setRightImage(boolean showRightImage, String rightImage, Integer rightImageDefault) {
        if (showRightImage) {
            ivRight.setVisibility(View.VISIBLE);
            GlideHelper.loadOvalImg(ivRight, rightImage, rightImageDefault);
        } else {
            ivRight.setVisibility(View.GONE);
        }
    }

    public void setRightText(String rightText) {
        if (rightText != null) {
            tvRight.setVisibility(View.VISIBLE);
            tvRight.setText(rightText);
        } else {
            tvRight.setVisibility(View.GONE);
        }
    }

    public void setDateAction(OnSelectedListener<String> listener) {
        setOnClickListener(v -> {
            String oldData = tvRight.getText().toString().trim();
            DialogUtils.showCalendarPickDialog(getContext(), oldData, calendar -> {
                String date = DateUtils.calendar2str(calendar);
                setRightText(date);
                if (listener != null) {
                    listener.onSelected(date);
                }
            });
        });
    }

    public void setSpinnerAction(OnSelectedListener<String> listener, String... items) {
        setSpinnerAction(listener, new ArrayList<>(Arrays.asList(items)));
    }

    public void setSpinnerAction(OnSelectedListener<String> listener, ArrayList<String> list) {
        setOnClickListener(v -> {
            String oldData = tvRight.getText().toString().trim();
            if (StringUtils.isEmpty(oldData)) {
                oldData = null;
            }

            BottomListSelectedDialog<String> dialog = new BottomListSelectedDialog<>(
                    getContext(), tvName.getText().toString().trim(), list, oldData);
            dialog.setOnListSelectedListener(data -> {
                setRightText(data);
                if (listener != null) {
                    listener.onSelected(data);
                }
            });
            dialog.show();
        });
    }

}