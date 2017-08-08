package de.mario.camera

import android.media.Image
import android.os.Environment
import android.util.Log
import de.mario.camera.glue.CameraControlable
import de.mario.camera.message.MessageSender
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 */
class ImageSaver(val control: CameraControlable, val image: Image) : Runnable {
    private val TAG = "ImageSaver"
    private val sender = MessageSender(control.getMessageHandler())

    override fun run() {
        if(!isExternalStorageWritable()){
            sendMessage(control.getString(R.string.no_storage))
        }else {
            save()
        }
    }

    private fun save() {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        var output: FileOutputStream? = null
        try {
            val file = getFile()
            output = FileOutputStream(file)
            output?.write(bytes)
            sendMessage(control.getString(R.string.photos_saved).format(1, file))
        } catch (e: IOException) {
            Log.w(TAG, e.message, e)
        } finally {
            image.close()
            try {
                output?.close()
            } catch (e: IOException) {
                Log.w(TAG, e.message, e)
            }
        }
    }

    fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            return true
        }
        return false
    }

    private fun getFile(): File {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        return File(dir, "pic.jpg")
    }

    private fun sendMessage(msg: String) {
        sender.send(msg)
    }
}