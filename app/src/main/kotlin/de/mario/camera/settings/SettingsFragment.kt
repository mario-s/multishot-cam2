package de.mario.camera.settings

import android.content.Intent
import android.preference.ListPreference
import android.preference.PreferenceCategory
import de.mario.camera.R

/**
 */
class SettingsFragment : android.preference.PreferenceFragment() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(de.mario.camera.R.xml.preferences);

        val intent = activity.intent
        addImageResolutions(intent)
    }

    private fun addImageResolutions(intent: Intent) {
        val catKey = getString(R.string.cat_camera)
        val prefKey = getString(R.string.pictureSize)

        val resolutions = intent.getStringArrayExtra(SettingsLauncher.RESOLUTIONS)
        val selected = intent.getStringExtra(prefKey)

        val customListPref = preferenceCategory(catKey).findPreference(prefKey) as ListPreference
        customListPref.entries = resolutions
        customListPref.entryValues = resolutions
        customListPref.value = selected
    }

    private fun preferenceCategory(catKey: String): PreferenceCategory = findPreference(catKey) as PreferenceCategory

}