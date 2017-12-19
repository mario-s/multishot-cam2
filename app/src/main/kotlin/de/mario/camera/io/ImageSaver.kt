package de.mario.camera.io

import android.media.Image
import android.media.ImageReader
import android.os.Environment
import android.util.Log
import de.mario.camera.R
import de.mario.camera.glue.CameraControllable
import de.mario.camera.glue.MessageSendable
import de.mario.camera.message.MessageSender
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 */
class ImageSaver(private val control: CameraControllable, private val reader: ImageReader, private val counter: Int) : Runnable {
    private val sender: MessageSendable = MessageSender(control.getMessageHandler())
    private val storageAccess: StorageAccessable = StorageAccess
    private val folder: File by lazy {
        val f = File(storageAccess.getStorageDirectory(), "100_MULTI")
        if (!f.exists()) {
            f.mkdir()
        }
        f
    }

    private companion object {
        val TAG = "ImageSaver"
        val PATTERN = "yyyy-MM-dd_HH:mm:ss"
    }

    override fun run() {
        if (!isExternalStorageWritable()) {
            sendMessage(getString(R.string.no_storage))
        } else {
            val max = reader.maxImages
            val img: Image? = reader.acquireNextImage()
            if (img != null) {
                Log.d(TAG, "image timestamp: " + img.timestamp)
                save(img, counter, max)
            }
        }
    }

    private fun save(image: Image, index: Int, max: Int) {
        val plane = image.planes[0]
        val buffer = plane.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        var output: FileOutputStream? = null
        try {
            val file = getFile(index)
            output = FileOutputStream(file)
            output.write(bytes)

            sendFileSavedInfo(max)
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

    private fun sendFileSavedInfo(max: Int) {
        if(counter == max) {
            sendMessage(getString(R.string.photos_saved).format(max, folder))
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        val state = storageAccess.getStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    private fun getFile(index: Int): File {
        return File(folder, createFileName(Date(), index))
    }

    private fun createFileName(date: Date, index: Int): String {
        val dateFormat = SimpleDateFormat(PATTERN)
        return String.format("DSC_%s_%s.jpg", dateFormat.format(date), index)
    }

    private fun getString(key: Int): String {
        return control.getString(key)
    }

    private fun sendMessage(msg: String) {
        sender.send(msg)
    }
}