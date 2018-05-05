package de.mario.camera.view

import android.app.Activity
import android.view.View
import de.mario.camera.R
import de.mario.camera.device.PackageLookup
import de.mario.camera.glue.SettingsAccessable
import de.mario.camera.glue.ViewsMediatable
import de.mario.camera.glue.ViewsOrientationListenable

class ViewsMediator(private val activity: Activity, val settings: SettingsAccessable,
                    private val viewsOrientationListener: ViewsOrientationListenable) : ViewsMediatable {

    private val packageLookup = PackageLookup(activity)

    private companion object {
        //the ids of the buttons
        private val BUTTONS = arrayOf(R.id.picture, R.id.settings, R.id.info)
    }

    private fun toggleViews() {
        fun findView(id: Int): AbstractPaintView = activity.findViewById<AbstractPaintView>(id)

        findView(R.id.grid).enable(settings.isEnabled(R.string.grid))
        findView(R.id.level).enable(settings.isEnabled(R.string.level))
    }

    private fun toggleOrientationListener(enable: Boolean) {
        if (enable) {
            BUTTONS.forEach { viewsOrientationListener.addView(findView(it)) }
            viewsOrientationListener.enable()
        } else {
            viewsOrientationListener.disable()
            BUTTONS.forEach { viewsOrientationListener.removeView(findView(it)) }
        }
    }

    override fun onResume() {
        toggleViews()
        toggleOrientationListener(true)

        visible(R.id.info, packageLookup.exists())
    }

    override fun onPause() {
        toggleOrientationListener(false)
    }

    override fun setOnClickListener(listener: View.OnClickListener) {
        BUTTONS.forEach { findView(it).setOnClickListener(listener) }
    }

    override fun showProgress(show: Boolean) = visible(R.id.progressBar, show)

    private fun visible(id: Int, show: Boolean) {
        findView(id).visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun findView(id: Int) = activity.findViewById<View>(id)
}