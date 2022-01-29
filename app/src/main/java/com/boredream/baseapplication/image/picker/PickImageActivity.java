package com.boredream.baseapplication.image.picker;

import android.Manifest;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.base.BaseActivity;
import com.boredream.baseapplication.utils.PermissionUtils;
import com.yanzhenjie.permission.AndPermission;

import java.util.ArrayList;
import java.util.List;

/**
 * 选图拍照的基类
 */
public class PickImageActivity extends BaseActivity implements PickImageAble {

    private Integer cusCameraReqCode;
    private Integer cusAlbumReqCode;
    private List<OnSinglePick> onSinglePickList = new ArrayList<>();
    private List<OnPick> onPickList = new ArrayList<>();

    /**
     * 调用摄像头
     */
    @Override
    public void takeCamera(boolean facingFront) {
        takeCameraWithReqCode(null, facingFront);
    }

    /**
     * 调用摄像头，自定义requestCode，如果用WithReqCode方法，请使用onPickImageResult获取
     */
    @Override
    public void takeCameraWithReqCode(Integer requestCode, boolean facingFront) {
        cusCameraReqCode = requestCode;

        AndPermission.with(this)
                .runtime()
                .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(permissions -> ImagePickerHelper.fromCamera(this, requestCode, facingFront))
                .onDenied(permissions -> {
                    if (AndPermission.hasAlwaysDeniedPermission(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        PermissionUtils.showSetting(this, permissions);
                    }
                })
                .start();
    }

    @Override
    public void takeAlbum() {
        takeAlbumWithReqCode(null);
    }

    @Override
    public void takeAlbumWithReqCode(Integer requestCode) {
        cusAlbumReqCode = requestCode;

        AndPermission.with(this)
                .runtime()
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(permissions -> ImagePickerHelper.fromAlbum(this, requestCode))
                .onDenied(permissions -> {
                    if (AndPermission.hasAlwaysDeniedPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        PermissionUtils.showSetting(this, permissions);
                    }
                })
                .start();
    }

    @Override
    public void addOnSinglePick(OnSinglePick onSinglePick) {
        onSinglePickList.add(onSinglePick);
    }

    @Override
    public void addOnPick(OnPick onPick) {
        onPickList.add(onPick);
    }

    /**
     * 默认无自定义requestCode的选图回调
     *
     * @param path 图片绝对路径
     */
    protected void onSinglePickImageResult(@NonNull String path) {
        for (OnSinglePick onSinglePick : onSinglePickList) {
            onSinglePick.onSinglePickImageResult(path);
        }
    }

    /**
     * 自定义requestCode的选图回调
     *
     * @param path 图片绝对路径
     */
    protected void onPickImageResultWithRequestCode(int requestCode, @NonNull String path) {
        for (OnPick onPick : onPickList) {
            onPick.onPickImageResultWithRequestCode(requestCode, path);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        if (cusAlbumReqCode != null && requestCode == cusAlbumReqCode) {
            String path = ImagePickerHelper.getAlbumImage(this, data);
            if (path != null) onPickImageResultWithRequestCode(cusAlbumReqCode, path);
        } else if (cusCameraReqCode != null && requestCode == cusCameraReqCode) {
            String path = ImagePickerHelper.getCameraImage();
            if (path != null) {
                onPickImageResultWithRequestCode(cusCameraReqCode, path);
            }
        } else {
            String imageFilePath = ImagePickerHelper.getImageResult(this, requestCode, data);
            if (!StringUtils.isEmpty(imageFilePath)) {
                onSinglePickImageResult(imageFilePath);
            }
        }
    }


}
