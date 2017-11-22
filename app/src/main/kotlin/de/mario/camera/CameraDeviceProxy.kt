package de.mario.camera

import android.hardware.camera2.CameraCaptureSession.StateCallback
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.os.Handler
import android.view.Surface

class CameraDeviceProxy {
    internal var cameraDevice: CameraDevice? = null

    fun close() {
        cameraDevice?.close()
        cameraDevice = null
    }

    fun isClosed(): Boolean {
        return cameraDevice == null
    }

    fun createCaptureRequest(templateType: Int, target: Surface): CaptureRequest.Builder? {
        val builder = cameraDevice?.createCaptureRequest(templateType)
        builder?.addTarget(target)
        setAuto(builder)
        return builder
    }

    private fun setAuto(builder: CaptureRequest.Builder?) {
        // Auto focus should be continuous for camera preview.
        builder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        // Flash is automatically enabled when necessary.
        builder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
    }

    fun createCaptureSession(outputs: List<Surface>, callback: StateCallback, handler: Handler? = null) {
        cameraDevice?.createCaptureSession(outputs, callback, handler)
    }
}