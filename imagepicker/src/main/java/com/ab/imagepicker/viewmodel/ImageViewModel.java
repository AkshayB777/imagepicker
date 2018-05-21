package com.ab.imagepicker.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;
import android.net.Uri;

import com.ab.imagepicker.BR;

/**
 * View model for image list.
 */
public class ImageViewModel extends BaseObservable {

    private long mId;
    private Uri mUri;
    private String mImagePath;
    private boolean mIsSelected;
    private ObservableField<Boolean> mIsLoading = new ObservableField<>(true);

    public ImageViewModel(long id, Uri uri, String imagePath) {
        mId = id;
        mUri = uri;
        mImagePath = imagePath;
    }

    @Bindable
    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
        notifyPropertyChanged(BR.uri);
    }

    @Bindable
    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
        notifyPropertyChanged(BR.imagePath);
    }

    @Bindable
    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
        notifyPropertyChanged(BR.selected);
    }

    public ObservableField<Boolean> getIsLoading() {
        return mIsLoading;
    }
}
