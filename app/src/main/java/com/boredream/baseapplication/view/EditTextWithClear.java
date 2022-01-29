package com.boredream.baseapplication.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditTextWithClear extends RelativeLayout {

    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.iv_clear)
    ImageView ivClear;

    public EditText getEt() {
        return etContent;
    }

    public EditTextWithClear(Context context) {
        super(context);
        initView(context, null);
    }

    public EditTextWithClear(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public EditTextWithClear(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_edit_text_with_clear, this);
        ButterKnife.bind(this);

        etContent.setOnFocusChangeListener((v, hasFocus) -> updateClearIcon());
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateClearIcon();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ivClear.setOnClickListener(v -> etContent.setText(""));

        if (attrs == null) {
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EditTextWithClear);
        String text = ta.getString(R.styleable.EditTextWithClear_text);
        float textSize = ta.getDimension(R.styleable.EditTextWithClear_textSize, SizeUtils.dp2px(14));
        String hint = ta.getString(R.styleable.EditTextWithClear_hint);
        etContent.setText(text);
        etContent.setTextSize(SizeUtils.px2dp(textSize));
        etContent.setHint(hint);
    }

    private void updateClearIcon() {
        ivClear.setVisibility(!StringUtils.isEmpty(getText()) && etContent.hasFocus() ? View.VISIBLE : View.GONE);
    }

    public void setText(CharSequence text) {
        etContent.setText(text);
        if (text != null) {
            etContent.setSelection(text.length());
        }
    }

    public Editable getText() {
        return etContent.getText();
    }

    public void setHint(CharSequence text) {
        etContent.setHint(text);
    }

}