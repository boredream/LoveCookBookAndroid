package com.boredream.baseapplication.image.picker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImagePickerHelper {

    public static final String KEY_USE_CAMERA = "useCamera";
    public static final String FACING_FRONT = "facingFront";

    public static final int DEF_REQ_CODE_ALBUM = 0x301;
    public static final int DEF_REQ_CODE_CAMERA = 0x302;
    private static File cameraFile;

    public static void fromAlbum(Activity activity, Integer requestCode) {
        if (requestCode == null) requestCode = DEF_REQ_CODE_ALBUM;
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intentToPickPic, requestCode);
    }

    public static void fromAlbum(Fragment fragment, Integer requestCode) {
        if (requestCode == null) requestCode = DEF_REQ_CODE_ALBUM;
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        fragment.startActivityForResult(intentToPickPic, requestCode);
    }

    private static boolean initCameraIntent(Activity context, Intent takePictureIntent) {
        takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) == null) {
            return true;
        }
        final String cameraOutDir = getExternalDCIM();
        // ????????????????????????
        final File dir = new File(cameraOutDir);
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs) return true;
        }

        // ????????????????????????
        String filename = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
                .format(new Date()) + ".png";
        cameraFile = new File(dir, filename);
        Uri uri = getFileUri(context, cameraFile);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return false;
    }

    /**
     * ??????
     */
    public static void fromCamera(Activity activity, Integer requestCode, boolean facing) {
        if (requestCode == null) requestCode = DEF_REQ_CODE_CAMERA;
        Intent takePictureIntent = new Intent();
        takePictureIntent.putExtra(FACING_FRONT, facing);
        if (initCameraIntent(activity, takePictureIntent)) return;
        activity.startActivityForResult(takePictureIntent, requestCode);
    }

    /**
     * ??????
     */
    public static void fromCamera(Fragment fragment, Integer requestCode, boolean facing) {
        if (requestCode == null) requestCode = DEF_REQ_CODE_CAMERA;
        Intent takePictureIntent = new Intent();
        takePictureIntent.putExtra(FACING_FRONT, facing);
        if (initCameraIntent(fragment.getActivity(), takePictureIntent)) return;
        fragment.startActivityForResult(takePictureIntent, requestCode);
    }

    /**
     * ??????Camera????????????DCIM????????????
     */
    private static String getExternalDCIM() {
        String result = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (file != null) {
                result = file.getAbsolutePath() + "/images";
            }
        }
        return result;
    }

    /**
     * ??????Uri?????????7.0+ ??????
     */
    public static Uri getFileUri(@NonNull Context context, @NonNull File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".file.provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public static String getImageResult(Context context, int requestCode, Intent data) {
        String path = null;
        if (requestCode == DEF_REQ_CODE_ALBUM) {
            path = getRealPathFromUri(context, data.getData());
        } else if (requestCode == DEF_REQ_CODE_CAMERA) {
            if (cameraFile != null) {
                path = cameraFile.getAbsolutePath();
            }
        }
        return path;
    }

    /**
     * ?????????requestCode???????????????????????????????????????requestCode
     */
    public static String getAlbumImage(Context context, Intent data) {
        return getRealPathFromUri(context, data.getData());
    }

    /**
     * ?????????requestCode???????????????????????????????????????requestCode
     */
    public static String getCameraImage() {
        String path = null;
        if (cameraFile != null) {
            path = cameraFile.getAbsolutePath();
        }
        return path;
    }

    /**
     * ??????Uri???????????????????????????
     *
     * @param context ???????????????
     * @param uri     ?????????Uri
     * @return ??????Uri?????????????????????, ????????????????????????????????????, ????????????null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) { // api >= 19
            return getRealPathFromUriAboveApi19(context, uri);
        } else { // api < 19
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    /**
     * ??????api19??????(?????????api19),??????uri???????????????????????????
     *
     * @param context ???????????????
     * @param uri     ?????????Uri
     * @return ??????Uri?????????????????????, ????????????????????????????????????, ????????????null
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * ??????api19?????????,??????uri???????????????????????????
     *
     * @param context ???????????????
     * @param uri     ?????????Uri
     * @return ??????Uri?????????????????????, ????????????????????????????????????, ????????????null
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ?????????document????????? uri, ?????????document id???????????????
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // ??????':'??????
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // ????????? content ????????? Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // ????????? file ????????? Uri,?????????????????????????????????
            filePath = uri.getPath();
        } else {
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * ???????????????????????? _data ???????????????Uri?????????????????????
     *
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

}
