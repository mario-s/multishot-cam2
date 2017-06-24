package de.mario.camera

import android.app.Fragment
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import android.util.Size
import android.view.Surface
import de.mario.camera.SizeHelper.chooseOptimalSize


class PreviewSizeFactory(val fragment: Fragment) {

    private val TAG = "PreviewSizeFactory"

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private val MAX_PREVIEW_WIDTH = 1920

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private val MAX_PREVIEW_HEIGHT = 1080


    private val activity = fragment.activity

    fun createPreviewSize(characteristics: CameraCharacteristics, origin: Size): Size {
        val map = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val largest: Size = SizeHelper.findLargestSize(characteristics)


        // Find out if we need to swap dimension to get the preview size relative to sensor
        // coordinate.
        val swappedDimensions = swapDimensions(characteristics)
        val width = origin.width
        val height = origin.height

        val displaySize = Point()
        activity.windowManager.defaultDisplay.getSize(displaySize)
        var rotatedPreviewWidth = width
        var rotatedPreviewHeight = height
        var maxPreviewWidth = displaySize.x
        var maxPreviewHeight = displaySize.y

        if (swappedDimensions) {
            rotatedPreviewWidth = height
            rotatedPreviewHeight = width
            maxPreviewWidth = displaySize.y
            maxPreviewHeight = displaySize.x
        }

        if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
            maxPreviewWidth = MAX_PREVIEW_WIDTH
        }

        if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
            maxPreviewHeight = MAX_PREVIEW_HEIGHT
        }

        // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
        // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
        // garbage capture data.
        return chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java),
                rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                maxPreviewHeight, largest)
    }

    private fun swapDimensions(characteristics: CameraCharacteristics): Boolean {
        val displayRotation = activity.windowManager.defaultDisplay.rotation
        val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
        var swappedDimensions = false
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> if (sensorOrientation == 90 || sensorOrientation == 270) {
                swappedDimensions = true
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> if (sensorOrientation == 0 || sensorOrientation == 180) {
                swappedDimensions = true
            }
            else -> Log.e(TAG, "Display rotation is invalid: " + displayRotation)
        }
        return swappedDimensions
    }
}