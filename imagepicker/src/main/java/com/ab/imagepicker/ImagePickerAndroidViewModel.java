package com.ab.imagepicker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ab.imagepicker.model.Image;
import com.ab.imagepicker.utils.ImageFetcher;
import com.ab.imagepicker.viewmodel.ImageViewModel;

import java.util.ArrayList;

/**
 * ViewModel for image picker.
 */
public class ImagePickerAndroidViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<ImageViewModel>> mImageListData = new MutableLiveData<>();

    public ImagePickerAndroidViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ArrayList<ImageViewModel>> getImageListData() {
        return mImageListData;
    }

    public void fetchImagesFromGallery() {
        new ImageFetcher(getApplication().getContentResolver()
                , images -> new Thread(() -> {
            ArrayList<ImageViewModel> imageViewModels = new ArrayList<>();
            for (Image image : images) {
                imageViewModels.add(new ImageViewModel(image.mId, image.mUri, image.mImagePath));
            }
            mImageListData.postValue(imageViewModels);
        }).start()).execute();
    }
}