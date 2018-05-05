package de.mario.camera.view

import android.app.Activity
import android.view.View
import com.nhaarman.mockito_kotlin.mock
import de.mario.camera.device.PackageLookup
import de.mario.camera.glue.SettingsAccessable
import de.mario.camera.glue.ViewsOrientationListenable
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.test.util.ReflectionTestUtils

@RunWith(JUnitPlatform::class)
object ViewsMediatorTest : Spek({

    describe("the views mediator") {

        val view: AbstractPaintView = mock()
        val activity: Activity = mock()
        val settings: SettingsAccessable = mock()
        val listener: ViewsOrientationListenable = mock()
        val packageLookup: PackageLookup = mock()
        val classUnderTest = ViewsMediator(activity, settings, listener)

        ReflectionTestUtils.setField(classUnderTest, "packageLookup", packageLookup)

        beforeEachTest {
            given(activity.findViewById<View>(anyInt())).willReturn(view)
        }

        it("should enable listener onResume") {
            classUnderTest.onResume()
            verify(listener).enable()
        }

        it("should disable listener onPause") {
            classUnderTest.onPause()
            verify(listener).disable()
        }
    }

})