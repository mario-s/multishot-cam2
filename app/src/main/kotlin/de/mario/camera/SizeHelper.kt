package de.mario.camera

import android.util.Log
import android.util.Size
import java.util.*


/**
 */
object SizeHelper {
    /*
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     */
    fun chooseOptimalSize(choices: Array<Size>, textureViewWidth: Int,
                          textureViewHeight: Int, maxWidth: Int, maxHeight: Int, aspectRatio: Size): Size {

        // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough = ArrayList<Size>()
        // Collect the supported resolutions that are smaller than the preview Surface
        val notBigEnough = ArrayList<Size>()
        val w = aspectRatio.width
        val h = aspectRatio.height
        for (option in choices) {
            if (option.width <= maxWidth && option.height <= maxHeight &&
                    option.height == option.width * h / w) {
                if (option.width >= textureViewWidth && option.height >= textureViewHeight) {
                    bigEnough.add(option)
                } else {
                    notBigEnough.add(option)
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size > 0) {
            return Collections.min(bigEnough, CameraFragment.CompareSizesByArea())
        } else if (notBigEnough.size > 0) {
            return Collections.max(notBigEnough, CameraFragment.CompareSizesByArea())
        } else {
            Log.w("SizeHelper", "Couldn't find any suitable preview size")
            return choices[0]
        }
    }
}