package de.mario.camera.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import de.mario.camera.R
import de.mario.camera.device.PackageLookup

class SettingsChangedListener(private val context: Context) : OnSharedPreferenceChangeListener {

    private val settings = SettingsAccess(context)
    private val lookup = PackageLookup(context)

    internal fun register() = settings.addListener(this)

    internal fun unregister() = settings.removeListener(this)

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(isExpFusion(key) && !hasOpenCv()) {
            throw UnsupportedOperationException("not yet implemented")
        }
    }

    private fun isExpFusion(key: String?) =
            key.equals(string(R.string.hdr)) && settings.isEnabled(R.string.hdr)

    private fun string(key: Int) = context.getString(key)

    private fun hasOpenCv() = lookup.exists(PackageLookup.OPENCV)
}