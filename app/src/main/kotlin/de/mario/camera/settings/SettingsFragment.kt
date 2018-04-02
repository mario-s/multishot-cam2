package de.mario.camera.settings

import android.content.Intent
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.ListPreference
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.view.View
import de.mario.camera.R


internal class SettingsFragment : PreferenceFragment() {

    private lateinit var listener: SettingsChangedListener

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        listener = SettingsChangedListener(context)
        listener.register()
        listener.hdrCheck = category(R.string.cat_hdr).findPreference(getString(R.string.hdr)) as CheckBoxPreference
    }

    override fun onDestroy() {
        super.onDestroy()
        listener.unregister()
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences);

        val intent = activity.intent
        addImageResolutions(intent)
    }

    private fun addImageResolutions(intent: Intent) {
        val prefKey = getString(R.string.pictureSize)

        val resolutions = intent.getStringArrayExtra(SettingsLauncher.RESOLUTIONS)
        val selected = intent.getStringExtra(prefKey)

        val customListPref = category(R.string.cat_camera).findPreference(prefKey) as ListPreference
        customListPref.entries = resolutions
        customListPref.entryValues = resolutions
        customListPref.value = selected
    }
    
    private fun category(key: Int): PreferenceCategory = findPreference(getString(key)) as PreferenceCategory

}