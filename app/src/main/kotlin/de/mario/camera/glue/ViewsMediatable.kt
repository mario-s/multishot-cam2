package de.mario.camera.glue

import android.view.View

interface ViewsMediatable {

    fun onResume()

    fun onPause()

    fun setOnClickListener(listener: View.OnClickListener)
}