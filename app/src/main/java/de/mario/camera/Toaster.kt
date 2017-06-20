package de.mario.camera

import android.app.Activity
import android.widget.Toast



/**
 */
class Toaster(val activity: Activity?) {

    fun showToast(text: String) {
        activity!!.runOnUiThread(Runnable {
            Toast.makeText(activity!!, text, Toast.LENGTH_SHORT).show()
        })
    }
}