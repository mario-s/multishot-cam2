package de.mario.camera.settings

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.os.Message
import android.util.Size
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import de.mario.camera.glue.CameraDeviceProxyable
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given


@RunWith(JUnitPlatform::class)
object SettingsLauncherTest : Spek({

    describe("the settings launcher") {

        val context: Context = mock()
        val fragment: Fragment = mock()
        val cameraProxy: CameraDeviceProxyable = mock()
        val classUnderTest = SettingsLauncher(fragment, cameraProxy)

        beforeEachTest {
            reset(fragment, cameraProxy)
            given(fragment.context).willReturn(context)
        }

        it("should start activity") {
            val msg: Message = mock()
            val data: Bundle = mock()
            val size: Size = mock()
            given(msg.data).willReturn(data)
            given(cameraProxy.imageSizes()).willReturn(arrayOf(size))

            classUnderTest.handleMessage(msg)

            verify(context).startActivity(any())
        }

    }
})