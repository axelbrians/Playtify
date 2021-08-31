package com.machina.playtify.player.callback

import android.widget.Toast
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.machina.playtify.player.MediaPlaybackService
import timber.log.Timber

class MusicPlayerEventListener(
    private val musicPlaybackService: MediaPlaybackService
) : Player.Listener {

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
//        Timber.d("onPlayWhenReady")
        if (reason == Player.STATE_READY && !playWhenReady) {
            musicPlaybackService.stopForeground(false)
        }
    }

//    override fun onPlaybackStateChanged(playbackState: Int) {
//        super.onPlaybackStateChanged(playbackState)
//        Timber.d("playbackState from PlayerEvent $playbackState")
//    }

//    override fun onEvents(player: Player, events: Player.Events) {
//        super.onEvents(player, events)
//        Timber.d("player $player | events $events")
//    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
//        Toast.makeText(musicPlaybackService, "An unknown error occured", Toast.LENGTH_LONG).show()
    }
}