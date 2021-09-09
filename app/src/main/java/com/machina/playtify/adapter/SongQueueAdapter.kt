package com.machina.playtify.adapter

import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.machina.playtify.R
import com.machina.playtify.databinding.VhItemQueueBinding
import com.machina.playtify.databinding.VhItemQueueNowPlayingBinding
import com.machina.playtify.model.Song
import javax.inject.Inject

class SongQueueAdapter @Inject constructor(
    private val glide: RequestManager
): RecyclerView.Adapter<VhItemBaseQueue>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.title == newItem.title &&
                    oldItem.subtitle == newItem.subtitle &&
                    oldItem.imageUrl == newItem.imageUrl &&
                    oldItem.songUrl == newItem.songUrl
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var songs: List<Song>
        get() = differ.currentList
        set(value) {
            if (listDifferSubmitCallback != null) {
                differ.submitList(value, listDifferSubmitCallback)
            } else {
                differ.submitList(value)
            }
        }

    var repeatMode: Int = PlaybackStateCompat.SHUFFLE_MODE_NONE

    private var listDifferSubmitCallback: (() -> Unit)? = null

    fun setListDifferSubmitCallback(listener: () -> Unit) {
        listDifferSubmitCallback = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItemBaseQueue {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.vh_item_queue_now_playing -> {
                val binding = VhItemQueueNowPlayingBinding.inflate(inflater, parent, false)
                VhItemQueueNowPlaying(binding)
            }
            else -> {
                val binding = VhItemQueueBinding.inflate(inflater, parent, false)
                VhItemQueue(binding)
            }
        }
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
            is VhItemQueueNowPlaying -> {
                holder.onBind(songs.first(), glide)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (repeatMode == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
            songs.size
        } else {
            Int.MAX_VALUE
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            R.layout.vh_item_queue_now_playing
        } else {
            R.layout.vh_item_queue
        }
    }
}

abstract class VhItemBaseQueue(view: View): RecyclerView.ViewHolder(view)

class VhItemQueueNowPlaying(
    private val binding: VhItemQueueNowPlayingBinding
): VhItemBaseQueue(binding.root) {

    fun onBind(song: Song, glide: RequestManager) {
        binding.nowPlayingTitle.text = song.title
        binding.nowPlayingArtist.text = song.subtitle
        glide.load(song.imageUrl).into(
            binding.nowPlayingImage
        )
    }
}

class VhItemQueue(private val binding: VhItemQueueBinding)
    : VhItemBaseQueue(binding.root) {

    fun onBind(song: Song) {
        binding.itemQueueTitle.text = song.title
        binding.itemQueueArtist.text = song.subtitle
    }
}