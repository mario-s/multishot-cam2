package de.mario.camera

import android.util.SparseIntArray
import android.view.Surface

/**
 */
class SurfaceOrientation : SparseIntArray(){
    init {
        append(Surface.ROTATION_0, 90)
        append(Surface.ROTATION_90, 0)
        append(Surface.ROTATION_180, 270)
        append(Surface.ROTATION_270, 180)
    }
}