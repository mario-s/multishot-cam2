package de.mario.camera.settings

import android.app.Fragment
import android.content.Intent
import de.mario.camera.glue.CameraDeviceProxyable

class SettingsLauncher(private val fragment: Fragment, private val cameraProxy: CameraDeviceProxyable) {

    internal companion object {
        const val RESOLUTIONS = "resolutions"
    }

    fun startSettings() = context().startActivity(intent())

    private fun intent(): Intent {
        val intent = Intent(context(), SettingsActivity::class.java)
        //intent.putExtra(RESOLUTIONS, cameraProxy.imageSizes())
        return intent
    }

    private fun context() = fragment.context
}