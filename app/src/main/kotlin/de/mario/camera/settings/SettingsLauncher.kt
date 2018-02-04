package de.mario.camera.settings

import android.app.Fragment
import android.content.Intent
import de.mario.camera.glue.CameraDeviceProxyable

class SettingsLauncher(private val fragment: Fragment, private val cameraProxy: CameraDeviceProxyable) {

    internal companion object {
        const val RESOLUTIONS = "resolutions"
        const val SELECTED_RESOLUTION = "selectedPictureSize"
    }

    fun startSettings() = context().startActivity(intent())

    private fun intent(): Intent {
        val intent = Intent(context(), SettingsActivity::class.java)
        val resolutions: List<String> = cameraProxy.imageSizes().map { it.toString() }
        intent.putExtra(RESOLUTIONS, resolutions.toTypedArray())
        intent.putExtra(SELECTED_RESOLUTION, "")
        return intent
    }

    private fun context() = fragment.context
}