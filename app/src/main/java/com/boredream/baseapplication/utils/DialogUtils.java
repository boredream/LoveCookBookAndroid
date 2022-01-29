package com.boredream.baseapplication.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.dialog.BottomSelectDialog;
import com.boredream.baseapplication.dialog.WheelDatePickDialog;
import com.boredream.baseapplication.image.picker.OnPickImageListener;
import com.boredream.baseapplication.image.picker.PickImageActivity;
import com.boredream.baseapplication.listener.OnDatePickListener;

import java.util.Arrays;


/**
 * 对话框工具类, 提供常用对话框显示, 使用support.v7包内的AlertDialog样式
 */
public class DialogUtils {

    public static Dialog showDeleteConfirmDialog(Context context, View.OnClickListener okListener) {
        return DialogUtils.show2BtnDialog(context, "提示", "是否确认删除？", okListener);
    }

    public static Dialog showCalendarPickDialog(Context context, String oldData, OnDatePickListener listener) {
        if (StringUtils.isEmpty(oldData)) {
            oldData = null;
        }
        WheelDatePickDialog dialog = new WheelDatePickDialog(context, oldData);
        dialog.setListener(listener);
        dialog.show();
        return dialog;
    }

    /**
     * 选择拍照还是相册对话框
     */
    public static Dialog showImagePickDialog(PickImageActivity activity) {
        BottomSelectDialog dialog = new BottomSelectDialog(activity,
                Arrays.asList(R.drawable.ic_camera, R.drawable.ic_albums),
                Arrays.asList("拍照", "相册"),
                (parent, view, position, id) -> {
                    if (position == 0) {
                        activity.takeAlbum();
                    } else if (position == 1) {
                        activity.takeAlbum();
                    }
                });
        dialog.show();
        return dialog;
    }

    /**
     * 选择拍照还是相册对话框
     */
    public static Dialog showImagePickDialog(Context context, OnPickImageListener listener) {
        BottomSelectDialog dialog = new BottomSelectDialog(context,
                Arrays.asList(R.drawable.ic_camera, R.drawable.ic_albums),
                Arrays.asList("拍照", "相册"),
                (parent, view, position, id) -> {
                    if (listener == null) return;

                    if (position == 0) {
                        listener.onCamera();
                    } else if (position == 1) {
                        listener.onAlbum();
                    }
                });
        dialog.show();
        return dialog;
    }

    /**
     * 自定义确定按钮/取消对话框
     */
    public static Dialog show2BtnDialog(@NonNull Context context, @NonNull String title, String content, String ok, View.OnClickListener okListener) {
        return show2BtnDialog(context, title, content, true, "取消", ok, null, okListener);
    }

    /**
     * 确定/取消对话框
     */
    public static Dialog show2BtnDialog(@NonNull Context context, @NonNull String title, String content, View.OnClickListener okListener) {
        return show2BtnDialog(context, title, content, true, "取消", "确定", null, okListener);
    }

    /**
     * 俩按钮对话框
     */
    public static Dialog show2BtnDialog(@NonNull Context context,
                                        @NonNull String title,
                                        String content,
                                        boolean autoClose,
                                        String cancel,
                                        String ok,
                                        View.OnClickListener cancelListener,
                                        View.OnClickListener okListener) {
        View view = View.inflate(context, R.layout.dialog_common, null);
        DialogViewHolder holder = new DialogViewHolder(view);

        Dialog dialog = new Dialog(context, R.style.custom_dialog);
        dialog.setContentView(view);
        holder.tv_title.setText(title);
        if (StringUtils.isEmpty(content)) {
            holder.tv_content.setVisibility(View.GONE);
        } else {
            holder.tv_content.setVisibility(View.VISIBLE);
            holder.tv_content.setText(content);
        }
        if (cancel != null) holder.tv_cancel.setText(cancel);
        holder.tv_cancel.setOnClickListener(v -> {
            if (autoClose) dialog.dismiss();
            if (cancelListener != null) cancelListener.onClick(v);
        });
        if (ok != null) holder.tv_ok.setText(ok);
        holder.tv_ok.setOnClickListener(v -> {
            if (autoClose) dialog.dismiss();
            if (okListener != null) okListener.onClick(v);
        });
        dialog.show();

        return dialog;
    }

    /**
     * 单按钮对话框
     */
    public static Dialog show1BtnDialog(Context context, String title, String content) {
        return show1BtnDialog(context, title, content, "确定", null);
    }

    /**
     * 单按钮对话框
     */
    public static Dialog show1BtnDialog(Context context, String title, String content,
                                        String ok, View.OnClickListener okListener) {
        View view = View.inflate(context, R.layout.dialog_common, null);
        DialogViewHolder holder = new DialogViewHolder(view);
        holder.btnDivider.setVisibility(View.GONE);
        holder.tv_cancel.setVisibility(View.GONE);

        Dialog dialog = new Dialog(context, R.style.custom_dialog);
        dialog.setContentView(view);
        holder.tv_title.setText(title);
        if (StringUtils.isEmpty(content)) {
            holder.tv_content.setVisibility(View.GONE);
        } else {
            holder.tv_content.setVisibility(View.VISIBLE);
            holder.tv_content.setText(content);
        }
        if (ok != null) holder.tv_ok.setText(ok);
        holder.tv_ok.setOnClickListener(v -> {
            dialog.dismiss();
            if (okListener != null) okListener.onClick(v);
        });
        dialog.show();

        return dialog;
    }

    static class DialogViewHolder {

        public View btnDivider;
        public TextView tv_title;
        public TextView tv_content;
        public TextView tv_cancel;
        public TextView tv_ok;

        public DialogViewHolder(View view) {
            btnDivider = view.findViewById(R.id.v_btn_divider);
            tv_title = view.findViewById(R.id.alert_tv_title);
            tv_content = view.findViewById(R.id.alert_tv_content);
            tv_cancel = view.findViewById(R.id.alert_tv_cancel);
            tv_ok = view.findViewById(R.id.alert_tv_ok);
        }

    }
}
