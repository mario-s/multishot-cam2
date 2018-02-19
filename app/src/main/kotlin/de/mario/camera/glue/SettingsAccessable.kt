package de.mario.camera.glue

interface SettingsAccessable {

    fun getString(key: String): String

    fun getInt(key: Int): Int

    fun getInt(key: String): Int

    fun isEnabled(key: Int): Boolean

    fun isEnabled(key: String): Boolean
}