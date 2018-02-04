package de.mario.camera

import android.app.Fragment
import android.graphics.Point
import android.util.Log
import android.util.Size
import android.view.Surface
import de.mario.camera.SizeFilter.chooseOptimalSize
import de.mario.camera.glue.CameraDeviceProxyable


internal class PreviewSizeFactory(private val fragment: Fragment, private val cameraProxy: CameraDeviceProxyable) {

    private fun defaultDisplay() = fragment.activity.windowManager.defaultDisplay

    private companion object {
        val TAG = "PreviewSizeFactory"

        /**
         * Max preview width that is guaranteed by Camera2 API
         */
        val MAX_PREVIEW_WIDTH = 1920

        /**
         * Max preview height that is guaranteed by Camera2 API
         */
        val MAX_PREVIEW_HEIGHT = 1080
    }


    fun createPreviewSize(origin: Size): Size {

        // Find out if we need to swap dimension to get the preview size relative to sensor
        // coordinate.
        val swappedDimensions = swapDimensions()
        val width = origin.width
        val height = origin.height

        val displaySize = Point()
        defaultDisplay().getSize(displaySize)
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

        val largest = SizeFilter.max(cameraProxy.imageSizes())

        // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
        // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
        // garbage capture data.
        return chooseOptimalSize(cameraProxy.surfaceSizes(),
                Size(rotatedPreviewWidth, rotatedPreviewHeight), Size(maxPreviewWidth,
                maxPreviewHeight), largest)
    }

    private fun swapDimensions(): Boolean {
        val displayRotation = defaultDisplay().rotation
        val sensorOrientation = cameraProxy.sensorOrientation()
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