package com.machina.playtify.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.machina.playtify.databinding.VhItemSongBinding
import javax.inject.Inject


class HomeSongAdapter(
    private val onSongClick: () -> Unit
): RecyclerView.Adapter<VhItemSong>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItemSong {
        val inflater = LayoutInflater.from(parent.context)

        val binding = VhItemSongBinding.inflate(inflater, parent, false)
        return VhItemSong(binding)
    }

    override fun onBindViewHolder(holder: VhItemSong, position: Int) {
        holder.onBind(onSongClick)
    }

    override fun getItemCount(): Int {
        return 12
    }


}

class VhItemSong(private val binding: VhItemSongBinding): RecyclerView.ViewHolder(binding.root) {

    fun onBind(onSongClick: () -> Unit) {
        binding.vhItemSongContainer.setOnClickListener {
            onSongClick()
        }
    }
}