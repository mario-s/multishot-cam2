package de.mario.camera.io

import java.io.File


internal interface StorageAccessable {
    fun getStorageState(): String

    fun getStorageDirectory() : File
}