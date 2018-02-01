package de.mario.camera.process

import android.databinding.ObservableArrayList
import android.databinding.ObservableList.OnListChangedCallback;
import android.media.MediaScannerConnection
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import de.mario.camera.R
import de.mario.camera.glue.CameraControlable
import de.mario.camera.settings.SettingsAccess
import java.io.File

/**
 * Listener for the collection of saved files, which may trigger processing.
 */
class FileNameListCallback(private val control: CameraControlable): OnListChangedCallback<ObservableArrayList<String>>()  {
    private val context = control.getContext()
    private val handlerThread: HandlerThread
    private val handler: Handler
    private val trigger = FusionProcessTrigger(context)
    private val settings = SettingsAccess(context)

    companion object {
        const val TAG = "FileNameListCallback"
        //TODO: use settings for number of images
        const val MAX_IMG = 3
    }

    init {
        handlerThread = HandlerThread(TAG)
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    fun stop() {
        handlerThread.quitSafely()
        try {
            handlerThread.join()
        } catch (e: InterruptedException) {
            Log.w(TAG, e.localizedMessage, e)
        }
    }

    private fun process(source : Array<String>) {
        handler.post({
            val folder = File(source[0]).parent
            control.showToast(context.getString(R.string.photos_saved).format(source.size, folder))
            MediaScannerConnection.scanFile(context, source, null, null)

            if(settings.isEnabled(R.string.hdr)) {
                trigger.process(source)
            }
        })
    }

    override fun onItemRangeInserted(source: ObservableArrayList<String>?, start: Int, count: Int) {
        if(source != null && source.size >= MAX_IMG) {
            process(source.toTypedArray())
        }
    }

    override fun onItemRangeMoved(source: ObservableArrayList<String>?, start: Int, end: Int, count: Int) {
    }

    override fun onItemRangeChanged(source: ObservableArrayList<String>?, start: Int, count: Int) {
    }

    override fun onItemRangeRemoved(source: ObservableArrayList<String>?, start: Int, count: Int) {
    }

    override fun onChanged(source: ObservableArrayList<String>?) {
    }
}