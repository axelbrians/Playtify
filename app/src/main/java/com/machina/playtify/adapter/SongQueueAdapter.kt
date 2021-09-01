package com.machina.playtify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.machina.playtify.databinding.VhItemQueueBinding
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
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItemBaseQueue {
        val inflater = LayoutInflater.from(parent.context)

        val binding = VhItemQueueBinding.inflate(inflater, parent, false)

        return VhItemQueue(binding)
    }

    override fun onBindViewHolder(holder: VhItemBaseQueue, position: Int) {
        when (holder) {
            is VhItemQueue -> holder.onBind(songs[position])
        }
    }

    override fun getItemCount(): Int {
        return songs.size
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