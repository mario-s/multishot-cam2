package de.mario.camera.opencv

import android.content.Context
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.opencv.android.InstallCallbackInterface

@RunWith(JUnitPlatform::class)
object DialogLoaderCallbackTest : Spek({

    describe("the call back") {

        val context: Context = mock()
        val classUnderTest = DialogLoaderCallback(context)

        beforeEachTest {
            reset(context)
        }

        it("should directly install the package") {
            val callback: InstallCallbackInterface = mock()
            classUnderTest.onPackageInstall(0, callback)
            verify(callback).install()
        }

    }
})