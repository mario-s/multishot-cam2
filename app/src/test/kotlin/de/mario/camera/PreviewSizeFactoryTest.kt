package de.mario.camera

import android.app.Activity
import android.app.Fragment
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.params.StreamConfigurationMap
import android.util.Size
import android.view.Display
import android.view.WindowManager
import com.nhaarman.mockito_kotlin.mock
import de.mario.camera.glue.CameraDeviceProxyable
import org.hamcrest.CoreMatchers.equalTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.isNull
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset

/**
 *
 */
@RunWith(JUnitPlatform::class)
object PreviewSizeFactoryTest : Spek( {

    describe("the preview size factory") {

        val fragment: Fragment = mock()
        val cameraProxy: CameraDeviceProxyable = mock()
        val characteristics: CameraCharacteristics = mock()
        val classUnderTest = PreviewSizeFactory(fragment, cameraProxy)

        val min: Size = mock()
        val max: Size = mock()

        beforeEachTest {
            reset(fragment, cameraProxy, characteristics)
            given(max.width).willReturn(100)
            given(max.height).willReturn(100)

            val activity:Activity = mock()
            val windowManager: WindowManager = mock()
            val display: Display = mock()

            given(fragment.activity).willReturn(activity)
            given(activity.windowManager).willReturn(windowManager)
            given(windowManager.defaultDisplay).willReturn(display)
            given(cameraProxy.getCameraCharacteristics()).willReturn(characteristics)
        }

        it("should return the minimum size") {
            val map: StreamConfigurationMap = mock()
            val arr = arrayOf(min, max)

            given(cameraProxy.imageSizes()).willReturn(arr)
            given(cameraProxy.surfaceSizes()).willReturn(arr)
            given(characteristics.get(isNull<CameraCharacteristics.Key<Any>>())).willReturn(map).willReturn(Integer(0))
            given(characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)).willReturn(90)

            val result = classUnderTest.createPreviewSize(max)
            assertThat(result, equalTo(min))
        }

    }
})