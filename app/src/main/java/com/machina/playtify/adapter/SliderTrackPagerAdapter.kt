package com.machina.playtify.adapter

import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.machina.playtify.databinding.VhItemSliderTrackBinding
import com.machina.playtify.model.Song

class SliderTrackPagerAdapter: RecyclerView.Adapter<VhItemSliderTrack>() {

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
        set(value) = differ.submitList(value)

    var repeatMode: Int = PlaybackStateCompat.SHUFFLE_MODE_NONE

    private var onSongClickListener: ((Song) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItemSliderTrack {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VhItemSliderTrackBinding.inflate(inflater, parent,false)

        return VhItemSliderTrack(binding)
    }

    override fun onBindViewHolder(holder: VhItemSliderTrack, position: Int) {
        if (repeatMode == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
            holder.onBind(songs[position])
        } else {
            holder.onBind(songs[position % songs.size])
        }
    }

    override fun getItemCount(): Int {
        return if (repeatMode == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
            songs.size
        } else {
            Int.MAX_VALUE
        }
    }
}

class VhItemSliderTrack(private val binding: VhItemSliderTrackBinding): RecyclerView.ViewHolder(binding.root) {

    fun onBind(song: Song) {
        binding.currentTrackTitle.text = song.title
        binding.currentTrackArtist.text = song.subtitle
        binding.root.setOnClickListener {

        }
    }
}