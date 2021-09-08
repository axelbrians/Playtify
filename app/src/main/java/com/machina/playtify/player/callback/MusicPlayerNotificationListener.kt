package com.machina.playtify.player.callback

import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.machina.playtify.core.Constants.NOTIFICATION_ID
import com.machina.playtify.player.MediaPlaybackService
import timber.log.Timber

class MusicPlayerNotificationListener(
    private val mediaPlaybackService: MediaPlaybackService
): PlayerNotificationManager.NotificationListener {

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        mediaPlaybackService.apply {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        mediaPlaybackService.apply {
           Timber.d("onGoing $ongoing || isForegroundService $isForegroundService")
            if (ongoing && !isForegroundService) {
//                Timber.d("Starting service and notification")
                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext, this::class.java)
                )
                startForeground(NOTIFICATION_ID, notification)
                isForegroundService = true
            } else if (!ongoing){
                Timber.d("Notification is now dismissible")
            }
        }
    }
}