package de.mario.camera.view

import android.app.Activity
import android.view.View
import de.mario.camera.R
import de.mario.camera.glue.SettingsAccessable
import de.mario.camera.glue.ViewsMediatable
import de.mario.camera.glue.ViewsOrientationListenable

class ViewsMediator(val activity: Activity, val settings: SettingsAccessable,
                    val viewsOrientationListener: ViewsOrientationListenable) : ViewsMediatable {

    companion object {
        //the ids of the buttons
        private val BUTTONS = arrayOf(R.id.picture, R.id.settings, R.id.info)
    }

    private fun toggleViews() {
        fun findView(id: Int): AbstractPaintView = activity.findViewById(id) as AbstractPaintView

        findView(R.id.grid).enable(settings.isEnabled(R.string.grid))
        findView(R.id.level).enable(settings.isEnabled(R.string.level))
    }

    private fun toggleOrientationListener(enable: Boolean) {
        if (enable) {
            BUTTONS.forEach { viewsOrientationListener.addView(activity.findViewById(it)) }
            viewsOrientationListener.enable()
        } else {
            viewsOrientationListener.disable()
            BUTTONS.forEach { viewsOrientationListener.removeView(activity.findViewById(it)) }
        }
    }

    override fun onResume() {
        toggleViews()
        toggleOrientationListener(true)
    }

    override fun onPause() {
        toggleOrientationListener(false)
    }

    override fun setOnClickListener(listener: View.OnClickListener) {
        BUTTONS.forEach { activity.findViewById(it).setOnClickListener(listener) }
    }
}