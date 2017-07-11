package de.mario.camera.view

import android.content.Context
import android.graphics.Canvas



/**
 */
class GridView(context: Context) : AbstractPaintView(context) {

    private var showGrid: Boolean = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
    }

    fun drawGrid(canvas: Canvas) {
        if (showGrid) {
            val width = canvas.width
            val height = canvas.height

            canvas.drawLine(width / 3.0f, 0.0f, width / 3.0f, height - 1.0f, stroke)
            canvas.drawLine(2.0f * width / 3.0f, 0.0f, 2.0f * width / 3.0f, height - 1.0f, stroke)
            canvas.drawLine(0.0f, height / 3.0f, width - 1.0f, height / 3.0f, stroke)
            canvas.drawLine(0.0f, 2.0f * height / 3.0f, width - 1.0f, 2.0f * height / 3.0f, stroke)
        }
    }

    override fun enable(showGrid: Boolean) {
        this.showGrid = showGrid
    }
}