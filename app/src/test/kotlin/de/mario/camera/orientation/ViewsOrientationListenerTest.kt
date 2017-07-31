package de.mario.camera.orientation


import android.content.Context
import android.view.View
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito.*


/**
 */
@RunWith(JUnitPlatform::class)
object ViewsOrientationListenerTest : Spek({

    describe("the views orientation listener") {

        var classUnderTest:ViewsOrientationListener? = null

        beforeEachTest {
            val context = mock(Context::class.java)
            classUnderTest = ViewsOrientationListener(context)
        }

        it("should change the orientation of a view") {
            val view = mock(View::class.java)
            classUnderTest?.addView(view)
            classUnderTest?.onOrientationChanged(90)
            verify(view).rotation = 270f
        }

        it("should not cause an exception when no view is present and the orientation changes") {
            val view = mock(View::class.java)
            classUnderTest?.addView(view)
            classUnderTest?.removeView(view)
            classUnderTest?.onOrientationChanged(90)
            verify(view, never()).rotation = 270f
        }

    }
})