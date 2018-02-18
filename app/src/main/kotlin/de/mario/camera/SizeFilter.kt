package de.mario.camera

import android.util.Log
import android.util.Size
import java.util.*
import java.util.Collections.*


/**
 */
object SizeFilter {
    private data class Result(val bigEnough: ArrayList<Size>, val notBigEnough: ArrayList<Size>)

    /**
     * Try to create a Size based on the given string.
     */
    fun parse(input: String): Size? {
        var result: Size? = null
        if(!input.isEmpty()) {
            val pair = input.split("x").map { it.toInt() }
            if(pair.size > 1) {
                result = Size(pair.first(), pair.last())
            }
        }
        return result
    }

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
            Log.w("SizeFilter", "Couldn't find any suitable preview size")
            return choices[0]
        }
    }

    private fun filterChoices(choices: Array<Size>, textureSize: Size, maxSize: Size, aspectRatio: Size): Result {
        // Collect the supported imageResolutions that are at least as big as the preview Surface
        val bigEnough = ArrayList<Size>()
        // Collect the supported imageResolutions that are smaller than the preview Surface
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

    fun max(sizes: Array<Size>) = Collections.max(sizes.asList(), CompareSizesByArea())

    internal class CompareSizesByArea : Comparator<Size> {

        override fun compare(lhs: Size, rhs: Size): Int {
            //cast here to ensure the multiplications won't overflow
            val l = lhs.width.toLong() * lhs.height
            val r = rhs.width.toLong() * rhs.height
            return java.lang.Long.signum(l - r)
        }
    }
}