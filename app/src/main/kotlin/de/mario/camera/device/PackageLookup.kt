package de.mario.camera.device

import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager

/**
 * This class provides functionality to check if a package is installed.
 */
class PackageLookup(private val context: Context) {

    constructor(fragment: Fragment): this(fragment.context)

    companion object {
        const val OPENCV = "org.opencv.engine"
    }

    /**
     * Returns true if the package is installed, otherwise false.
     */
    fun exists(name: String): Boolean {
        val packageInfoList = context.packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)
        return packageInfoList.asSequence().filter { it?.packageName == name }.any()
    }
}