package com.machina.playtify.data

import com.google.firebase.firestore.FirebaseFirestore
import com.machina.playtify.core.Constants.BASE_SONGS_COLLECTION
import com.machina.playtify.model.Song
import com.machina.playtify.model.Song.Companion.toSong
import kotlinx.coroutines.tasks.await

class FirebaseMusicDatabase {

    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(BASE_SONGS_COLLECTION)

    suspend fun getAllSongs(): List<Song> {
        return try {
            val songs = mutableListOf<Song>()
            songCollection.get().await()
                .forEach { snapshot ->
                    snapshot.toSong()?.let { songs.add(it) }
                }
            return songs
        } catch (e: Exception) {
            emptyList()
        }
    }
}