package de.mario.camera


import android.app.Fragment
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import android.media.Image
import de.mario.camera.CameraState.Available.STATE_WAITING_LOCK
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.rules.TemporaryFolder
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import java.nio.ByteBuffer
import kotlin.reflect.KFunction

/**
 *
 */
@RunWith(JUnitPlatform::class)
object CaptureCallbackTest : Spek( {

    describe("the capture call back") {

        val state = CameraState()
        val fragment = mock(Fragment::class.java)
        val method = fragment::onStart
        val classUnderTest = CaptureCallback(state, method, method)

        val session = mock(CameraCaptureSession::class.java)
        val request = mock(CaptureRequest::class.java)

        it("should capture picture") {
            state.currentState = STATE_WAITING_LOCK

            val captureResult = mock(TotalCaptureResult::class.java)
            classUnderTest.onCaptureCompleted(session, request, captureResult)

            verify(fragment).onStart()
        }

    }
})