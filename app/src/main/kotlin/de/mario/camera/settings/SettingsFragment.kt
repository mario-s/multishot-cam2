package de.mario.camera.settings

import android.os.Bundle
import android.preference.ListPreference
import android.preference.PreferenceFragment

/**
 */
class SettingsFragment : android.preference.PreferenceFragment() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(de.mario.camera.R.xml.preferences);
    }

    private fun createListPreference(available: Array<String>, selected: String): ListPreference {
        val customListPref = ListPreference(context)
        customListPref.isPersistent = true
        customListPref.entries = available
        customListPref.entryValues = available
        customListPref.value = selected
        return customListPref
    }
}