package com.machina.playtify.player

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.machina.playtify.R
import com.machina.playtify.core.Constants.NOTIFICATION_CHANNEL_ID
import com.machina.playtify.core.Constants.NOTIFICATION_ID

class MusicNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallback: () -> Unit
) {

    private val notificationManager: PlayerNotificationManager

    init {
        notificationManager = PlayerNotificationManager
            .Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .setChannelNameResourceId(R.string.notification_channel_name)
            .setChannelDescriptionResourceId(R.string.notification_channel_description)
            .setMediaDescriptionAdapter(mediaController)
            .setNotificationListener(notificationListener)
            .build().apply {
                setSmallIcon(R.drawable.ic_baseline_music)
            }
    }
}