package com.machina.playtify.core

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import com.google.common.reflect.Reflection.getPackageName
import com.machina.playtify.R

object Constants {
    val mediaItems = listOf(
        MediaBrowserCompat.MediaItem(
            MediaDescriptionCompat.Builder()
                .setTitle("Imagination")
                .setSubtitle("Shawn Mendes")
                .setIconUri(Uri.parse("android.resource://com.machina.playtify/${R.drawable.album_cover_bbibbi}"))
                .setMediaUri(Uri.parse("android.resource://com.machina.playtify/${R.raw.imagination_shawn}"))
                .build(),
            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        ),
        MediaBrowserCompat.MediaItem(
            MediaDescriptionCompat.Builder()
                .setTitle("I Like Me Better")
                .setSubtitle("Lauv")
                .setIconUri(Uri.parse("android.resource://com.machina.playtify/${R.drawable.taeyeon_playlist}"))
                .setMediaUri(Uri.parse("android.resource://com.machina.playtify/${R.raw.likemebetter_lauv}"))
                .build(),
            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        )
    )
}