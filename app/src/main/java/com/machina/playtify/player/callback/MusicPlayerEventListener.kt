package com.machina.playtify.player.callback

import android.widget.Toast
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.machina.playtify.player.MediaPlaybackService

class MusicPlayerEventListener(
    private val musicPlaybackService: MediaPlaybackService
) : Player.Listener {

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
        if (reason == Player.STATE_READY && !playWhenReady) {
            musicPlaybackService.stopForeground(false)
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicPlaybackService, "An unknown error occured", Toast.LENGTH_LONG).show()
    }
}