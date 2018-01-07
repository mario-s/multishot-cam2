package de.mario.camera.process

import android.app.IntentService
import android.content.Intent
import de.mario.camera.exif.ExifTagWriteable
import de.mario.camera.exif.ExifWriter
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.core.Mat
import java.io.File
import org.opencv.core.Core
import org.opencv.core.Scalar



internal class ExposureMergeService() : IntentService(TAG) {

    private val merger: Merger = MertensMerger()

    private val exifWriter: ExifTagWriteable = ExifWriter()

    private val proxy = OpenCvProxy()

    companion object {
        const val TAG = "ExposureMergeService"
        const val PARAM_PICS = "de.mario.camera.extra.PICS"
        const val MERGED = "merged"
    }

    override fun onHandleIntent(intent: Intent?) {
        process(intent!!.getStringArrayExtra(PARAM_PICS))
    }

    internal fun process(pictures: Array<String>) {
        val images = loadImages(pictures)

        val fusion = merger.merge(images)

        val firstPic = pictures[0]
        val out = File(createFileName(firstPic))
        write(fusion, out)
        copyExif(firstPic, out)
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
        val result = multiply(fusion)
        proxy.write(result, out)
    }

    private fun multiply(fusion: Mat): Mat {
        val result = proxy.mat()
        val scalar = proxy.scalar(255.0, 255.0, 255.0)
        return proxy.multiply(fusion, scalar, result)
    }

    private fun loadImages(pics: Array<String>): List<Mat> {
        val imgs: MutableList<Mat> = mutableListOf()

        pics.forEach {
            imgs.add(proxy.read(File(it)))
        }

        return imgs
    }
}