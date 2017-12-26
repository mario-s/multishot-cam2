package de.mario.camera

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

/**
 * Send and broadcast to force update of media gallery
 */
internal class MediaUpdater(private val context: Context) {

    internal fun sendUpdate(file: File) {
        val uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)
        context.sendBroadcast(intent)
    }

    internal fun sendUpdate(file: String) {
        sendUpdate(File(file))
    }
}
