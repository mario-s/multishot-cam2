package de.mario.camera.process

import com.nhaarman.mockito_kotlin.*
import de.mario.camera.exif.ExifTagWriteable
import de.mario.camera.glue.SettingsAccessable
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.opencv.core.Mat
import org.springframework.test.util.ReflectionTestUtils
import java.io.File


@RunWith(JUnitPlatform::class)
object ExposureMergeServiceTest : Spek({

    describe("the exposure merge service") {

        val proxy: OpenCvProxy = mock()
        val exifWriter: ExifTagWriteable = mock()
        val mat: Mat = mock()
        val settingsAccess: SettingsAccessable = mock()

        val classUnderTest = ExposureMergeService()
        ReflectionTestUtils.setField(classUnderTest, "exifWriter", exifWriter)
        ReflectionTestUtils.setField(classUnderTest, "proxy", proxy)
        ReflectionTestUtils.setField(classUnderTest, "settingsAccess", settingsAccess)

        beforeEachTest {
            reset(proxy, mat)
        }

        it("should merge images") {
            given(proxy.read(any<File>())).willReturn(mat)
            given(proxy.merge(any())).willReturn(mat)

            classUnderTest.process(arrayOf("FOO.png", "BAR.png"))

            val order = Mockito.inOrder(proxy, settingsAccess)
            order.verify(proxy, atLeastOnce()).read(any<File>())
            order.verify(proxy).merge(any())
            order.verify(settingsAccess).isEnabled(anyInt())
        }

    }
})