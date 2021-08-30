package com.machina.playtify.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.machina.playtify.databinding.VhItemSongBinding
import com.machina.playtify.model.Song
import javax.inject.Inject


class HomeSongAdapter @Inject constructor(
    private val glide: RequestManager
): RecyclerView.Adapter<VhItemSong>() {


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

    private var onSongClickListener: ((Song) -> Unit)? = null

    fun setOnSongClickListener(listener: (Song) -> Unit) {
        onSongClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItemSong {
        val inflater = LayoutInflater.from(parent.context)

        val binding = VhItemSongBinding.inflate(inflater, parent, false)
        return VhItemSong(binding)
    }

    override fun onBindViewHolder(holder: VhItemSong, position: Int) {
        holder.onBind(songs[position], glide, onSongClickListener)
    }

    override fun getItemCount(): Int {
        return songs.size
    }


}

class VhItemSong(private val binding: VhItemSongBinding): RecyclerView.ViewHolder(binding.root) {

    fun onBind(song: Song, glide: RequestManager, onSongClick: ((Song) -> Unit)?) {
        binding.vhItemSongContainer.setOnClickListener {
            onSongClick?.let { onSongClick(song) }
        }

        binding.vhItemSongTitle.text = song.title
        binding.vhItemSongArtist.text = song.subtitle
        glide.load(song.imageUrl).into(binding.vhItemSongImg)
    }
}