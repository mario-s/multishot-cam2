package de.mario.camera.settings

import android.app.Activity
import android.os.Bundle

/**
 *
 */
class SettingsActivity : Activity() {

    private val listener = SettingsChangedListener(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener.register()

        fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        listener.unregister()
    }


}