package com.machina.playtify.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.machina.playtify.R
import com.machina.playtify.adapter.HomeSongAdapter
import com.machina.playtify.databinding.FragmentHomeBinding
import com.machina.playtify.model.Song
import com.machina.playtify.model.Status
import com.machina.playtify.player.isPlayEnabled
import com.machina.playtify.player.isPlaying
import com.machina.playtify.player.toSong
import com.machina.playtify.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var mAdapter: HomeSongAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupRecycler()
        setupObserver()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mAdapter.setOnSongClickListener { song ->
            Timber.d("Song clicked $song")
            viewModel.playOrToggleSong(song)
        }
    }

    private fun setupRecycler() {
        binding.songRecycler.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObserver() {
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        viewModel.mediaItems.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    binding.progressCircular.isVisible = false
                    if (resource.data is List<Song>)
                        mAdapter.songs = resource.data
                }
                Status.ERROR -> {
                    binding.progressCircular.isVisible = false
                }
                else -> {
                    binding.progressCircular.isVisible = true
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}