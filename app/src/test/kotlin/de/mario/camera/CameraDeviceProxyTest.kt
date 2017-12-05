package de.mario.camera

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest.Builder
import android.os.Handler
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
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset


/**
 */
@RunWith(JUnitPlatform::class)
object CameraDeviceProxyTest : Spek({
    val ID = "foo"

    describe("the camera proxy") {

        val cameraDevice = mock(CameraDevice::class.java)
        val target = mock(Surface::class.java)
        val cameraManager = mock(CameraManager::class.java)
        var classUnderTest: CameraDeviceProxy? = null

        beforeEachTest {
            val fragment = mock(Fragment::class.java)
            val activity = mock(Activity::class.java)

            given(fragment.activity).willReturn(activity)
            given(activity.getSystemService(Context.CAMERA_SERVICE)).willReturn(cameraManager)

            classUnderTest = CameraDeviceProxy(fragment)
            classUnderTest!!.cameraDevice = cameraDevice
            classUnderTest!!.cameraId = ID
        }

        it("should return false when actual device is present") {
            assertThat(classUnderTest!!.isClosed(), CoreMatchers.`is`(false))
        }

        it("should create a CaptureRequest.Builder") {
            val type = 0;
            given(cameraDevice.createCaptureRequest(type)).willReturn(mock(Builder::class.java))
            val builder = classUnderTest!!.createCaptureRequest(type, target)
            assertThat(builder, notNullValue())
        }

        it("should forward open camera call") {
            val callBack = mock(CameraDevice.StateCallback::class.java)
            val handler = mock(Handler::class.java)
            classUnderTest?.openCamera(callBack, handler)
            Mockito.verify(cameraManager).openCamera(ID, callBack, handler)
        }
    }
})