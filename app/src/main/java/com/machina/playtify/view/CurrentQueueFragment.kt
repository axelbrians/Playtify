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
import com.machina.playtify.adapter.SongQueueAdapter
import com.machina.playtify.databinding.FragmentCurrentQueueBinding
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
    }

    private fun setupRecycler() {
        binding.recyclerView.apply {
            adapter = songQueueAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObserver() {
        mainViewModel.currentQueue.observe(viewLifecycleOwner) { currentQueue ->
            songQueueAdapter.songs = currentQueue
        }

        mainViewModel.repeatMode.observe(viewLifecycleOwner) { repeatMode ->
            songQueueAdapter.repeatMode = repeatMode
        }

//        mainViewModel.currentPlayingSong.observe(viewLifecycleOwner) { currentPlayingSong ->
//            songQueueAdapter.currentPlayingSong = currentPlayingSong
//        }
    }
}