package com.ab.imagepicker.listeners;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Listener for providing selected image Uri list.
 */
public interface ImageSelectionCompletionListener {

    void onImageSelectionCompleted(ArrayList<Uri> imageUriList);
}
