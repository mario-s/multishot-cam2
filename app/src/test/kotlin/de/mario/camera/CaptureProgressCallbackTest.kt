package de.mario.camera


import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.hamcrest.CoreMatchers.`is`

/**
 *
 */
@RunWith(JUnitPlatform::class)
object CaptureProgressCallbackTest : Spek( {

    describe("the capture call back") {

        val state = CameraState()
        val capturable = mock(Captureable::class.java)
        val classUnderTest = CaptureProgressCallback(state, capturable)

        val session = mock(CameraCaptureSession::class.java)
        val request = mock(CaptureRequest::class.java)

        beforeEachTest {
            state.currentState = CameraState.STATE_PREVIEW
        }

        it("should capture picture") {
            state.currentState = CameraState.STATE_WAITING_LOCK

            val captureResult = mock(TotalCaptureResult::class.java)
            classUnderTest.onCaptureCompleted(session, request, captureResult)

            verify(capturable).capturePicture()
        }

        it("should wait for non precapture") {
            state.currentState = CameraState.STATE_WAITING_PRECAPTURE

            val captureResult = mock(TotalCaptureResult::class.java)
            classUnderTest.onCaptureCompleted(session, request, captureResult)

            assertThat(state.currentState, `is`(CameraState.STATE_WAITING_NON_PRECAPTURE))
        }

    }
})