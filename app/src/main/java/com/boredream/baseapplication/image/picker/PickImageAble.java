package com.boredream.baseapplication.image.picker;

import androidx.annotation.NonNull;

public interface PickImageAble {

    default void takeCamera() {
        takeCamera(false);
    }

    void takeCamera(boolean facingFront);

    default void takeCameraWithReqCode(Integer requestCode) {
        takeCameraWithReqCode(requestCode, false);
    }

    void takeCameraWithReqCode(Integer requestCode, boolean facingFront);

    void takeAlbum();

    void takeAlbumWithReqCode(Integer requestCode);


    interface OnSinglePick {
        void onSinglePickImageResult(String path);
    }

    interface OnPick {
        void onPickImageResultWithRequestCode(int requestCode, @NonNull String path);
    }

    void addOnSinglePick(OnSinglePick onSinglePick);

    void addOnPick(OnPick onPick);

}
