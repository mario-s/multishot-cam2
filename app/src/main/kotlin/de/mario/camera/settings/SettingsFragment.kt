package de.mario.camera.settings

import android.content.Intent
import android.preference.ListPreference
import android.preference.PreferenceCategory
import de.mario.camera.R
import de.mario.camera.glue.SettingsAccessable

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
        val key = getString(R.string.pictureSize)
        val resolutions = intent.getStringArrayExtra(SettingsLauncher.RESOLUTIONS)
        val selected = intent.getStringExtra(key)
        val customListPref = createListPreference(resolutions, selected);
        customListPref.setKey(key)
        customListPref.setTitle(R.string.prefs_picture_size_title)
        customListPref.setSummary(R.string.prefs_picture_size_description)

        addPreference(customListPref)
    }

    private fun createListPreference(available: Array<String>, selected: String): ListPreference {
        val customListPref = ListPreference(context)
        customListPref.isPersistent = true
        customListPref.entries = available
        customListPref.entryValues = available
        customListPref.value = selected
        return customListPref
    }

    private fun addPreference(customListPref: ListPreference) {
        val cameraCategory = findPreference(getString(R.string.cat_camera)) as PreferenceCategory
        cameraCategory.addPreference(customListPref)
    }
}