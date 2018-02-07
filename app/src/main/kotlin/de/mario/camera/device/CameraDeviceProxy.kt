package de.mario.camera.device

import android.app.Fragment
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession.StateCallback
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.StreamConfigurationMap
import android.os.Handler
import android.util.Log
import android.util.Range
import android.util.Size
import android.view.Surface
import de.mario.camera.glue.CameraDeviceProxyable

class CameraDeviceProxy(fragment: Fragment) : CameraDeviceProxyable{
    private val managerSupply = CameraManagerSupply(fragment)
    internal var cameraDevice: CameraDevice? = null
    internal var cameraId: String? = null

    companion object {
        const val TAG = "CameraDeviceProxy"
    }


    fun openCamera(callback: CameraDevice.StateCallback, handler: Handler)  {
        managerSupply.cameraManager().openCamera(cameraId, callback, handler)
    }

    fun getCameraCharacteristics(): CameraCharacteristics = managerSupply.cameraCharacteristics(cameraId!!)

    fun close() {
        cameraDevice?.close()
        cameraDevice = null
    }

    fun isClosed(): Boolean {
        return cameraDevice == null
    }

    fun createCaptureSession(outputs: List<Surface>, callback: StateCallback, handler: Handler? = null) {
        cameraDevice?.createCaptureSession(outputs, callback, handler)
    }

    fun createCaptureRequest(templateType: Int, target: Surface): CaptureRequest.Builder? {
        val builder = captureRequest(templateType, target)
        setAuto(builder)
        return builder
    }

    private fun captureRequest(templateType: Int, target: Surface): CaptureRequest.Builder? {
        val builder = cameraDevice?.createCaptureRequest(templateType)
        builder?.addTarget(target)
        return builder
    }

    fun createBurstRequests(orientation: Int, target: Surface): List<CaptureRequest> {
        val range = exposureCompensationRange()
        val evs = arrayOf(0, range.lower, range.upper)

        Log.d(TAG, "ev: " + evs)

        val builder = captureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE, target)
        setAuto(builder!!)
        builder.set(CaptureRequest.JPEG_ORIENTATION, orientation)

        return buildRequests(builder, evs)
    }

    private fun buildRequests(builder:  CaptureRequest.Builder, evs: Array<Int>): List<CaptureRequest> {
        return evs.map { ev ->
            builder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, ev)
            builder.build()
        }
    }

    private fun exposureCompensationRange(): Range<Int>
            = getCameraCharacteristics().get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)


    private fun setAuto(builder: CaptureRequest.Builder?) {
        // Auto focus should be continuous for camera preview.
        builder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        // Flash is automatically enabled when necessary.
        builder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
    }

    override fun surfaceSizes(): Array<Size> = configMap().getOutputSizes(SurfaceTexture::class.java)

    override fun imageSizes(): Array<Size> = configMap().getOutputSizes(ImageFormat.JPEG)

    override fun sensorOrientation(): Int = getCameraCharacteristics().get(CameraCharacteristics.SENSOR_ORIENTATION)


    private fun configMap(): StreamConfigurationMap = getCameraCharacteristics().get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

}