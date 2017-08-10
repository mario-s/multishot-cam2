package de.mario.camera.io

import android.media.Image
import android.os.Environment
import android.util.Log
import de.mario.camera.R
import de.mario.camera.glue.CameraControlable
import de.mario.camera.glue.MessageSendable
import de.mario.camera.message.MessageSender
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 */
class ImageSaver(private val control: CameraControlable, private val image: Image) : Runnable {
    private val TAG = "ImageSaver"
    private val sender: MessageSendable = MessageSender(control.getMessageHandler())
    private val storageAccess: StorageAccessable = StorageAccess

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

    private fun isExternalStorageWritable(): Boolean {
        val state = storageAccess.getStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    private fun getFile(): File {
        val dir = storageAccess.getStorageDirectory()
        return File(dir, "pic.jpg")
    }

    private fun sendMessage(msg: String) {
        sender.send(msg)
    }
}