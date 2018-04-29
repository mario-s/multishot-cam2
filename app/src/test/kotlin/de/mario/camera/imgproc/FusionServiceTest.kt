package de.mario.camera.imgproc

import android.content.Intent
import com.nhaarman.mockito_kotlin.*
import de.mario.camera.exif.ExifTagWriteable
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.opencv.core.Mat
import org.springframework.test.util.ReflectionTestUtils
import java.io.File


@RunWith(JUnitPlatform::class)
object FusionServiceTest : Spek({

    describe("the exposure merge service") {

        val proxy: OpenCvProxy = mock()
        val exifWriter: ExifTagWriteable = mock()
        val mat: Mat = mock()
        val intent: Intent = mock()

        val classUnderTest = FusionService()
        ReflectionTestUtils.setField(classUnderTest, "exifWriter", exifWriter)
        ReflectionTestUtils.setField(classUnderTest, "proxy", proxy)

        beforeEachTest {
            reset(proxy, mat)
        }

        it("should merge images") {
            given(proxy.read(any<File>())).willReturn(mat)
            given(proxy.merge(any())).willReturn(mat)
            given(intent.getStringArrayExtra(FusionService.PICTURES)).willReturn(arrayOf("FOO.png", "BAR.png"))

            classUnderTest.process(intent)

            val order = Mockito.inOrder(proxy)
            order.verify(proxy, atLeastOnce()).read(any<File>())
            order.verify(proxy).merge(any())
        }

    }
})