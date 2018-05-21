package com.ab.imagepicker.model;

import android.net.Uri;

/**
 * Image model.
 */
public class Image {

    public final long mId;
    public final Uri mUri;
    public final String mImagePath;

    public Image(long id, Uri uri, String imagePath) {
        mId = id;
        mUri = uri;
        mImagePath = imagePath;
    }
}
