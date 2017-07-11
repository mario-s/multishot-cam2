package de.mario.camera.settings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager



import java.lang.Integer.parseInt

/**
 * This class provides easy access to settings values.
 */
class SettingsAccess(private val context: Context){

    fun getString(key: String): String {
        return prefs().getString(key, "")
    }

    fun getInt(key: Int): Int {
        return getInt(context.getString(key))
    }

    fun getInt(key: String): Int {
        return parseInt(prefs().getString(key, "0"))
    }

    fun isEnabled(key: Int): Boolean {
        return isEnabled(context.getString(key))
    }

    fun isEnabled(key: String): Boolean {
        return prefs().getBoolean(key, false)
    }

    private fun prefs(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}