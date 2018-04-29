package de.mario.camera.device

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest.Builder
import android.hardware.camera2.params.StreamConfigurationMap
import android.os.Handler
import android.util.Range
import android.util.Size
import android.view.Surface
import com.nhaarman.mockito_kotlin.reset
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
import com.nhaarman.mockito_kotlin.mock
import de.mario.camera.glue.SettingsAccessable
import org.mockito.ArgumentMatchers.anyInt


/**
 */
@RunWith(JUnitPlatform::class)
object CameraDeviceProxyTest : Spek({
    val ID = "foo"

    describe("the camera proxy") {

        val settingsAccess: SettingsAccessable = mock()
        val cameraDevice: CameraDevice = mock()
        val target: Surface = mock()
        val cameraManager: CameraManager = mock()
        val characteristics: CameraCharacteristics = mock()
        val builder: Builder = mock()
        val fragment: Fragment = mock()
        val activity: Activity = mock()

        given(fragment.activity).willReturn(activity)
        given(fragment.context).willReturn(activity)
        given(activity.getSystemService(Context.CAMERA_SERVICE)).willReturn(cameraManager)
        given(cameraManager.getCameraCharacteristics(ID)).willReturn(characteristics)
        given(activity.getString(anyInt())).willReturn("0")

        val classUnderTest = object : CameraDeviceProxy(fragment) {
            override fun settings(): SettingsAccessable = settingsAccess
        }
        classUnderTest.cameraDevice = cameraDevice
        classUnderTest.cameraId = ID

        beforeEachTest {
            reset(characteristics)
        }

        it("should return false when actual device is present") {
            assertThat(classUnderTest.isClosed(), `is`(false))
        }

        it("should create a CaptureRequest.Builder") {
            val type = 0;
            given(cameraDevice.createCaptureRequest(type)).willReturn(builder)
            val result = classUnderTest.createCaptureRequest(type, target)
            assertThat(result, notNullValue())
        }

        it("should forward open camera call") {
            val callBack: CameraDevice.StateCallback = mock()
            val handler: Handler = mock()
            classUnderTest.openCamera(callBack, handler)
            Mockito.verify(cameraManager).openCamera(ID, callBack, handler)
        }

        it("should create a collection of capture requests") {
            val range: Range<Int> = mock()
            given(range.lower).willReturn(-4)
            given(range.upper).willReturn(4)

            given(characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)).willReturn(range)
            given(cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)).willReturn(builder)

            val result = classUnderTest.createBurstRequests(0, target)
            assertThat(result.isEmpty(), `is`(false))
        }


        it("should return a list of image resolutions") {
            val map: StreamConfigurationMap = mock()

            given(characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).willReturn(map)
            given(map.getOutputSizes(ImageFormat.JPEG)).willReturn(arrayOf(Size(1, 1)))

            val result = classUnderTest.imageSizes()

            assertThat(result.isEmpty(), `is`(false))
        }
    }
})