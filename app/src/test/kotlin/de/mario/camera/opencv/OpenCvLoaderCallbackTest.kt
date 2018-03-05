package de.mario.camera.opencv


import android.app.Activity
import android.content.Intent
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.opencv.android.LoaderCallbackInterface


@RunWith(JUnitPlatform::class)
object OpenCvLoaderCallbackTest : Spek({

    describe("the call back") {

        val context: Activity = mock()
        val intent: Intent = mock()
        val classUnderTest = LoaderCallback(context, intent)

        beforeEachTest {
            reset(context)
        }

        it("should start the service when status is success") {
            classUnderTest.onManagerConnected(LoaderCallbackInterface.SUCCESS)
            verify(context).startService(intent)
        }

        it("should not start the service when status is canceled") {
            classUnderTest.onManagerConnected(LoaderCallbackInterface.INSTALL_CANCELED)
            verify(context, never()).startService(intent)
        }
    }
})