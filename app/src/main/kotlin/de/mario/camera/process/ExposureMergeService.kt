package de.mario.camera.process

import android.app.IntentService
import android.content.Intent


internal class ExposureMergeService(name: String?) : IntentService(name) {

    companion object {
        const val PARAM_PICS = "de.mario.camera.extra.PICS"
    }

    override fun onHandleIntent(intent: Intent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}