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
object LevelViewTest : Spek({

    describe("the level view") {

        val context = mock(Context::class.java)
        val classUnderTest = LevelView(context, null)

        it("should draw a line") {
            val canvas = mock(Canvas::class.java)
            given(canvas.width).willReturn(200)
            given(canvas.height).willReturn(300)
            classUnderTest.enable(true)

            classUnderTest.drawLevel(canvas)
            verify(canvas).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), any(Paint::class.java))
        }
    }
})