package de.mario.camera.view


import android.content.Context
import android.util.Size
import org.hamcrest.CoreMatchers.equalTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

/**
 */
@RunWith(JUnitPlatform::class)
object AutoFitTextureViewTest : Spek({

    class TestView(context: Context) : AutoFitTextureView(context) {
        var w: Int = 0
        var h: Int = 0

        fun doMeasure(w: Int, h: Int) {
            onMeasure(w, h)
        }

        override fun setDimension(w: Int, h: Int) {
            this.w = w
            this.h = h
        }
    }

    describe("the auto fit texture view") {

        val context = mock(Context::class.java)
        val classUnderTest = TestView(context)

        it("should have a method to set the aspect ration") {
            val size = mock(Size::class.java)
            given(size.width).willReturn(2)
            given(size.height).willReturn(3)
            classUnderTest.setAspectRatio(size, 0)
        }

        it("should override onMeasure") {
            classUnderTest.setAspectRatio(2,3)
            classUnderTest.doMeasure(2,3)
            assertThat(classUnderTest.w, equalTo(0))
            assertThat(classUnderTest.h, equalTo(0))
        }

    }
})