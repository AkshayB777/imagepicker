package com.ab.imagepicker.utils;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Custom adapter class for data binding.
 */
public final class DataBindingCustomAdapters {

    @BindingAdapter(value = {"app:imagePath", "app:selected", "app:loading"}, requireAll = false)
    public static void loadImage(ImageView imageView, String imagePath, boolean selected
            , ObservableField<Boolean> loading) {
        Glide.with(imageView.getContext())
                .load(imagePath)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model
                            , Target<Drawable> target, boolean isFirstResource) {
                        if (loading != null) {
                            loading.set(false);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model
                            , Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (loading != null) {
                            loading.set(false);
                        }
                        return false;
                    }
                })
                .into(imageView);
        imageView.setSelected(selected);
    }
}