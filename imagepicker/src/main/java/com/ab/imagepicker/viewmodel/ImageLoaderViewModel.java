package com.ab.imagepicker.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.ab.imagepicker.BR;

/**
 * View model for image picker activity.
 */
public class ImageLoaderViewModel extends BaseObservable {

    private boolean mIsLoading = true;
    private boolean mShowErrorMessage;
    private boolean mImageSelectionCompleted;
    private String mErrorMessage;

    @Bindable
    public boolean isLoading() {
        return mIsLoading;
    }

    public void setLoading(boolean loading) {
        mIsLoading = loading;
        notifyPropertyChanged(BR.loading);
    }

    @Bindable
    public boolean isShowErrorMessage() {
        return mShowErrorMessage;
    }

    public void setShowErrorMessage(boolean showErrorMessage) {
        mShowErrorMessage = showErrorMessage;
        notifyPropertyChanged(BR.showErrorMessage);
    }

    @Bindable
    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
        notifyPropertyChanged(BR.errorMessage);
    }

    @Bindable
    public boolean isImageSelectionCompleted() {
        return mImageSelectionCompleted;
    }

    public void setImageSelectionCompleted(boolean imageSelectionCompleted) {
        mImageSelectionCompleted = imageSelectionCompleted;
        notifyPropertyChanged(BR.imageSelectionCompleted);
    }
}