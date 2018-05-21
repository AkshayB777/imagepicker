package com.ab.imagepicker.listeners;

import com.ab.imagepicker.model.Image;

import java.util.ArrayList;

public interface ImageFetchListener {

    void onImageFetched(ArrayList<Image> images);
}