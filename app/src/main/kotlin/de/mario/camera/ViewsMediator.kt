package de.mario.camera

import android.app.Activity
import android.view.View
import de.mario.camera.glue.SettingsAccessable
import de.mario.camera.glue.ViewsMediatable
import de.mario.camera.glue.ViewsOrientationListenable
import de.mario.camera.orientation.ViewsOrientationListener
import de.mario.camera.settings.SettingsAccess
import de.mario.camera.view.AbstractPaintView


class ViewsMediator(private val activity: Activity) : ViewsMediatable {
    private val settings: SettingsAccessable = SettingsAccess(activity)
    private val viewsOrientationListener: ViewsOrientationListenable = ViewsOrientationListener(activity)


    override fun toggleViews(view: View) {
        fun findView(id: Int): AbstractPaintView = view.findViewById(id) as AbstractPaintView

        findView(R.id.grid).enable(settings.isEnabled(R.string.grid))
        findView(R.id.level).enable(settings.isEnabled(R.string.level))
    }

    override fun toggleOrientationListener(enable: Boolean) {
        if (enable) {
            ViewsMediatable.BUTTONS.forEach { viewsOrientationListener.addView(activity.findViewById(it)) }
            viewsOrientationListener.enable()
        } else {
            viewsOrientationListener.disable()
            ViewsMediatable.BUTTONS.forEach { viewsOrientationListener.removeView(activity.findViewById(it)) }
        }
    }
}