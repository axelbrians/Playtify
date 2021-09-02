package com.machina.playtify.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import timber.log.Timber

data class Song(
    var id: String,

    var title: String,

    var subtitle: String,

    var imageUrl: String,

    var songUrl: String,

    var isCurrentlyPlaying: Boolean = false
) {

    companion object {
        fun DocumentSnapshot.toSong(): Song? {
            return try {
                val id = getString("id")!!
                val title = getString("title")!!
                val subtitle = getString("artist")!!
                val imageUrl = getString("imageUrl")!!
                val songUrl = getString("songUrl")!!

                Song(id, title, subtitle, imageUrl, songUrl)
            } catch (e: Exception) {
                Timber.e(e, "Failed to convert DocumentSnapshot to Song")
                null
            }
        }
    }
}
