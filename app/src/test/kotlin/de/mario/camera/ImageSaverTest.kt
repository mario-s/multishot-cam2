package de.mario.camera


import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import android.media.Image
import android.os.Handler
import de.mario.camera.glue.CameraControlable
import de.mario.camera.glue.MessageSendable
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.anyInt
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


/**
 *
 */
@RunWith(JUnitPlatform::class)
object ImageSaverTest : Spek( {

    describe("the image saver") {

        val control = mock(CameraControlable::class.java)
        val image = mock(Image::class.java)
        val messageSendable = mock(MessageSendable::class.java)

        beforeEachTest {
            val handler = mock(Handler::class.java)
            given(control.getMessageHandler()).willReturn(handler)
            given(control.getString(anyInt())).willReturn("foo")
        }

        it("run method should call message sender when done") {
            val classUnderTest = ImageSaver(control, image, messageSendable)
            classUnderTest.run()
            verify(messageSendable).send(anyString())
        }

    }
})