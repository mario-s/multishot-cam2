package de.mario.camera


import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import de.mario.camera.CameraState.Available.STATE_WAITING_LOCK
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

/**
 *
 */
@RunWith(JUnitPlatform::class)
object CaptureCallbackTest : Spek( {

    describe("the capture call back") {

        val state = CameraState()
        val capturable = mock(Captureable::class.java)
        val classUnderTest = CaptureProgressCallback(state, capturable)

        val session = mock(CameraCaptureSession::class.java)
        val request = mock(CaptureRequest::class.java)

        it("should capture picture") {
            state.currentState = STATE_WAITING_LOCK

            val captureResult = mock(TotalCaptureResult::class.java)
            classUnderTest.onCaptureCompleted(session, request, captureResult)

            verify(capturable).capturePicture()
        }

    }
})