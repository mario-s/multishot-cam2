package de.mario.camera.io

import android.media.Image
import android.media.ImageReader
import android.os.Environment
import android.os.Message
import android.util.Log
import de.mario.camera.R
import de.mario.camera.glue.CameraControlable
import de.mario.camera.glue.MessageSendable
import de.mario.camera.glue.MessageType
import de.mario.camera.glue.MessageSender
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 */
class ImageSaver(private val control: CameraControlable, private val reader: ImageReader) : Runnable {
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
        val PATTERN = "yyyy-MM-dd_HH:mm:ss.SSS"
    }

    override fun run() {
        if (!isExternalStorageWritable()) {
            sender.send(getString(R.string.no_storage))
        } else {
            val img: Image? = reader.acquireNextImage()
            if (img != null) {
                Log.d(TAG, "image timestamp: " + img.timestamp)
                val file = newFile()
                if(save(img, file)){
                    sendImageSavedMessage(file)
                }
            }
        }
    }

    private fun save(image: Image, file: File): Boolean {
        var success = false
        val plane = image.planes[0]
        val buffer = plane.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        var output: FileOutputStream? = null
        try {
            output = FileOutputStream(file)
            output.write(bytes)
            output.flush()
            success = true
        } catch (e: IOException) {
            Log.w(TAG, e.message, e)
        } finally {
            try {
                output?.close()
            } catch (e: IOException) {
                Log.w(TAG, e.message, e)
            }
        }
        return success
    }

    private fun isExternalStorageWritable(): Boolean {
        val state = storageAccess.getStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    private fun newFile(): File {
        return File(folder, createFileName(Date()))
    }

    private fun createFileName(date: Date): String {
        val dateFormat = SimpleDateFormat(PATTERN)
        return String.format("DSC_%s.jpg", dateFormat.format(date))
    }

    private fun getString(key: Int): String {
        return control.getString(key)
    }

    private fun sendImageSavedMessage(file: File) {
        val msg = Message.obtain(control.getMessageHandler())
        msg.what = MessageType.IMAGE_SAVED
        msg.data.putString("File", file.absolutePath)
        sender.send(msg)
    }
}