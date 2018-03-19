package de.mario.camera.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.preference.PreferenceManager
import de.mario.camera.glue.SettingsAccessable


import java.lang.Integer.parseInt

/**
 * This class provides easy access to settings values.
 */
class SettingsAccess(private val context: Context) : SettingsAccessable{

    override fun getString(key: String): String {
        return prefs().getString(key, "")
    }

    override fun getInt(key: Int): Int {
        return getInt(context.getString(key))
    }

    override fun getInt(key: String): Int {
        return parseInt(prefs().getString(key, "0"))
    }

    override fun isEnabled(key: Int): Boolean {
        return isEnabled(context.getString(key))
    }

    override fun isEnabled(key: String): Boolean {
        return prefs().getBoolean(key, false)
    }

    internal fun addListener(listener: OnSharedPreferenceChangeListener) = prefs().registerOnSharedPreferenceChangeListener(listener)

    internal fun removeListener(listener: OnSharedPreferenceChangeListener) = prefs().unregisterOnSharedPreferenceChangeListener(listener)

    private fun prefs(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}