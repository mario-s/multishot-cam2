package de.mario.camera.io

import android.os.Environment
import java.io.File


internal object StorageAccess: StorageAccessable {
    override fun getStorageState(): String {
        return Environment.getExternalStorageState()
    }

    override fun getStorageDirectory(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
    }
}