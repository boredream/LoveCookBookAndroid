package com.boredream.baseapplication.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;

import com.boredream.baseapplication.R;

public class BottomDialog extends Dialog {

    public BottomDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog);

        //设置窗口出现和窗口隐藏的动画
        getWindow().setWindowAnimations(R.style.DialogAnimation);
    }

    @Override
    public void show() {
        // Dialog的位置置底
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }

        super.show();
    }

}
