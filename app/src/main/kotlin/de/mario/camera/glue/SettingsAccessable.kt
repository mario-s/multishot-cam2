package de.mario.camera.glue

interface SettingsAccessable {

    fun isEnabled(key: Int): Boolean

    fun isEnabled(key: String): Boolean
}