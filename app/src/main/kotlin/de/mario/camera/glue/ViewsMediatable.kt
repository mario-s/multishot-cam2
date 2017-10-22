package de.mario.camera.glue

import android.view.View
import de.mario.camera.R

interface ViewsMediatable {

    companion object {
        //the ids of the buttons
        val BUTTONS = arrayOf(R.id.picture, R.id.settings, R.id.info)
    }

    fun toggleViews(view: View)

    fun toggleOrientationListener(enable: Boolean)
}