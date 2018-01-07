package de.mario.camera.exif

import java.io.File

interface ExifTagWriteable {

    fun addTags(source: File, tags: Map<String, String>)

    fun copy(source: File, target: File)
}