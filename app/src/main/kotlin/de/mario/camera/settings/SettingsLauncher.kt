package de.mario.camera.settings

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import de.mario.camera.glue.CameraDeviceProxyable

class SettingsLauncher(private val fragment: Fragment, private val cameraProxy: CameraDeviceProxyable):
        Handler(Looper.getMainLooper()){

    companion object {
        const val RESOLUTIONS = "resolutions"
    }

    override fun handleMessage(msg: Message) {
        val data = msg.data
        context().startActivity(intent(data))
    }

    private fun intent(data: Bundle): Intent {

        val resolutions: List<String> = cameraProxy.imageSizes().map { it.toString() }

        val intent = Intent(context(), SettingsActivity::class.java)
        intent.putExtra(RESOLUTIONS, resolutions.toTypedArray())
        intent.putExtras(data)

        return intent
    }

    private fun context() = fragment.context
}