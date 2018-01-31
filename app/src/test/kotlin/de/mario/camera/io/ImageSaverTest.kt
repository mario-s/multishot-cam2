package de.mario.camera.io


import android.content.Context
import android.media.Image
import android.media.ImageReader
import android.os.Environment
import android.os.Handler
import com.nhaarman.mockito_kotlin.mock
import de.mario.camera.glue.CameraControlable
import de.mario.camera.glue.MessageSendable
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.*
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.test.util.ReflectionTestUtils
import java.nio.ByteBuffer


/**
 *
 */
@RunWith(JUnitPlatform::class)
object ImageSaverTest : Spek( {

    describe("the image saver") {

        val tmp = TemporaryFolder()
        val control: CameraControlable = mock()
        val context: Context = mock()
        val reader: ImageReader = mock()
        val messageSendable: MessageSendable = mock()
        val storageAccessable: StorageAccessable = mock()
        var classUnderTest: ImageSaver? = null

        beforeEachTest {
            tmp.create()
            val handler = mock(Handler::class.java)
            given(control.getMessageHandler()).willReturn(handler)
            given(control.getContext()).willReturn(context)
            given(context.getString(anyInt())).willReturn("foo")
            given(storageAccessable.getStorageDirectory()).willReturn(tmp.newFile(("foo")))
            classUnderTest = ImageSaver(control, reader)
            ReflectionTestUtils.setField(classUnderTest, "sender", messageSendable)
            ReflectionTestUtils.setField(classUnderTest, "storageAccess", storageAccessable)
        }

        afterEachTest {
            tmp.delete()
        }

        it("run method should call message sender when done") {
            classUnderTest?.run()
            verify(messageSendable).send(anyString())
        }

        it("run method should read next image from reader") {
            given(storageAccessable.getStorageState()).willReturn(Environment.MEDIA_MOUNTED)
            val image = mock(Image::class.java)
            val plane = mock(Image.Plane::class.java)
            val buffer = ByteBuffer.allocate(10)
            given(reader.acquireNextImage()).willReturn(image)
            given(image.planes).willReturn(arrayOf(plane))
            given(plane.buffer).willReturn(buffer)

            classUnderTest?.run()
            val order = inOrder(reader, image)
            order.verify(reader).acquireNextImage()
            order.verify(image).close()
        }

    }
})