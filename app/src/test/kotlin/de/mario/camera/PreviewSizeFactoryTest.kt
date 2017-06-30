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
import org.hamcrest.CoreMatchers.equalTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.isNull
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

/**
 *
 */
@RunWith(JUnitPlatform::class)
object PreviewSizeFactoryTest : Spek( {

    describe("the preview size factory") {

        val fragment = mock(Fragment::class.java)
        val classUnderTest = PreviewSizeFactory(fragment)

        val min = mock(Size::class.java)
        val max = mock(Size::class.java)

        beforeEachTest {
            given(max.width).willReturn(100)
            given(max.height).willReturn(100)

            val activity = mock(Activity::class.java)
            val windowManager = mock(WindowManager::class.java)
            val display = mock(Display::class.java)

            given(fragment.activity).willReturn(activity)
            given(activity.windowManager).willReturn(windowManager)
            given(windowManager.defaultDisplay).willReturn(display)
        }

        it("should return the minimum size") {
            val characteristic = mock(CameraCharacteristics::class.java)
            val map = mock(StreamConfigurationMap::class.java)
            val arr = arrayOf(min, max)

            given(map.getOutputSizes(ImageFormat.JPEG)).willReturn(arr)
            given(map.getOutputSizes(SurfaceTexture::class.java)).willReturn(arr)
            given(characteristic.get(isNull(CameraCharacteristics.Key::class.java))).willReturn(map).willReturn(0)

            val result = classUnderTest.createPreviewSize(characteristic, max)
            assertThat(result, equalTo(min))
        }

    }
})