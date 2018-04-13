package de.mario.camera.device

import android.app.Fragment
import android.hardware.camera2.CameraCharacteristics

/**
 * This class searches for the id of the camera that we are going to use.
 */
class CameraLookup(val fragment: Fragment) {

    private val cameraManagerSupply = CameraManagerSupply(fragment)

    fun findCameraId(): String? {
        var id: String? = null
        val manager = cameraManagerSupply.cameraManager()
        for (cameraId in manager.cameraIdList) {
            val characteristics = cameraManagerSupply.cameraCharacteristics(cameraId)

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