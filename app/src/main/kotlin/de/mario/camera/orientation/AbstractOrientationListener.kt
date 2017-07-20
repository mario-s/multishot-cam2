package de.mario.camera.orientation

import android.content.Context
import android.view.OrientationEventListener
import android.view.Surface

/**
 * This class fires an event only if the device is in one of the angles:  0, 90, 180 or 270.
 */
abstract class AbstractOrientationListener(context: Context) : OrientationEventListener(context) {

    private val noiseFilter = OrientationNoiseFilter()

    protected var lastOrientation = OrientationEventListener.ORIENTATION_UNKNOWN

    override fun onOrientationChanged(angle: Int) {
        if (angle != OrientationEventListener.ORIENTATION_UNKNOWN) {
            val orientation = orientationInDeg(noiseFilter.filter(angle))
            if (orientation != lastOrientation) {
                orientationChanged(orientation)
                lastOrientation = orientation
            }
        }
    }

    protected abstract fun orientationChanged(orientation: Int)

    /**
     * Returns orientation of the device for the given angle as an int value.
     * Possible values: 0, 90, 180 or 270.
     * @param angle
     * *
     * @return
     */
    fun orientationInDeg(angle: Int): Int {
        when(orientationAbs(angle)){
            1 -> return 90
            2 -> return 180
            3 -> return 270
        }
        return 0
    }


    /**
     * Returns orientation of the device for the given angle as an int value from 0 to 3.
     * Surface.ROTATION_0 for an angle greater 315 or smaller 45,
     * Surface.ROTATION_90 for an angle between 45 and 135,
     * Surface.ROTATION_180 for an angle between 135 and 225,
     * Surface.ROTATION_270 for an angle between 225 and 315,
     * otherwise 0.

     * [Surface]

     * @param angle the current angle
     * *
     * @return a orientation
     */
    protected fun orientationAbs(angle: Int): Int {
        var orientation = 0
        if (angle >= 315 || angle < 45) {
            orientation = Surface.ROTATION_0
        } else if (angle in 45 .. 134) {
            orientation = Surface.ROTATION_90
        } else if (angle in 135 .. 224) {
            orientation = Surface.ROTATION_180
        } else if (angle in 225 .. 314) {
            orientation = Surface.ROTATION_270
        }
        return orientation
    }
}