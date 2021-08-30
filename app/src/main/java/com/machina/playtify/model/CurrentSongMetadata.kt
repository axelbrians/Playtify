package com.machina.playtify.model

import android.content.Context
import android.net.Uri
import kotlin.math.floor

data class CurrentSongMetadata(
    val id: String,
    val albumArtUri: Uri,
    val title: String?,
    val subtitle: String?,
    val duration: Long
) {
    companion object {
        /**
         * Utility method to convert milliseconds to a display of minutes and seconds
         */
        fun timestampToMSS(context: Context, position: Long): Int {
            val totalSeconds = floor(position / 1E3).toInt()
            val minutes = totalSeconds / 60
            return totalSeconds - (minutes * 60)
        }
    }
}
