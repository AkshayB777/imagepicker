# Imagepicker
Image picker for android.

## Download [![](https://jitpack.io/v/esafirm/android-image-picker.svg)](https://jitpack.io/#AkshayB777/imagepicker)

Add this to your project's `build.gradle`

```groovy
allprojects {
    repositories {
	      ...
	      maven { url 'https://jitpack.io' }
    }
}
```

And add this to your module's `build.gradle` 

```groovy
dependencies {
    implementation 'com.github.AkshayB777:imagepicker:-SNAPSHOT'
}
```

### Start image picker activity

The simple way to start 

```java
Intent intent = new ImagePickerActivity.Builder()
                .setMinSelection(1)
                .setMaxSelection(5)
                .build(context);
startActivityForResult(intent, requestCode);
``` 

### Receive result

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == requestCode && resultCode == RESULT_OK) {
        ArrayList<Uri> uriList = data
                .getParcelableArrayListExtra(ImagePickerActivity.EXTRA_SELECTED_IMAGES);
    }
}
```

### Start image picker dialog fragment

```java
ImagePickerDialogFragment imagePickerDialogFragment = new ImagePickerDialogFragment.Builder()
                .setMinSelection(1)
                .setMaxSelection(5)
                .build(new ImageSelectionCompletionListener() {
                    @Override
                    public void onImageSelectionCompleted(ArrayList<Uri> imageUriList) {
                        //Here is the selected image list
                    }
                });
imagePickerDialogFragment.show(getSupportFragmentManager(), "picker");
```
