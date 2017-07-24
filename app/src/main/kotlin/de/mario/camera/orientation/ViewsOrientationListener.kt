package de.mario.camera.orientation

import android.content.Context
import android.view.View

import java.util.ArrayList

/**
 * Orientation listener to update the rotation of views.
 */
class ViewsOrientationListener(context: Context) : AbstractOrientationListener(context) {

    private val views = ArrayList<View>()

    override fun orientationChanged(orientation: Int) {
        val angle = 360 - orientation
        for (view in views) {
            view.rotation = angle.toFloat()
        }
    }

    override fun enable() {
        if (canDetectOrientation()) {
            super.enable()
        }
    }

    fun addView(view: View) {
        views.add(view)
    }

    fun removeView(view: View) {
        views.remove(view)
    }
}