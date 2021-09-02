package com.machina.playtify.view

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.machina.playtify.R
import com.machina.playtify.adapter.SongQueueAdapter
import com.machina.playtify.databinding.FragmentCurrentQueueBinding
import com.machina.playtify.player.isPlayEnabled
import com.machina.playtify.player.isPlaying
import com.machina.playtify.player.toSong
import com.machina.playtify.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CurrentQueueFragment : Fragment() {

    private var _binding: FragmentCurrentQueueBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var songQueueAdapter: SongQueueAdapter

    private lateinit var mLayoutManager: LinearLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentQueueBinding.inflate(inflater, container, false)
        setupRecycler()
        setupObserver()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.arrowDown.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.currentQueuePlaybackControl.setOnClickListener {
            mainViewModel.currentPlayingSong.value?.toSong()?.let { song ->
                mainViewModel.playOrToggleSong(song, true)
            }
        }

        binding.currentQueuePrevious.setOnClickListener {
            mainViewModel.skipToPrevious()
        }

        binding.currentQueueNext.setOnClickListener {
            mainViewModel.skipToNextSong()
        }
    }

    private fun setupRecycler() {
        songQueueAdapter.setListDifferSubmitCallback(this::listDifferSubmitCallback)
        mLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.apply {
            adapter = songQueueAdapter
            layoutManager = mLayoutManager
        }
    }

    private fun setupObserver() {
        mainViewModel.playbackState.observe(viewLifecycleOwner) { state ->
            if (state?.isPlaying == true) {
                binding.currentQueuePlaybackControl.setImageResource(R.drawable.ic_pause_cirrcle_white)
            } else if (state?.isPlayEnabled == true) {
                binding.currentQueuePlaybackControl.setImageResource(R.drawable.ic_play_circle_white)
            }
        }

        mainViewModel.currentQueue.observe(viewLifecycleOwner) { currentQueue ->
            songQueueAdapter.songs = currentQueue
        }

        mainViewModel.repeatMode.observe(viewLifecycleOwner) { repeatMode ->
            songQueueAdapter.repeatMode = repeatMode
        }

        mainViewModel.currentSongDuration.observe(viewLifecycleOwner) { currentSongDuration ->
            binding.currentQueueLinearProgress.max = (currentSongDuration ?: 0L).toInt()
        }

        mainViewModel.currentPlayerPosition.observe(viewLifecycleOwner) { position ->
            binding.currentQueueLinearProgress.progress =  position.toInt()
        }


    }

    private fun listDifferSubmitCallback() {
        mLayoutManager.scrollToPosition(mLayoutManager.findFirstCompletelyVisibleItemPosition())
    }
}