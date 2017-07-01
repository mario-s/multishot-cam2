package de.mario.camera


import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

import org.junit.platform.runner.JUnitPlatform
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import android.media.Image
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.io.File
import java.nio.ByteBuffer

/**
 *
 */
@RunWith(JUnitPlatform::class)
object ImageSaverTest : Spek( {

    describe("the image saver") {

        val temp = TemporaryFolder()
        var file: File? = null
        var image: Image? = null

        beforeEachTest {
            temp.create()
            file = temp.newFile("pic.jpg")
            image = mock(Image::class.java)
        }

        afterEachTest {
            temp.delete()
        }

        it("should store an image") {
            val plane = mock(Image.Plane::class.java)
            val buffer = mock(ByteBuffer::class.java)

            given(image!!.planes).willReturn(arrayOf(plane))
            given(plane.buffer).willReturn(buffer)

            val classUnderTest = ImageSaver(image!!, file!!)
            classUnderTest.run()
            verify(buffer).remaining()
        }

    }
})