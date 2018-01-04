package de.mario.camera.view

import android.app.Activity
import android.view.View
import de.mario.camera.glue.SettingsAccessable
import de.mario.camera.glue.ViewsOrientationListenable
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(JUnitPlatform::class)
object ViewsMediatorTest : Spek({

    describe("the views mediator") {

        val view = mock(AbstractPaintView::class.java)
        val activity = mock(Activity::class.java)
        val settings = mock(SettingsAccessable::class.java)
        val listener = mock(ViewsOrientationListenable::class.java)
        val classUnderTest = ViewsMediator(activity, settings, listener)

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