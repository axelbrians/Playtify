package com.machina.playtify.adapter

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.machina.playtify.databinding.VhItemQueueBinding
import com.machina.playtify.model.Song
import com.machina.playtify.player.toSong
import javax.inject.Inject

class SongQueueAdapter @Inject constructor(
    private val glide: RequestManager
): RecyclerView.Adapter<VhItemBaseQueue>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    var currentPlayingSong: MediaMetadataCompat? = null
        set(value) {
            field = value
            if (value != null) {
                val song = value.toSong()
                val currentIndex = songs.indexOfFirst { it.id == song.id }
                val tempList = mutableListOf<Song>()
                when (repeatMode) {
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
        }

    var repeatMode: Int = PlaybackStateCompat.SHUFFLE_MODE_NONE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItemBaseQueue {
        val inflater = LayoutInflater.from(parent.context)

        val binding = VhItemQueueBinding.inflate(inflater, parent, false)

        return VhItemQueue(binding)
    }

    override fun onBindViewHolder(holder: VhItemBaseQueue, position: Int) {
        when (holder) {
            is VhItemQueue -> {
                if (repeatMode == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
                    holder.onBind(songs[position])
                } else {
                    holder.onBind(songs[position % songs.size])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        if (repeatMode == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
            return songs.size
        } else {
            return Int.MAX_VALUE
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}

abstract class VhItemBaseQueue(view: View): RecyclerView.ViewHolder(view)

class VhItemQueue(private val binding: VhItemQueueBinding)
    : VhItemBaseQueue(binding.root) {

    fun onBind(song: Song) {
        binding.itemQueueTitle.text = song.title
        binding.itemQueueArtist.text = song.subtitle
    }
}