package de.mario.camera.exif

import android.media.ExifInterface
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.test.util.ReflectionTestUtils
import java.io.File

/**
 *
 */
@RunWith(JUnitPlatform::class)
object ExifWriterTest : Spek( {

    describe("the exif writer") {

        val tmp = TemporaryFolder()
        val exifFactory: ExifInterfaceFactory = mock()
        val exifInterface: ExifInterface = mock()
        val classUnderTest = ExifWriter()
        ReflectionTestUtils.setField(classUnderTest, "exifFactory", exifFactory)

        beforeEachTest {
            reset(exifInterface)
            tmp.create()
        }

        afterEachTest {
            tmp.delete()
        }

        it("should add tags") {
            val file = File(tmp.newFolder(), "test.png")
            given(exifFactory.newInterface(file)).willReturn(exifInterface)
            val tags = mapOf("foo" to "bar")
            classUnderTest.addTags(file, tags)
            verify(exifInterface).saveAttributes()
        }

    }
})