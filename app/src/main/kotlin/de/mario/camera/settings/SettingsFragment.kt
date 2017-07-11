package de.mario.camera.settings

import android.os.Bundle
import android.preference.PreferenceFragment

/**
 */
class SettingsFragment : android.preference.PreferenceFragment() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(de.mario.camera.R.xml.preferences);
    }
}