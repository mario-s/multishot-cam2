package de.mario.camera.process

import android.content.Context
import android.databinding.ObservableArrayList
import android.os.Handler
import android.os.HandlerThread
import com.nhaarman.mockito_kotlin.mock
import de.mario.camera.glue.CameraControlable
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.BDDMockito.*
import org.springframework.test.util.ReflectionTestUtils

/**
 *
 */
@RunWith(JUnitPlatform::class)
object FileNameListCallbackTest : Spek({

    describe("the list call back") {

        val control: CameraControlable = mock()
        val context: Context = mock()
        val handlerThread: HandlerThread = mock()
        val handler: Handler = mock()
        given(control.getContext()).willReturn(context)
        val classUnderTest = FileNameListCallback(control)
        ReflectionTestUtils.setField(classUnderTest, "handler", handler)
        ReflectionTestUtils.setField(classUnderTest, "handlerThread", handlerThread)


        it("should stop the handlerThread") {
            classUnderTest.stop()
            verify(handlerThread).quitSafely()
        }

        it("should post a background thread onItemRangeInserted") {
            val source = ObservableArrayList<String>()
            listOf("1","2","3").forEach{source.add(it)}
            classUnderTest.onItemRangeInserted(source, 0, source.size)
            verify(handler).post(any())
        }

    }
})