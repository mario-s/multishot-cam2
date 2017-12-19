package de.mario.camera

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest.Builder
import android.os.Handler
import android.util.Range
import android.view.Surface
import com.nhaarman.mockito_kotlin.mock
import org.hamcrest.CoreMatchers.`is`
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


/**
 */
@RunWith(JUnitPlatform::class)
object CameraDeviceProxyTest : Spek({
    val ID = "foo"

    describe("the camera proxy") {

        val cameraDevice = mock(CameraDevice::class.java)
        val target = mock(Surface::class.java)
        val cameraManager = mock(CameraManager::class.java)
        val characteristics = mock(CameraCharacteristics::class.java)
        val builder = mock(Builder::class.java)
        var classUnderTest: CameraDeviceProxy? = null

        beforeEachTest {
            val fragment = mock(Fragment::class.java)
            val activity = mock(Activity::class.java)

            given(fragment.activity).willReturn(activity)
            given(activity.getSystemService(Context.CAMERA_SERVICE)).willReturn(cameraManager)
            given(cameraManager.getCameraCharacteristics(ID)).willReturn(characteristics)

            classUnderTest = CameraDeviceProxy(fragment)
            classUnderTest!!.cameraDevice = cameraDevice
            classUnderTest!!.cameraId = ID
        }

        it("should return false when actual device is present") {
            assertThat(classUnderTest!!.isClosed(), `is`(false))
        }

        it("should create a CaptureRequest.Builder") {
            val type = 0;
            given(cameraDevice.createCaptureRequest(type)).willReturn(builder)
            val result = classUnderTest!!.createCaptureRequest(type, target)
            assertThat(result, notNullValue())
        }

        it("should forward open camera call") {
            val callBack = mock(CameraDevice.StateCallback::class.java)
            val handler = mock(Handler::class.java)
            classUnderTest?.openCamera(callBack, handler)
            Mockito.verify(cameraManager).openCamera(ID, callBack, handler)
        }

        it("should create a collection of capture requests") {
            val range:Range<Int> = mock()
            given(range.lower).willReturn(-4)
            given(range.upper).willReturn(4)

            given(characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)).willReturn(range)
            given(cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)).willReturn(builder)

            val target = mock(Surface::class.java)
            val result = classUnderTest!!.createBurstRequests(0, target)
            assertThat(result.isEmpty(), `is`(false))
        }
    }
})