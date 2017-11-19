package de.mario.camera.orientation

import android.content.Context
import android.view.View
import de.mario.camera.glue.ViewsOrientationListenable
import java.util.*

/**
 * Orientation listener to update the rotation of views.
 */
class ViewsOrientationListener(context: Context) : AbstractOrientationListener(context), ViewsOrientationListenable {

    private val views = ArrayList<View>()

    override fun orientationChanged(orientation: Int) {
        val angle = 360 - orientation
        for (view in views) {
            view.rotation = angle.toFloat()
        }
    }

    override fun addView(view: View) {
        views.add(view)
    }

    override fun removeView(view: View) {
        views.remove(view)
    }
}