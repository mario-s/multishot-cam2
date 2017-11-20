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
class ImageSaver(private val control: CameraControllable, private val reader: ImageReader) : Runnable {
    private val sender: MessageSendable = MessageSender(control.getMessageHandler())
    private val storageAccess: StorageAccessable = StorageAccess
    private val folder:File by lazy {
        val f = File(storageAccess.getStorageDirectory(), "100_MULTI")
        if(!f.exists()){
            f.mkdir()
        }
        f
    }

    private companion object {
        val TAG = "ImageSaver"
        val PATTERN = "yyyy-MM-dd_HH:mm:ss"
    }

    override fun run() {
        if(!isExternalStorageWritable()){
            sendMessage(control.getString(R.string.no_storage))
        }else {
            val max = reader.maxImages
            for (i in 0 until max) {
                val img: Image? = reader.acquireLatestImage()
                if (img != null) {
                    save(img, i)
                }
            }
        }
    }

    private fun save(image: Image, index: Int) {
        val plane = image.planes[0]
        val buffer = plane.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        var output: FileOutputStream? = null
        try {
            val file = getFile(index)
            output = FileOutputStream(file)
            output.write(bytes)
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

    private fun getFile(index: Int): File {
        return File(folder, createFileName(Date(), index))
    }

    private fun createFileName(date: Date, index: Int): String {
        val dateFormat = SimpleDateFormat(PATTERN)
        return String.format("DSC_%s_%s.jpg", dateFormat.format(date), index)
    }

    private fun sendMessage(msg: String) {
        sender.send(msg)
    }
}