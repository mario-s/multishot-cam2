package de.mario.camera

import android.app.Activity
import android.os.Bundle
import android.util.Log

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null){
            fragmentManager.beginTransaction()
                    .replace(R.id.container, CameraFragment.newInstance())
                    .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MultiShot", "finished!")
    }
}
