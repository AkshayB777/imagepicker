package com.ab.imagepicker.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.ab.imagepicker.databinding.ItemImageBinding;
import com.ab.imagepicker.listeners.ImageSelectionChangeListener;
import com.ab.imagepicker.viewmodel.ImageViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Adapter class for imagePicker.
 */
public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.ImagePickViewHolder> {

    private ArrayList<ImageViewModel> mImageViewModels;
    private ImageSelectionChangeListener mImageSelectionChangeListener;
    private int mMaxSelectableCount;
    @SuppressLint("UseSparseArrays")
    private HashMap<Long, Uri> mSelectedImageUriList = new HashMap<>();

    public ImagePickerAdapter(ArrayList<ImageViewModel> imageViewModels
            , ImageSelectionChangeListener imageSelectionChangeListener, int maxSelectableCount) {
        mImageViewModels = imageViewModels;
        mImageSelectionChangeListener = imageSelectionChangeListener;
        mMaxSelectableCount = maxSelectableCount;
    }

    @NonNull
    @Override
    public ImagePickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageBinding itemImageBinding =
                ItemImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ImagePickViewHolder(itemImageBinding, mSelectedImageUriList
                , mImageSelectionChangeListener, mMaxSelectableCount);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePickViewHolder holder, int position) {
        holder.mItemImageBinding.setImageModel(mImageViewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return mImageViewModels == null ? 0 : mImageViewModels.size();
    }

    public void setViewModels(ArrayList<ImageViewModel> viewModels) {
        mImageViewModels = viewModels;
        notifyDataSetChanged();
    }

    public ArrayList<Uri> getSelectedImageUriList() {
        return new ArrayList<>(mSelectedImageUriList.values());
    }

    public static final class ImagePickViewHolder extends RecyclerView.ViewHolder {

        private ItemImageBinding mItemImageBinding;
        private HashMap<Long, Uri> mSelectedImageUriList;
        private ImageSelectionChangeListener mImageSelectionChangeListener;
        private int mMaxSelectableCount;

        public ImagePickViewHolder(ItemImageBinding itemImageBinding
                , HashMap<Long, Uri> selectedImageUriList
                , ImageSelectionChangeListener imageSelectionChangeListener, int maxSelectableCount) {
            super(itemImageBinding.getRoot());
            mItemImageBinding = itemImageBinding;
            mSelectedImageUriList = selectedImageUriList;
            mImageSelectionChangeListener = imageSelectionChangeListener;
            mMaxSelectableCount = maxSelectableCount;
            mItemImageBinding.setHandler(this);
            final ConstraintLayout parent = mItemImageBinding.parent;
            parent.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int parentWidth = parent.getWidth();
                            int paddingWidth = 0;
                            ViewGroup.LayoutParams layoutParams = mItemImageBinding.ivImage.getLayoutParams();
                            int size = parentWidth - paddingWidth;
                            layoutParams.width = size;
                            layoutParams.height = size;
                        }
                    });
        }

        public void onItemClicked() {
            ImageViewModel imageModel = mItemImageBinding.getImageModel();
            if (imageModel != null) {
                boolean selected = imageModel.isSelected();
                if (!selected && mSelectedImageUriList.size() < mMaxSelectableCount) {
                    mSelectedImageUriList.put(imageModel.getId(), imageModel.getUri());
                    imageModel.setSelected(true);
                } else if (selected && mSelectedImageUriList.size() <= mMaxSelectableCount) {
                    mSelectedImageUriList.remove(imageModel.getId());
                    imageModel.setSelected(false);
                }
                if (mImageSelectionChangeListener != null && mSelectedImageUriList.size() <= mMaxSelectableCount) {
                    mImageSelectionChangeListener.onImageSelectionChanged(mSelectedImageUriList.size());
                }
            }
        }
    }
}