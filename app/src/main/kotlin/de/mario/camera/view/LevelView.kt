package de.mario.camera.view


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.OrientationEventListener

import de.mario.camera.orientation.OrientationNoiseFilter


/**
 * This view draws an indicator when the device is near horizontal or vertical.
 */
class LevelView(context: Context, attrs: AttributeSet?) : AbstractPaintView(context, attrs) {

    private val listener = LevelOrientationListener(context)

    private var orientation: Int = 0

    private var showLevel: Boolean = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLevel(canvas)
    }

    internal fun drawLevel(canvas: Canvas) {
        if (showLevel) {
            if (isHorizontal(orientation) || isVertical(orientation)) {
                stroke.color = Color.GREEN
            } else {
                stroke.color = Color.WHITE
            }

            val cx = canvas.width / 2
            val cy = canvas.height / 2
            val len = cx * 2 / 3

            canvas.rotate((-orientation).toFloat(), cx.toFloat(), cy.toFloat())
            canvas.drawLine((cx - len).toFloat(), cy.toFloat(), (cx + len).toFloat(), cy.toFloat(), stroke)
        }
    }

    private fun isHorizontal(orientation: Int): Boolean {
        return Math.abs(90 - orientation) <= TOL || Math.abs(270 - orientation) <= TOL
    }

    private fun isVertical(orientation: Int): Boolean {
        return Math.abs(0 - orientation) <= TOL || Math.abs(180 - orientation) <= TOL
    }

    override fun enable(enabled: Boolean) {
        this.showLevel = enabled
        if (showLevel) {
            listener.enable()
        } else {
            listener.disable()
        }
    }

    private inner class LevelOrientationListener(context: Context) : OrientationEventListener(context) {

        private val noiseFilter = OrientationNoiseFilter()

        override fun onOrientationChanged(arg: Int) {
            if (arg != OrientationEventListener.ORIENTATION_UNKNOWN) {
                orientation = noiseFilter.filter(arg)
                invalidate()
            }
        }
    }

    companion object {
        private val TOL = 1
    }
}