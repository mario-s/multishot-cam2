package de.mario.camera

import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.params.StreamConfigurationMap
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
    fun chooseOptimalSize(choices: Array<Size>, textureViewSize: Size, maxSize: Size, aspectRatio: Size): Size {

        // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough = ArrayList<Size>()
        // Collect the supported resolutions that are smaller than the preview Surface
        val notBigEnough = ArrayList<Size>()

        val ratio = if(aspectRatio.width > 0) aspectRatio.height / aspectRatio.width else 1
        for (option in choices) {
            val optWidth = option.width
            val optHeight = option.height
            if (optWidth <= maxSize.width && optHeight <= maxSize.height && optHeight == optWidth * ratio) {
                if (optWidth >= textureViewSize.width && optHeight >= textureViewSize.height) {
                    bigEnough.add(option)
                } else {
                    notBigEnough.add(option)
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size > 0) {
            return Collections.min(bigEnough, CompareSizesByArea())
        } else if (notBigEnough.size > 0) {
            return Collections.max(notBigEnough, CompareSizesByArea())
        } else {
            Log.w("SizeHelper", "Couldn't find any suitable preview size")
            return choices[0]
        }
    }

    fun findLargestSize(characteristics: CameraCharacteristics): Size {
        val map = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

        // For still image captures, we use the largest available size.
        return findLargestSize(map)
    }

    fun findLargestSize(map : StreamConfigurationMap) : Size {
        val sizes: List<Size> = map.getOutputSizes(ImageFormat.JPEG).asList()
        return Collections.max(sizes, CompareSizesByArea())
    }

    internal class CompareSizesByArea : Comparator<Size> {

        override fun compare(lhs: Size, rhs: Size): Int {
            // We cast here to ensure the multiplications won't overflow
            val l = lhs.width.toLong() * lhs.height
            val r = rhs.width.toLong() * rhs.height
            return java.lang.Long.signum(l - r)
        }
    }
}