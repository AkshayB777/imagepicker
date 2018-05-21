package com.ab.imagepicker;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.ab.imagepicker.adapter.ImagePickerAdapter;
import com.ab.imagepicker.databinding.DialogFragmentImagePickerBinding;
import com.ab.imagepicker.listeners.ImageSelectionChangeListener;
import com.ab.imagepicker.listeners.ImageSelectionCompletionListener;
import com.ab.imagepicker.viewmodel.ImageLoaderViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Bottom sheet dialog fragment showing image picker.
 * <p>
 * Use ImagePickerDialogFragment.newInstance() to create the instance.
 * Pass the listener when creating new instance.
 * Call dialogFragment.show() as usual to show the dialog.
 */
public class ImagePickerDialogFragment extends DialogFragment implements ImageSelectionChangeListener {

    public static final String EXTRA_MIN_SELECTION = "min_selection";
    public static final String EXTRA_MAX_SELECTION = "max_selection";
    private static final int STORAGE_REQUEST_CODE = 100;
    private ImagePickerAndroidViewModel mImagePickerViewModel;
    private ImageLoaderViewModel mImageLoaderViewModel;
    private int mMinSelection = 1;
    private int mMaxSelection = Integer.MAX_VALUE;
    private DialogFragmentImagePickerBinding mImagePickerBinding;
    private WeakReference<ImageSelectionCompletionListener> mCompletionListenerWeakReference;

    public static ImagePickerDialogFragment newInstance(int minSelection, int maxSelection
            , ImageSelectionCompletionListener listener) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_MIN_SELECTION, minSelection);
        args.putInt(EXTRA_MAX_SELECTION, maxSelection);
        ImagePickerDialogFragment fragment = new ImagePickerDialogFragment();
        fragment.setImageSelectionCompletionListener(listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mImagePickerBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_image_picker
                , container, false);
        return mImagePickerBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        obtainSelectionLimit();
        init();
        ImagePickerAdapter adapter = setAdapter(mImagePickerBinding);
        observeForData(adapter);
        //Check read storage permission
        FragmentActivity activity = getActivity();
        assert activity != null;
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        onImageSelectionChanged(0);
    }

    private void obtainSelectionLimit() {
        Bundle extras = getArguments();
        if (extras != null) {
            int extraMinSelection = extras.getInt(EXTRA_MIN_SELECTION);
            int extraMaxSelection = extras.getInt(EXTRA_MAX_SELECTION);
            if (!(extraMinSelection < 0 && extraMaxSelection < 0)) {
                mMinSelection = extraMinSelection;
                if (extraMaxSelection != 0 && extraMaxSelection >= extraMinSelection) {
                    mMaxSelection = extraMaxSelection;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            View decorView = activity.getWindow().getDecorView();
            int screenWidth = decorView.getWidth();
            int screenHeight = decorView.getHeight();
            Window window = getDialog().getWindow();
            if (window != null) {
                window.setLayout((int) (screenWidth - screenWidth * 0.1), (int) (screenHeight - screenHeight * 0.2));
            }
        }
    }

    private void init() {
        setTitle();
        mImageLoaderViewModel = new ImageLoaderViewModel();
        mImageLoaderViewModel.setErrorMessage(getString(R.string.images_not_available));
        mImagePickerBinding.setViewModel(mImageLoaderViewModel);
        mImagePickerBinding.setHandler(this);
    }

    private void setTitle() {
        String title = getString(R.string.app_name);
        if (mMinSelection > 0) {
            if (mMinSelection == 1) {
                title = String.format(getString(R.string.select_atleast_image), mMinSelection);
            } else {
                title = String.format(getString(R.string.select_atleast_images), mMinSelection);
            }
        }
        mImagePickerBinding.tvTitle.setText(title);
    }

    private ImagePickerAdapter setAdapter(DialogFragmentImagePickerBinding binding) {
        ImagePickerAdapter adapter = new ImagePickerAdapter(null, this, mMaxSelection);
        binding.rvImages.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvImages.setAdapter(adapter);
        return adapter;
    }

    private void observeForData(ImagePickerAdapter adapter) {
        mImagePickerViewModel = ViewModelProviders.of(this).get(ImagePickerAndroidViewModel.class);
        mImagePickerViewModel.getImageListData().observe(this, imageViewModels -> {
            mImageLoaderViewModel.setLoading(false);
            adapter.setViewModels(imageViewModels);
            mImageLoaderViewModel.setShowErrorMessage(imageViewModels == null || imageViewModels.size() == 0);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mImagePickerViewModel.fetchImagesFromGallery();
            } else {
                mImageLoaderViewModel.setLoading(false);
                mImageLoaderViewModel.setShowErrorMessage(true);
                Snackbar.make(mImagePickerBinding.getRoot(), "Storage Permissions required"
                        , Snackbar.LENGTH_INDEFINITE).setAction("Okay", __ -> dismiss()).show();
            }
        }
    }

    public void onDoneClick() {
        ArrayList<Uri> selectedImageUriList
                = ((ImagePickerAdapter) mImagePickerBinding.rvImages.getAdapter()).getSelectedImageUriList();
        if (mCompletionListenerWeakReference != null) {
            ImageSelectionCompletionListener completionListener = mCompletionListenerWeakReference.get();
            if (completionListener != null) {
                completionListener.onImageSelectionCompleted(selectedImageUriList);
            }
        }
        dismiss();
    }

    @Override
    public void onImageSelectionChanged(int selectedImageCount) {
        if (mMaxSelection == Integer.MAX_VALUE) {
            mImagePickerBinding.tvSelectionProgress.setText(String.format(getString(R.string.num_images_selected)
                    , selectedImageCount));
            mImageLoaderViewModel.setImageSelectionCompleted(selectedImageCount > 0 && selectedImageCount >= mMinSelection);
        } else {
            mImagePickerBinding.tvSelectionProgress.setText(String.format(getString(R.string.num_num_selected_image)
                    , selectedImageCount, mMaxSelection));
            mImageLoaderViewModel.setImageSelectionCompleted(selectedImageCount <= mMaxSelection && selectedImageCount >= mMinSelection);
        }
    }

    private void setImageSelectionCompletionListener(ImageSelectionCompletionListener listener) {
        mCompletionListenerWeakReference = new WeakReference<>(listener);
    }

    public static class Builder {

        private int mMinSelection;
        private int mMaxSelection;

        public Builder setMinSelection(int minSelection) {
            mMinSelection = minSelection;
            return this;
        }

        public Builder setMaxSelection(int maxSelection) {
            mMaxSelection = maxSelection;
            return this;
        }

        public ImagePickerDialogFragment build(ImageSelectionCompletionListener listener) {
            return ImagePickerDialogFragment.newInstance(mMinSelection
                    , mMaxSelection, listener);
        }
    }
}