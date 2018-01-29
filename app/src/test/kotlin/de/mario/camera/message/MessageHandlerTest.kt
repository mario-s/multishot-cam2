package de.mario.camera.message

import android.os.Bundle
import android.os.Message
import de.mario.camera.CameraFragment
import de.mario.camera.glue.MessageSendable
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import org.hamcrest.CoreMatchers.`is`
import org.springframework.test.util.ReflectionTestUtils


@RunWith(JUnitPlatform::class)
object MessageHandlerTest : Spek({

    describe("the message handler") {

        val fragment = CameraFragment.newInstance()
        val message = spy(Message::class.java)
        val classUnderTest = MessageHandler(fragment)

        it("should handle the message for the processed file") {
            val name = "foo"
            val data = mock(Bundle::class.java)
            message.what = MessageSendable.MessageType.IMAGE_SAVED
            given(message.data).willReturn(data)
            given(data.getString(MessageSendable.MessageType.FILE)).willReturn(name)

            classUnderTest.handleMessage(message)
            val stack = ReflectionTestUtils.getField(fragment, "fileNameStack") as List<String>
            assertThat(stack.isEmpty(), `is`(false))
        }
    }
})