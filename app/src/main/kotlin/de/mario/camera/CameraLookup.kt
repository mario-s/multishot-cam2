package de.mario.camera

import android.app.Fragment
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler

/**
 */
class CameraLookup(val fragment: Fragment) {

    private val cameraManagerSupply = CameraManagerSupply(fragment)

    fun findCameraId(): String? {
        var id: String? = null
        val manager = cameraManagerSupply.cameraManager()
        for (cameraId in manager.cameraIdList) {
            val characteristics = cameraManagerSupply.getCameraCharacteristics(cameraId)

            // We don't use a front facing camera in this sample.
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (CameraCharacteristics.LENS_FACING_FRONT == facing) {
                continue
            }
            id = cameraId
        }
        return id
    }
}