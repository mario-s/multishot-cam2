package de.mario.camera.glue

import android.hardware.camera2.CameraCharacteristics
import android.util.Size

interface CameraDeviceProxyable {

    fun getCameraCharacteristics(): CameraCharacteristics

    fun surfaceSizes(): Array<Size>

    fun imageSizes(): Array<Size>
}