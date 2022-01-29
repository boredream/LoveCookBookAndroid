package com.boredream.baseapplication.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Base64;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class FileUtils {
    private static final String MIME_TYPE_APK = "application/vnd.android.package-archive";
    private static final String FILE_MARK_NAME_MERGE = "merge";
    private static final String FILE_MARK_NAME = "markName=";

    public static String getFileString(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }

        String json = builder.toString();
        if (StringUtils.isEmpty(json)) {
            throw new IOException("no file");
        }

        return json;
    }

    public static String getAssetsFileString(String fileName) throws IOException {
        AssetManager manager = Utils.getApp().getAssets();

        InputStream is = manager.open(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }

        String json = builder.toString();
        if (StringUtils.isEmpty(json)) {
            throw new IOException("no file");
        }

        return json;
    }

    /**
     * 获取App根目录
     */
    public static File getAppDir() {
        if (!hasSdcard()) return null;
        File file = new File(Environment.getExternalStorageDirectory(), "lovecookbook");
        if (!file.exists()) {
            boolean success = file.mkdirs();
            if (!success) return null;
        }
        return file;
    }

    /**
     * 获取App上传目录
     */
    public static File getUploadDir() {
        File dir = getAppDir();
        if (dir == null) return null;
        File file = new File(dir, "upload");
        if (!file.exists()) {
            boolean success = file.mkdirs();
            if (!success) return null;
        }
        return file;
    }

    public static String createUploadPhotoName() {
        return "upload_" + System.currentTimeMillis() + ".jpg";
    }

    /**
     * 获取App下载目录
     */
    public static File getDownloadDir() {
        File dir = getAppDir();
        if (dir == null) return null;
        File file = new File(dir, "download");
        if (!file.exists()) {
            boolean success = file.mkdirs();
            if (!success) return null;
        }
        return file;
    }

    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static void delete(File dir, FilenameFilter filenameFilter) {
        if (dir == null) return;
        File[] files = dir.listFiles(filenameFilter);
        if (files == null) return;
        for (File file : files) {
            file.delete();
        }
    }

    public static File writeToDisk(InputStream inputStream, File file) throws IOException {
        OutputStream outputStream = null;
        try {
            byte[] fileReader = new byte[4096];
            long fileSizeDownloaded = 0;
            outputStream = new FileOutputStream(file);
            while (true) {
                int read = inputStream.read(fileReader);

                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
                fileSizeDownloaded += read;
            }
            outputStream.flush();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
        return file;
    }

    public static boolean hasFileInAsset(Context context, String filePath) {
        AssetManager am = context.getAssets();
        String folder;
        String file;
        int divisionIndex = filePath.lastIndexOf("/");
        if (divisionIndex == -1 || divisionIndex == filePath.length() - 1) {
            folder = "";
            file = filePath;
        } else {
            folder = filePath.substring(0, divisionIndex);
            file = filePath.substring(divisionIndex + 1);
        }
        try {
            return Arrays.asList(am.list(folder)).contains(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String fileToBase64(String path) throws Exception {
        InputStream in = new FileInputStream(path);
        byte[] bytes = new byte[in.available()];
        in.read(bytes);
        in.close();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static void Base64ToFile(String base64, String path) throws Exception {
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(path);
        out.write(bytes);
        out.close();
    }

    public static File saveBitmapIntoJPG(Context context, Bitmap bitmap, String name) throws FileNotFoundException {
        File file = new File(getImageDir(context), name);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        return file;
    }

    // 获取存放图片的文件夹
    public static File getImageDir(Context context) {
        return context.getExternalFilesDir("images");
    }

}
