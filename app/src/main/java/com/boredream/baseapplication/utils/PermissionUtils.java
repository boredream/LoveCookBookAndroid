package com.boredream.baseapplication.utils;

import android.content.Context;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.runtime.setting.SettingRequest;

import java.util.List;

public final class PermissionUtils {

    public static void showSetting(Context context, List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        StringBuilder sbMsg = new StringBuilder();
        sbMsg.append("需要以下权限，请在设置中开启：");
        for (String permissionName : permissionNames) {
            sbMsg.append("\n").append(permissionName);
        }

        final SettingRequest setting = AndPermission.with(context).runtime().setting();
        DialogUtils.show2BtnDialog(context, "提示", sbMsg.toString(), true,
                "不同意", "设置", v -> {}, v -> setting.start(0));
    }
}