package com.boredream.baseapplication.dialog;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.listener.OnSelectedListener;
import com.boredream.baseapplication.view.DialogTitle;
import com.boredream.baseapplication.view.EditTextWithClear;

public class BottomInputDialog extends BottomDialog {

    private final String title;
    private final String oldValue;
    private final OnSelectedListener<String> listener;
    private DialogTitle dialogTitle;
    private EditTextWithClear etwcContent;

    public BottomInputDialog(@NonNull Context context, String title, String oldValue, OnSelectedListener<String> listener) {
        super(context);

        this.title = title;
        this.oldValue = oldValue;
        this.listener = listener;

        setContentView(R.layout.dialog_bottom_input);
        initView();
        initData();
    }

    private void initView() {
        dialogTitle = findViewById(R.id.bottom_dialog_title);
        etwcContent = findViewById(R.id.etwc_content);
        dialogTitle.setOnClickListener(v -> dismiss());
        findViewById(R.id.btn_ok).setOnClickListener(v -> performClick());
        setOnDismissListener(dialog -> KeyboardUtils.hideSoftInput(etwcContent));
    }

    private void initData() {
        String hint = StringUtils.isEmpty(title) ? "" : title;
        dialogTitle.setTitle(hint);
        etwcContent.setHint(hint);
        if (!StringUtils.isEmpty(oldValue)) {
            etwcContent.setText(oldValue);
        }
        etwcContent.post(KeyboardUtils::showSoftInput);
    }

    private void performClick() {
        String value = etwcContent.getText().toString().trim();
        if (StringUtils.isEmpty(value)) {
            Toast.makeText(getContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listener != null) listener.onSelected(value);
        KeyboardUtils.hideSoftInput(etwcContent);
        dismiss();
    }
}
