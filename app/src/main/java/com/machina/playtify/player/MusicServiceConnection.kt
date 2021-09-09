package com.machina.playtify.player

import android.content.ComponentName
import android.content.Context
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.machina.playtify.core.Constants.NETWORK_ERROR
import com.machina.playtify.model.Event
import com.machina.playtify.model.Resource
import com.machina.playtify.model.Song
import timber.log.Timber

class MusicServiceConnection(
    context: Context
) {

    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    val networkError: LiveData<Event<Resource<Boolean>>> = _networkError

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

    private val _currentPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    val currentPlayingSong: LiveData<MediaMetadataCompat?> = _currentPlayingSong

    private val _currentQueue = MutableLiveData<List<Song>>()
    val currentQueue: LiveData<List<Song>> = _currentQueue

    private val _shuffleMode = MutableLiveData<Int>()
    val shuffleMode: LiveData<Int> = _shuffleMode

    private val _repeatMode = MutableLiveData<Int>()
    val repeatMode: LiveData<Int> = _repeatMode

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private val mediaBrowserCompat = MediaBrowserCompat(
        context,
        ComponentName(context, MediaPlaybackService::class.java),
        mediaBrowserConnectionCallback,
        null
    ).apply { connect() }

    lateinit var mediaController: MediaControllerCompat

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowserCompat.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowserCompat.unsubscribe(parentId, callback)
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowserCompat.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            _isConnected.postValue(Event(Resource.success(true)))
        }

        override fun onConnectionSuspended() {
            _isConnected.postValue(Event(Resource.error(
                "The connection was suspended",
                false
            )))
        }

        override fun onConnectionFailed() {
            _isConnected.postValue(Event(Resource.error(
                "Couldn't connect to media browser",
                false
            )))
        }
    }


    private inner class MediaControllerCallback: MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            _playbackState.postValue(state)
            updateCurrentQueue(mediaController.queue)
//            Timber.d("newPlaybackState $state")
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
            _currentQueue.postValue(queue?.map { it.toSong() })
//            updateCurrentQueue(queue)
            var newQueues = ""
            queue?.forEach {
                newQueues = "$newQueues | ${it.description.title}"
            }
            Timber.d("callback queue $newQueues")
        }

        private fun updateCurrentQueue(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            val repeat = mediaController.repeatMode
            val value = mediaController.metadata?.toSong()
            var songs = queue?.map { it.toSong() }?.toMutableList()
//            logQueues("old", songs)
            if (value != null && songs != null) {
                val tempList = mutableListOf<Song>()
                var currentIndex = songs.indexOfFirst { it.id == value.id }
                if (currentIndex == -1) currentIndex = 0

                when (repeat) {
                    PlaybackStateCompat.REPEAT_MODE_NONE -> {
                        tempList.addAll(songs.subList(currentIndex, songs.size))
                    }
                    else -> {
                        tempList.addAll(songs.subList(currentIndex, songs.size))
                        tempList.addAll(songs.subList(0, currentIndex))
                    }
                }
                songs = tempList
            }
//            logQueues("new", songs)
            songs?.let {
//                Timber.d("return new queue at songs")
                _currentQueue.postValue(it)
                return
            }
            queue?.let { tempQueue ->
//                Timber.d("return new queue queue")
                _currentQueue.postValue(tempQueue.map { it.toSong() })
            }
        }

        private fun logQueues(tag: String, queues: List<Song>?) {
            var newQueues = ""
            queues?.forEach {
                newQueues = "$newQueues | ${it.title}"
            }
            Timber.d("$tag $newQueues")
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            _repeatMode.postValue(repeatMode)
        }

        override fun onShuffleModeChanged(shuffleMode: Int) {
            super.onShuffleModeChanged(shuffleMode)
            _shuffleMode.postValue(shuffleMode)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            _repeatMode.postValue(mediaController.repeatMode)
            _shuffleMode.postValue(mediaController.shuffleMode)
            _currentPlayingSong.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when (event) {
                NETWORK_ERROR -> _networkError.postValue(
                    Event(Resource.error(
                        "Couldn't connect to server. Please check your internet connection",
                        null
                    ))
                )
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}