package de.mario.camera

import android.app.Activity
import android.app.Fragment
import android.widget.Toast



/**
 */
class Toaster(val fragment: Fragment) {

    fun showToast(text: String) {
        val activity = fragment.activity
        activity.runOnUiThread(Runnable {
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
        })
    }
}