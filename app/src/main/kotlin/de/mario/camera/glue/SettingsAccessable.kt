package de.mario.camera.glue

interface SettingsAccessable {

    companion object {
        const val PICTURE_SIZE = "pictureSize"
    }

    fun isEnabled(key: Int): Boolean

    fun isEnabled(key: String): Boolean
}