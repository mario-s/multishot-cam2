package de.mario.camera.orientation

import android.app.Activity

/**
 * This class register when the device orientation is changed. Since the screen orientation is fixed
 * the sensor needs to be queried for any change.
 */
class DeviceOrientationListener(private val activity: Activity) : AbstractOrientationListener(activity) {

    private var orientation: Int = OrientationMapper.get(0)

    override fun orientationChanged(orientation: Int) {
        this.orientation = OrientationMapper.get(orientation)
    }

    /**
     * Returns the last orientation.
     */
    fun getOrientation(): Int = orientation

    fun displayRotation(): Int = activity.windowManager.defaultDisplay.rotation

}