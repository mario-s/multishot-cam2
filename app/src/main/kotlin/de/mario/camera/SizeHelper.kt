package de.mario.camera

import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.params.StreamConfigurationMap
import android.util.Log
import android.util.Size
import java.util.Collections.*


/**
 */
object SizeHelper {
    private data class Result(val bigEnough: ArrayList<Size>, val notBigEnough: ArrayList<Size>)

    /*
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     */
    fun chooseOptimalSize(choices: Array<Size>, textureSize: Size, maxSize: Size, aspectRatio: Size): Size {

        val result = filterChoices(choices, textureSize, maxSize, aspectRatio)

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (result.bigEnough.isNotEmpty()) {
            return min(result.bigEnough, CompareSizesByArea())
        } else if (result.notBigEnough.isNotEmpty()) {
            return max(result.notBigEnough, CompareSizesByArea())
        } else {
            Log.w("SizeHelper", "Couldn't find any suitable preview size")
            return choices[0]
        }
    }

    private fun filterChoices(choices: Array<Size>, textureSize: Size, maxSize: Size, aspectRatio: Size): Result {
        // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough = ArrayList<Size>()
        // Collect the supported resolutions that are smaller than the preview Surface
        val notBigEnough = ArrayList<Size>()

        choices.filter {
            (it.width <= maxSize.width && it.height <= maxSize.height &&
                    it.height == it.width * aspectRatio.height / aspectRatio.width)
        }.forEach {
            if (it.width >= textureSize.width && it.height >= textureSize.height) {
                bigEnough.add(it)
            } else {
                notBigEnough.add(it)
            }
        }

        return Result(bigEnough, notBigEnough)
    }

    fun largestSize(characteristics: CameraCharacteristics): Size = largestSize(configMap(characteristics))

    fun largestSize(map : StreamConfigurationMap) : Size = max(sizes(map), CompareSizesByArea())

    fun smallestSize(characteristics: CameraCharacteristics): Size = smallestSize(configMap(characteristics))

    fun smallestSize(map: StreamConfigurationMap) : Size = min(sizes(map), CompareSizesByArea())

    private fun configMap(characteristics: CameraCharacteristics): StreamConfigurationMap = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

    private fun sizes(map : StreamConfigurationMap): List<Size> = map.getOutputSizes(ImageFormat.JPEG).asList()

    internal class CompareSizesByArea : Comparator<Size> {

        override fun compare(lhs: Size, rhs: Size): Int {
            // We cast here to ensure the multiplications won't overflow
            val l = lhs.width.toLong() * lhs.height
            val r = rhs.width.toLong() * rhs.height
            return java.lang.Long.signum(l - r)
        }
    }
}