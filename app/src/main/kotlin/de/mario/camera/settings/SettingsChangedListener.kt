package de.mario.camera.settings

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.preference.CheckBoxPreference
import de.mario.camera.R
import de.mario.camera.device.PackageLookup
import de.mario.camera.opencv.OpenCvAlert

internal class SettingsChangedListener(private val context: Context) : OnSharedPreferenceChangeListener {

    private val settings = SettingsAccess(context)
    private val lookup = PackageLookup(context)

    internal var hdrCheck: CheckBoxPreference? = null

    internal fun register() = settings.addListener(this)

    internal fun unregister() = settings.removeListener(this)

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        if(isHdr(key) && !hasOpenCv()) {
            resetHdrCheck(preferences, key)
            showAlert()
        }
    }

    private fun resetHdrCheck(preferences: SharedPreferences?, key: String?) {
        unregister()
        hdrCheck?.setChecked(false)
        preferences?.edit()?.putBoolean(key, false)?.commit()
        register()
    }

    internal fun showAlert() = OpenCvAlert.show(context)

    private fun isHdr(key: String?) =
            key.equals(string(R.string.hdr)) && settings.isEnabled(R.string.hdr)

    private fun string(key: Int) = context.getString(key)

    private fun hasOpenCv() = lookup.exists(PackageLookup.OPENCV)
}