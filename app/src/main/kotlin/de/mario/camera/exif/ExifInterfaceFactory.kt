package de.mario.camera.exif

import android.media.ExifInterface
import java.io.File


internal class ExifInterfaceFactory {

    fun newInterface(file: File): ExifInterface = ExifInterface(file.getAbsolutePath())
}