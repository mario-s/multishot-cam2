package de.mario.camera.view


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*

/**
 */
@RunWith(JUnitPlatform::class)
object GridViewTest : Spek({

    describe("the grid view") {

        val context = mock(Context::class.java)
        val classUnderTest = GridView(context, null)

        it("should draw 4 lines on the canvas") {
            val canvas = mock(Canvas::class.java)
            given(canvas.width).willReturn(200)
            given(canvas.height).willReturn(300)
            classUnderTest.enable(true)

            classUnderTest.drawGrid(canvas)
            verify(canvas, times(4)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), any(Paint::class.java))
        }


    }
})