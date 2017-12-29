package de.mario.camera.device

import android.app.Fragment
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager


internal class CameraManagerSupply(val fragment: Fragment) {

    fun cameraManager() = fragment.activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    fun cameraCharacteristics(cameraId: String): CameraCharacteristics = cameraManager().getCameraCharacteristics(cameraId)
}