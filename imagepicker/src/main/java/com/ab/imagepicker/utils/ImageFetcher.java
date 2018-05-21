package com.ab.imagepicker.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.ab.imagepicker.listeners.ImageFetchListener;
import com.ab.imagepicker.model.Image;

import java.util.ArrayList;

public final class ImageFetcher extends AsyncTask<Void, Void, ArrayList<Image>> {

        private ContentResolver mContentResolver;
        private ImageFetchListener mListener;

        public ImageFetcher(ContentResolver contentResolver, ImageFetchListener listener) {
            mContentResolver = contentResolver;
            mListener = listener;
        }

        @Override
        protected ArrayList<Image> doInBackground(Void... voids) {
            ArrayList<Image> images = new ArrayList<>();
            Cursor imageCursor = null;
            try {
                final String[] columns = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media.HEIGHT, MediaStore.Images.Media.WIDTH};
                final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
                imageCursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        , columns, null, null, orderBy);
                while (imageCursor != null && imageCursor.moveToNext()) {
                    long _id = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                    String imagePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(_id));
                    Image image = new Image(_id, uri, imagePath);
                    images.add(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (imageCursor != null && !imageCursor.isClosed()) {
                    imageCursor.close();
                }
            }
            return images;
        }

        @Override
        protected void onPostExecute(ArrayList<Image> images) {
            super.onPostExecute(images);
            if (mListener != null) {
                mListener.onImageFetched(images);
            }
        }
    }