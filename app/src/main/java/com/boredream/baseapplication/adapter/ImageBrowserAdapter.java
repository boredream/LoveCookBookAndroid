package com.boredream.baseapplication.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.blankj.utilcode.util.ScreenUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.dialog.BottomSelectDialog;
import com.boredream.baseapplication.image.upload.ImageUploadUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ImageBrowserAdapter extends PagerAdapter {

    private Activity context;
    private LayoutInflater layoutInflater;
    private Map<Integer, WeakReference<ImageView>> imageViews = new HashMap<>();
    private ArrayList images;

    public ImageBrowserAdapter(Activity context, ArrayList images) {
        this.context = context;
        this.images = images;
        layoutInflater = LayoutInflater.from(context);
    }

    public ImageView getImageView(int position) {
        int index = position % images.size();
        WeakReference<ImageView> weakReference = imageViews.get(index);
        if (weakReference == null) return null;
        return weakReference.get();
    }

    public Bitmap getBitmap(int position) {
        ImageView imageView = getImageView(position);
        if (imageView == null) return null;
        Bitmap bitmap = null;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            bitmap = bd.getBitmap();
        }
        return bitmap;
    }

    private String getUrls(int position) {
        return images.get(position % images.size()).toString();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(final ViewGroup container, int position) {
        final View rootView = layoutInflater.inflate(R.layout.item_image_browser, container, false);

        final PhotoView iv_image_browser = rootView.findViewById(R.id.iv_image_browser);
        final View pb_loading = rootView.findViewById(R.id.pb_loading);

        final PhotoViewAttacher pva = new PhotoViewAttacher(iv_image_browser);

        String url = getUrls(position);
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().override(
                        ScreenUtils.getScreenWidth(),
                        ScreenUtils.getScreenHeight()))
                .into(new SimpleTarget<Drawable>() {

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        pb_loading.setVisibility(View.GONE);
                        iv_image_browser.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        pb_loading.setVisibility(View.VISIBLE);
                        iv_image_browser.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        pb_loading.setVisibility(View.GONE);
                        iv_image_browser.setVisibility(View.VISIBLE);

                        iv_image_browser.setImageDrawable(resource);
                        pva.update();

                        pva.setOnLongClickListener(v -> {
                            BottomSelectDialog dialog = new BottomSelectDialog(context, null, Arrays.asList("保存到相册"),
                                    (parent, view, position1, id) -> {
                                        if (position1 == 0) {
                                            boolean saveSuccess = ImageUploadUtils.saveImage2Local(resource);
                                            Toast.makeText(context, "图片保存" + (saveSuccess ? "成功" : "失败"), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            dialog.show();
                            return true;
                        });
                    }
                });

        pva.setOnPhotoTapListener((view, x, y) -> context.onBackPressed());

        imageViews.put(position % images.size(), new WeakReference<>(iv_image_browser));
        container.addView(rootView);
        return rootView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        imageViews.remove(position % images.size());
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}