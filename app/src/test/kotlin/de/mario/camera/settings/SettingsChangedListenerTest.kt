package de.mario.camera.settings

import android.content.Context
import android.preference.CheckBoxPreference
import com.nhaarman.mockito_kotlin.*
import de.mario.camera.device.PackageLookup
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.doNothing
import org.springframework.test.util.ReflectionTestUtils

@RunWith(JUnitPlatform::class)
object SettingsChangedListenerTest : Spek({
    val foo = "foo"

    describe("the settings changed listener") {

        val context: Context = mock()
        val settings: SettingsAccess = mock()
        val lookup: PackageLookup = mock()
        val hdrCheck: CheckBoxPreference = mock()
        val classUnderTest = SettingsChangedListener(context)
        classUnderTest.hdrCheck = hdrCheck
        ReflectionTestUtils.setField(classUnderTest, "settings", settings)
        ReflectionTestUtils.setField(classUnderTest, "lookup", lookup)

        beforeEachTest {
            reset(context, settings, lookup)
        }

        it("should check for HDR and if OpenCv is installed") {
            given(context.getString(anyInt())).willReturn(foo)
            given(settings.isEnabled(anyInt())).willReturn(true)

            val spy = spy(classUnderTest)
            doNothing().`when`(spy).showAlert(any())

            spy.onSharedPreferenceChanged(mock(), foo)

            verify(settings).isEnabled(anyInt())
            verify(lookup).exists(PackageLookup.OPENCV)
        }

    }
})