package com.boredream.baseapplication.image.upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.Utils;
import com.boredream.baseapplication.entity.dto.FileUploadPolicyDTO;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.ProgressRequestBody;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 图片上传
 */
public class ImageUploadUtils {

    public static final String UPLOAD_KEY = "common";

    /**
     * 上传
     */
    public static void startUpload(String filepath, @NonNull OnImageUploadListener listener) {
        // 压缩大小
        int size = getDefaultCompressSize();
        // 压缩质量
        int quality = getDefaultCompressQuality();
        startUpload(filepath, size, quality, listener);
    }

    /**
     * 上传
     */
    public static void startUpload(String filepath, int compressSize, int quality, @NonNull OnImageUploadListener listener) {
        Log.i("DDD", "startUpload: " + filepath);

        Glide.with(Utils.getApp())
                .asBitmap()
                .load(filepath)
                .apply(new RequestOptions()
                        .override(compressSize, compressSize)
                        .format(DecodeFormat.PREFER_RGB_565))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        // 质量压缩到70，并保存到文件
                        upload(bitmap, quality, filepath, listener);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        listener.onError(new IllegalArgumentException("compress image - load image failed"));
                    }
                });
    }

    private static FileUploadPolicyDTO uploadPolicyDTO;
    private static void upload(Bitmap bitmap, int quality, String filepath, OnImageUploadListener listener) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> listener.onProgressChanged(1));

        new Thread(() -> {
            try {
                File uploadFile = new File(FileUtils.getUploadDir(), FileUtils.createUploadPhotoName());
                FileOutputStream fos = new FileOutputStream(uploadFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
                Log.i("DDD", "ImageUtils compress success " + bitmap.getWidth() + ":" + bitmap.getHeight());

                ProgressRequestBody imageBody = new ProgressRequestBody(uploadFile,
                        percentage -> handler.post(() -> listener.onProgressChanged(percentage)));
                MultipartBody.Part part = MultipartBody.Part.createFormData(
                        "file", "image.jpeg", imageBody);

                // 获取上传凭证
                HttpRequest.getInstance()
                        .getApiService()
                        .getFileGetUploadPolicy()
                        .compose(RxComposer.defaultResponse())
                        .flatMap((Function<FileUploadPolicyDTO, ObservableSource<HashMap<String, String>>>) dto -> {
                            uploadPolicyDTO = dto;

                            RequestBody tokenRequestBody = RequestBody.create(
                                    MediaType.parse("multipart/form-data"), dto.getToken());
                            RequestBody keyRequestBody = RequestBody.create(
                                    MediaType.parse("multipart/form-data"), com.blankj.utilcode.util.FileUtils.getFileName(uploadFile));

                            // 上传
                            return HttpRequest.getInstance()
                                    .getDataStreamApiService()
                                    .uploadFile7niu(dto.getUploadHost(), part, tokenRequestBody, keyRequestBody);
                        })
                        .compose(RxComposer.schedulers())
                        .subscribe(new SimpleObserver<HashMap<String, String>>() {
                            @Override
                            public void onNext(HashMap<String, String> response) {
                                handler.post(() -> {
                                    String url = uploadPolicyDTO.getDownloadHost() + response.get("key");
                                    Log.i("DDD", "ImageUtils upload success = " + url);
                                    listener.onSuccess(url);
                                });

                                try {
                                    uploadFile.delete();
                                } catch (Exception e) {
                                    Log.i("DDD", "ImageUtils delete upload file error");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                handler.post(() -> {
                                    Log.i("DDD", "ImageUtils upload error");
                                    listener.onError(e);
                                });

                                try {
                                    uploadFile.delete();
                                } catch (Exception exception) {
                                    Log.i("DDD", "ImageUtils delete upload file error");
                                }
                            }
                        });
            } catch (FileNotFoundException e) {
                handler.post(() -> {
                    Log.i("DDD", "ImageUtils upload error");
                    listener.onError(e);
                });
            }
        }).start();
    }

    public static boolean saveImage2Local(Drawable drawable) {
        File imageDir = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/");
        if (!imageDir.exists()) {
            boolean mkdir = imageDir.mkdir();
            if (!mkdir) return false;
        }
        String fileName = "sfa_" + System.currentTimeMillis() + ".jpg";
        File file = new File(imageDir, fileName);
        try {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            Bitmap bitmap = bd.getBitmap();

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        Utils.getApp().sendBroadcast(intent);
        return true;
    }

    public static int getDefaultCompressSize() {
        return 1024;
    }

    public static int getAvatarCompressSize() {
        return 600;
    }


    public static int getDefaultCompressQuality() {
        return 70;
    }

    public static int getAvatarCompressQuality() {
        return 50;
    }

    public static interface OnImageUploadListener {
        void onProgressChanged(int percentage);

        void onSuccess(String url);

        void onError(Throwable e);
    }
}
