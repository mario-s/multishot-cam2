package de.mario.camera.glue

interface SettingsAccessable {

    fun getString(key: String): String

    fun isEnabled(key: Int): Boolean

    fun isEnabled(key: String): Boolean
}