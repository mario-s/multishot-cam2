package de.mario.camera.opencv

import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager

/**
 * This class provides functionality to check if opencv ist installed.
 */
class ManagerLookup(private val context: Context) {

    constructor(fragment: Fragment): this(fragment.context)

    private companion object {
        const val OPENCV = "org.opencv.engine"
    }

    /**
     * Returns true if the app is installed, otherwise false.
     */
    fun exists() = exists(OPENCV)

    private fun exists(uri: String): Boolean {
        val packageInfoList = context.packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)
        return packageInfoList.asSequence().filter { it?.packageName == uri }.any()
    }
}