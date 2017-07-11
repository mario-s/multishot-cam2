package de.mario.camera.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.View

/**
 * This class returns a [Paint].
 */
abstract class AbstractPaintView(context: Context) : View(context) {

    private val WIDTH = 1F

    protected val stroke: Paint by lazy {
        val paint = Paint()
        paint.color = Color.WHITE
        paint.isAntiAlias = true
        paint.strokeWidth = WIDTH
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint
    }


    /**
     * Enables or disables this view.
     * @param enabled
     */
    abstract fun enable(enabled: Boolean)
}