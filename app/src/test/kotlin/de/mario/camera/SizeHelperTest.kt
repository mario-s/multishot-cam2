package de.mario.camera

import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.params.StreamConfigurationMap
import android.util.Size
import org.hamcrest.CoreMatchers.equalTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

/**
 *
 */
@RunWith(JUnitPlatform::class)
object SizeHelperTest : Spek( {

    describe("the size helper") {

        it("should return the maximum size") {
            val min = mock(Size::class.java)
            val max = mock(Size::class.java)
            val characteristic = mock(CameraCharacteristics::class.java)
            val map = mock(StreamConfigurationMap::class.java)

            given(characteristic.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).willReturn(map)
            given(map.getOutputSizes(ImageFormat.JPEG)).willReturn(arrayOf(min, max))
            given(max.width).willReturn(100)
            given(max.height).willReturn(100)

            val result = SizeHelper.findLargestSize(characteristic)
            assertThat(result, equalTo(max))
        }

    }
})