package com.boredream.baseapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.activity.ImageBrowserActivity;
import com.boredream.baseapplication.entity.ImageInfo;
import com.boredream.baseapplication.image.picker.OnPickImageListener;
import com.boredream.baseapplication.image.picker.PickImageActivity;
import com.boredream.baseapplication.image.upload.ImageUploadUtils;
import com.boredream.baseapplication.net.GlideHelper;
import com.boredream.baseapplication.utils.DialogUtils;
import com.boredream.baseapplication.view.decoration.GridDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageGridView extends RecyclerView {

    private int maxCount;
    private int compressSize;
    private int quality;
    private boolean onlyCamera;
    private ImageGridAdapter adapter;
    private ArrayList<ImageInfo> infoList = new ArrayList<>();
    private OnPickImageListener listener;

    public List<ImageInfo> getInfoList() {
        if (infoList == null) infoList = new ArrayList<>();
        return infoList;
    }

    public void setCompressSize(int compressSize) {
        this.compressSize = compressSize;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public void setListener(OnPickImageListener listener) {
        this.listener = listener;
    }

    public void setInfoList(ArrayList<ImageInfo> infoList) {
        this.infoList = infoList;
    }

    public void setOnlyCamera(boolean onlyCamera) {
        this.onlyCamera = onlyCamera;
    }

    public ImageGridView(Context context) {
        super(context);
        initView(context);
    }

    public ImageGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ImageGridView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        // 初始化默认值
        maxCount = 9;
        compressSize = ImageUploadUtils.getDefaultCompressSize();
        quality = ImageUploadUtils.getDefaultCompressQuality();
        setOverScrollMode(OVER_SCROLL_NEVER);
        setItemAnimator(null);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        setLayoutManager(layoutManager);
        addItemDecoration(new GridDecoration());
        adapter = new ImageGridAdapter();
        setAdapter(adapter);
    }

    public String getImages() {
        if (CollectionUtils.isEmpty(infoList)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (ImageInfo info : infoList) {
            sb.append(",");
            if (!StringUtils.isEmpty(info.getUrl())) {
                // 优先取url，无需再次上传
                sb.append(info.getUrl());
            } else {
                sb.append(info.getPath());
            }
        }
        return sb.substring(1);
    }

    public void setImages(String imageUrls) {
        if (StringUtils.isEmpty(imageUrls)) {
            return;
        }

        infoList.clear();
        for (String url : imageUrls.split(",")) {
            ImageInfo info = new ImageInfo();
            info.setUrl(url);
            infoList.add(info);
        }
        adapter.notifyDataSetChanged();
    }

    public void setImages(List<ImageInfo> infoList) {
        this.infoList.clear();
        if (infoList != null) {
            this.infoList.addAll(infoList);
        }
        adapter.notifyDataSetChanged();
    }

    public void addLocalImage(String path) {
        if (adapter == null) return;
        if (infoList.size() >= maxCount) return;

        ImageInfo info = new ImageInfo();
        info.setPath(path);
        infoList.add(info);
        adapter.notifyDataSetChanged();
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        adapter.notifyDataSetChanged();
    }

    private class ImageGridAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_grid, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean showAdd = infoList.size() < maxCount && position == infoList.size();
            if (showAdd) {
                holder.ivImageRemove.setVisibility(View.GONE);
                holder.ivImage.setScaleType(ImageView.ScaleType.CENTER);
                holder.ivImage.setImageResource(R.drawable.ic_image_add);
                holder.ivImage.setOnClickListener(v -> {
                    DialogUtils.showImagePickDialog(getContext(), new OnPickImageListener() {
                        @Override
                        public void onCamera() {
                            if (listener != null) {
                                listener.onCamera();
                            }
                            if (getContext() instanceof PickImageActivity) {
                                ((PickImageActivity) getContext()).takeCamera();
                            }
                        }

                        @Override
                        public void onAlbum() {
                            if (listener != null) {
                                listener.onAlbum();
                            }
                            if (getContext() instanceof PickImageActivity) {
                                ((PickImageActivity) getContext()).takeAlbum();
                            }
                        }
                    });
                });
            } else {
                ImageInfo imageInfo = infoList.get(position);
                holder.ivImageRemove.setVisibility(View.VISIBLE);
                holder.ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                GlideHelper.loadRoundedImg(holder.ivImage, imageInfo.getImageShowModel(), 12);
                holder.ivImageRemove.setOnClickListener(v -> deleteImage(imageInfo));
                holder.ivImage.setOnClickListener(v -> browseImage(position));
            }
        }

        private void browseImage(int position) {
            ImageBrowserActivity.start(getContext(), infoList, position);
        }

        private void deleteImage(ImageInfo imageInfo) {
            DialogUtils.showDeleteConfirmDialog(getContext(),
                    v -> {
                        // 如果手动处理删除，则不做remove操作
                        infoList.remove(imageInfo);
                        notifyDataSetChanged();
                    });
        }

        @Override
        public int getItemCount() {
            int count = infoList.size();
            if (count < maxCount) count++;
            return count;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.iv_image_remove)
        ImageView ivImageRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
