package de.mario.camera.process

import android.content.Context;
import android.app.PendingIntent
import android.provider.MediaStore
import android.content.Intent
import android.app.NotificationManager
import de.mario.camera.R
import android.support.v4.app.NotificationCompat;


class NotificationSender(private val context: Context) {

    private val title: String = context.getString(R.string.title_merge_process)
    private val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun send(text: String) = nManager.notify(12345, addIntent(builder(text)).build())

    private fun addIntent(builder: NotificationCompat.Builder): NotificationCompat.Builder {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return builder.setContentIntent(pendingIntent)
    }

    private fun builder(text: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.camera_burst_white_36dp)
                .setContentTitle(title)
                .setContentText(text)
    }
}