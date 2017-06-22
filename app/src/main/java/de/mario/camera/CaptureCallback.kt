package de.mario.camera

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.TotalCaptureResult

/**
 */
class CaptureCallback(val camState: CameraState,  val precaptureSequence: () -> Unit, val capturePicture: () -> Unit) :
        CameraCaptureSession.CaptureCallback() {

    override fun onCaptureProgressed(session: CameraCaptureSession,
                                     request: CaptureRequest,
                                     partialResult: CaptureResult) {
        process(partialResult)
    }

    override fun onCaptureCompleted(session: CameraCaptureSession,
                                    request: CaptureRequest,
                                    result: TotalCaptureResult) {
        process(result)
    }

    private fun process(result: CaptureResult) {
        when (camState.currentState) {
            CameraState.STATE_WAITING_LOCK -> {
                val afState = result.get(CaptureResult.CONTROL_AF_STATE)
                if (afState == null) {
                    capturePicture()
                } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                        CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                    // CONTROL_AE_STATE can be null on some devices
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                        camState.currentState = CameraState.STATE_PICTURE_TAKEN
                        capturePicture()
                    } else {
                        precaptureSequence()
                    }
                }
            }
            CameraState.STATE_WAITING_PRECAPTURE -> {
                // CONTROL_AE_STATE can be null on some devices
                val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                if (aeState == null ||
                        aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                        aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                    camState.currentState = CameraState.STATE_WAITING_NON_PRECAPTURE
                }
            }
            CameraState.STATE_WAITING_NON_PRECAPTURE -> {
                // CONTROL_AE_STATE can be null on some devices
                val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                    camState.currentState = CameraState.STATE_PICTURE_TAKEN
                    capturePicture()
                }
            }
        }
    }
}