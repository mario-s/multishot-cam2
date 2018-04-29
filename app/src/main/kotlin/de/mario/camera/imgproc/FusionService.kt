package de.mario.camera.imgproc

import android.app.IntentService
import android.content.Intent
import android.media.MediaScannerConnection
import de.mario.camera.R
import de.mario.camera.exif.ExifTagWriteable
import de.mario.camera.exif.ExifWriter
import de.mario.camera.message.BroadcastingSender
import org.opencv.core.Mat
import java.io.File



internal class FusionService() : IntentService(TAG) {

    private val exifWriter: ExifTagWriteable = ExifWriter()

    private val proxy = OpenCvProxy()

    companion object {
        const val TAG = "FusionService"
        const val PICTURES = "de.mario.camera.extra.PICTURES"
        const val SYSTEM_NOTIFY = "de.mario.camera.extra.NOTIFY"
        const val MERGED = "_fusion"
    }

    override fun onHandleIntent(intent: Intent?) {
        process(intent!!)
    }

    internal fun process(intent: Intent) {
        val picsNames = intent.getStringArrayExtra(PICTURES)

        val images = loadImages(picsNames)

        val fusion = proxy.merge(images)

        val firstPic = picsNames[0]
        val out = File(createFileName(firstPic))
        write(fusion, out)
        copyExif(firstPic, out)

        val path = out.absolutePath
        MediaScannerConnection.scanFile(applicationContext, arrayOf(path), null, null)

        broadcast(path)

        if(intent.getBooleanExtra(SYSTEM_NOTIFY, false)) {
            NotificationSender(this).send(path)
        }
    }

    private fun broadcast(path: String) {
        //message for the app
        val intent = Intent(getString(R.string.EXPOSURE_MERGE))
        intent.putExtra(getString(R.string.MERGED), path)
        BroadcastingSender.send(baseContext, intent)
    }

    private fun copyExif(src: String, target: File) {
        val srcFile = File(src)
        exifWriter.copy(srcFile, target)
    }

    private fun createFileName(src: String): String {
        val pos = src.lastIndexOf(".")
        val prefix = src.substring(0, pos - 1)
        val suffix = src.substring(pos)
        return prefix + MERGED + suffix
    }

    private fun write(fusion: Mat, out: File) {
        val result = proxy.multiply(fusion)
        proxy.write(result, out)
    }

    private fun loadImages(pics: Array<String>): List<Mat> {
        val imgs: MutableList<Mat> = mutableListOf()

        pics.forEach {
            val mat = proxy.read(File(it))
            imgs.add(mat)
        }

        return imgs
    }
}