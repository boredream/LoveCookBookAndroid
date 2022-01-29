package com.boredream.baseapplication.net;

import android.widget.ImageView;

import com.blankj.utilcode.util.SizeUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.entity.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class GlideHelper {

    public static void loadImage(ImageView iv, Object model) {
        int defaultImg = R.drawable.rect_gray;
        loadImage(iv, model, defaultImg);
    }

    public static void loadImage(ImageView iv, Object model, Integer defaultImg) {
        RequestOptions options = new RequestOptions().transform(new CenterCrop());

        if (defaultImg != null) {
            options = options.placeholder(defaultImg)
                    .error(defaultImg);
        }

        Glide.with(iv.getContext())
                .load(model)
                .apply(options)
                .into(iv);
    }

    public static void loadRoundedImg(ImageView iv, Object model) {
        loadRoundedImg(iv, model, 4);
    }

    public static void loadRoundedImg(ImageView iv, Object model, int corner) {
        int defaultImg = R.drawable.correct_gray;

        RoundedCorners corners = new RoundedCorners(SizeUtils.dp2px(corner));
        RequestOptions options = new RequestOptions()
                .transform(new MultiTransformation<>(new CenterCrop(), corners))
                .placeholder(defaultImg)
                .error(defaultImg);

        Glide.with(iv.getContext())
                .load(model)
                .apply(options)
                .into(iv);
    }

    public static void loadAvatar(ImageView iv, User user) {
        loadOvalImg(iv, user.getAvatar(), user.getAvatarDefaultImg());
    }

    public static void loadOvalImg(ImageView iv, Object model) {
        loadOvalImg(iv, model, R.drawable.oval_primary_light_solid);
    }

    public static void loadOvalImg(ImageView iv, Object model, Integer defaultImg) {
        CircleCrop circleCrop = new CircleCrop();
        RequestOptions options = new RequestOptions()
                .transform(new MultiTransformation<>(new CenterCrop(), circleCrop));

        if (defaultImg != null) {
            options = options.placeholder(defaultImg)
                    .error(defaultImg);
        }

        Glide.with(iv.getContext())
                .load(model)
                .apply(options)
                .into(iv);
    }
}
