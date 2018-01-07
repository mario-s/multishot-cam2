package de.mario.camera.process

import com.nhaarman.mockito_kotlin.*
import de.mario.camera.exif.ExifTagWriteable
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.springframework.test.util.ReflectionTestUtils


@RunWith(JUnitPlatform::class)
object ExposureMergeServiceTest : Spek({

    describe("the exposure merge service") {

        val proxy: OpenCvProxy = mock()
        val exifWriter: ExifTagWriteable = mock()
        val merger: Merger = mock()
        val mat: Mat = mock()
        val scalar: Scalar = mock()
        val classUnderTest = ExposureMergeService()
        ReflectionTestUtils.setField(classUnderTest, "merger", merger)
        ReflectionTestUtils.setField(classUnderTest, "exifWriter", exifWriter)
        ReflectionTestUtils.setField(classUnderTest, "proxy", proxy)

        beforeEachTest {
            reset(proxy, mat)
            given(proxy.mat()).willReturn(mat)
            given(proxy.scalar(anyDouble(), anyDouble(), anyDouble())).willReturn(scalar)
        }

        it("should merge images") {
            given(proxy.read(anyString())).willReturn(mat)
            given(merger.merge(any())).willReturn(mat)

            classUnderTest.process(arrayOf("FOO.png", "BAR.png"))

            val order = Mockito.inOrder(proxy, merger)
            order.verify(proxy, atLeastOnce()).read(anyString())
            order.verify(merger).merge(any())
        }

    }
})