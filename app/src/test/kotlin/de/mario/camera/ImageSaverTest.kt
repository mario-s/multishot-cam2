package de.mario.camera


import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import android.media.Image
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.rules.TemporaryFolder
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import java.nio.ByteBuffer

/**
 *
 */
@RunWith(JUnitPlatform::class)
object ImageSaverTest : Spek( {

    describe("the image saver") {

        val tmp = TemporaryFolder()

        beforeEachTest {
            tmp.create()
        }

        afterEachTest {
            tmp.delete()
        }

        it("should store an image") {

            val file = tmp.newFile("test.jpg")
            val image = mock(Image::class.java)
            val plane = mock(Image.Plane::class.java)
            val buffer = mock(ByteBuffer::class.java)

            given(image.planes).willReturn(arrayOf(plane))
            given(plane.buffer).willReturn(buffer)

            val classUnderTest = ImageSaver(image, file)
            classUnderTest.run()

            assertThat(file.exists(), equalTo(true))
        }

    }
})