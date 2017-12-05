package de.mario.camera

import android.app.Fragment
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager


open class CameraManagerSupply(val fragment: Fragment) {

    fun cameraManager() = fragment.activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    fun getCameraCharacteristics(cameraId: String): CameraCharacteristics = cameraManager().getCameraCharacteristics(cameraId)
}