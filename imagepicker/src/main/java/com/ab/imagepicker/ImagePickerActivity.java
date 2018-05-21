package com.ab.imagepicker;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;

import com.ab.imagepicker.adapter.ImagePickerAdapter;
import com.ab.imagepicker.databinding.ActivityImagePickerBinding;
import com.ab.imagepicker.listeners.ImageSelectionChangeListener;
import com.ab.imagepicker.viewmodel.ImageLoaderViewModel;

import java.util.ArrayList;

/**
 * Call this activity using startActivityForResult();
 *
 * After successful image selection, get the list of image uri in onActivityResult();
 * Use EXTRA_SELECTED_IMAGES key in result intent to get list of image uri.
 */
public class ImagePickerActivity extends AppCompatActivity implements ImageSelectionChangeListener {

    public static final String EXTRA_SELECTED_IMAGES = "selected_images";
    public static final String EXTRA_MIN_SELECTION = "min_selection";
    public static final String EXTRA_MAX_SELECTION = "max_selection";
    private static final int STORAGE_REQUEST_CODE = 100;
    private ImagePickerAndroidViewModel mImagePickerViewModel;
    private ImageLoaderViewModel mImageLoaderViewModel;
    private int mMinSelection = 1;
    private int mMaxSelection = Integer.MAX_VALUE;
    private ActivityImagePickerBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_picker);
        obtainSelectionLimit();
        init();
        ImagePickerAdapter adapter = setAdapter(mBinding);
        observeForData(adapter);
        //Check read storage permission
        ActivityCompat.requestPermissions(this
                , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        onImageSelectionChanged(0);
    }

    private void obtainSelectionLimit() {
        Bundle extras = getIntent().getExtras();
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

    private void init() {
        setSupportActionBar(mBinding.toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle();
        mImageLoaderViewModel = new ImageLoaderViewModel();
        mImageLoaderViewModel.setErrorMessage(getString(R.string.images_not_available));
        mBinding.setViewModel(mImageLoaderViewModel);
        mBinding.setHandler(this);
    }

    private void setTitle() {
        if (mMinSelection > 0) {
            String title;
            if (mMinSelection == 1) {
                title = String.format(getString(R.string.select_atleast_image), mMinSelection);
            } else {
                title = String.format(getString(R.string.select_atleast_images), mMinSelection);
            }
            mBinding.toolbar.setTitle(title);
        }
    }

    private ImagePickerAdapter setAdapter(ActivityImagePickerBinding binding) {
        ImagePickerAdapter adapter = new ImagePickerAdapter(null, this, mMaxSelection);
        binding.rvImages.setLayoutManager(new GridLayoutManager(this, 3));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                Snackbar.make(mBinding.getRoot(), "Storage Permissions required"
                        , Snackbar.LENGTH_INDEFINITE).setAction("Okay", __ -> finish()).show();
            }
        }
    }

    @Override
    public void onImageSelectionChanged(int selectedImageCount) {
        if (mMaxSelection == Integer.MAX_VALUE) {
            mBinding.tvSelectionProgress.setText(String.format(getString(R.string.num_images_selected)
                    , selectedImageCount));
            mImageLoaderViewModel.setImageSelectionCompleted(selectedImageCount > 0 && selectedImageCount >= mMinSelection);
        } else {
            mBinding.tvSelectionProgress.setText(String.format(getString(R.string.num_num_selected_image)
                    , selectedImageCount, mMaxSelection));
            mImageLoaderViewModel.setImageSelectionCompleted(selectedImageCount <= mMaxSelection && selectedImageCount >= mMinSelection);
        }
    }

    public void onDoneClick() {
        ArrayList<Uri> selectedImageUriList
                = ((ImagePickerAdapter) mBinding.rvImages.getAdapter()).getSelectedImageUriList();
        Intent intent = getIntent();
        intent.putExtra(EXTRA_SELECTED_IMAGES, selectedImageUriList);
        setResult(RESULT_OK, intent);
        finish();
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

        public Intent build(Context context) {
            Intent intent = new Intent(context, ImagePickerActivity.class);
            intent.putExtra(ImagePickerActivity.EXTRA_MIN_SELECTION, mMinSelection);
            intent.putExtra(ImagePickerActivity.EXTRA_MAX_SELECTION, mMaxSelection);
            return intent;
        }
    }
}