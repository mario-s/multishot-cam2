package de.mario.camera.glue

import android.view.View

/**
 */
interface ViewsOrientationListenable {

    fun enable()

    fun disable()

    fun addView(view: View)

    fun removeView(view: View)
}