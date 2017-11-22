package de.mario.camera

import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest.Builder
import android.view.Surface
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.notNullValue
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset


/**
 */
@RunWith(JUnitPlatform::class)
object CameraDeviceProxyTest : Spek({

    describe("the camera proxy") {

        val cameraDevice = mock(CameraDevice::class.java)
        val target = mock(Surface::class.java)
        val classUnderTest = CameraDeviceProxy()
        classUnderTest.cameraDevice = cameraDevice

        beforeEachTest {
            reset(cameraDevice)
        }

        it("should return false when actual device is present") {
            assertThat(classUnderTest.isClosed(), CoreMatchers.`is`(false))
        }

        it("should create a CaptureRequest.Builder") {
            val type = 0;
            given(cameraDevice.createCaptureRequest(type)).willReturn(mock(Builder::class.java))
            val builder = classUnderTest.createCaptureRequest(type, target)
            assertThat(builder, notNullValue())
        }
    }
})