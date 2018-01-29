package de.mario.camera.exif

import android.media.ExifInterface
import android.util.Log
import java.io.File
import java.io.IOException


/**
 */
class ExifWriter : ExifTagWriteable {

    private val exifFactory = ExifInterfaceFactory()

    private companion object {
        const val TAG = "ExifWriter"

        val EXIF_TAGS = arrayOf(ExifInterface.TAG_ORIENTATION, ExifInterface.TAG_DATETIME,
                ExifInterface.TAG_MAKE, ExifInterface.TAG_MODEL, ExifInterface.TAG_FLASH,
                ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.TAG_EXPOSURE_TIME,
                ExifInterface.TAG_APERTURE, ExifInterface.TAG_ISO,
                ExifInterface.TAG_WHITE_BALANCE, ExifInterface.TAG_FOCAL_LENGTH, ExifInterface.TAG_GPS_LATITUDE,
                ExifInterface.TAG_GPS_LONGITUDE, ExifInterface.TAG_GPS_LATITUDE_REF, ExifInterface.TAG_GPS_LONGITUDE_REF,
                ExifInterface.TAG_GPS_ALTITUDE, ExifInterface.TAG_GPS_ALTITUDE_REF, ExifInterface.TAG_GPS_TIMESTAMP,
                ExifInterface.TAG_GPS_DATESTAMP, ExifInterface.TAG_GPS_PROCESSING_METHOD)
    }

    override fun addTags(source: File, tags: Map<String, String>) {
        if (!tags.isEmpty()) {
            try {
                val sourceExif = getExifInterface(source)
                val targetExif = getExifInterface(source)
                copy(sourceExif, targetExif) //no to loose the existing metadata

                tags.entries.forEach{targetExif.setAttribute(it.key, it.value)}

                targetExif.saveAttributes()
            } catch (exc: IOException) {
                Log.w(TAG, exc)
            }

        }
    }

    override fun copy(source: File, target: File) {
        try {
            val sourceExif = getExifInterface(source)
            val targetExif = getExifInterface(target)
            copy(sourceExif, targetExif)

            targetExif.saveAttributes()
        } catch (exc: IOException) {
            Log.w(TAG, exc)
        }

    }

    private fun copy(sourceExif: ExifInterface, targetExif: ExifInterface) {
        for (tag in EXIF_TAGS) {
            val attr = sourceExif.getAttribute(tag)
            if (attr != null) {
                targetExif.setAttribute(tag, attr)
            }
        }
    }

    @Throws(IOException::class)
    private fun getExifInterface(file: File): ExifInterface {
        return exifFactory.newInterface(file)
    }
}