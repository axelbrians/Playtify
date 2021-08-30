package com.machina.playtify.view

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.machina.playtify.R
import com.machina.playtify.databinding.FragmentCurrentTrackBinding
import com.machina.playtify.databinding.FragmentHomeBinding
import com.machina.playtify.model.Song
import com.machina.playtify.model.Status
import com.machina.playtify.player.isPlayEnabled
import com.machina.playtify.player.isPlaying
import com.machina.playtify.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CurrentTrackFragment : Fragment() {

    private var _binding: FragmentCurrentTrackBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var glide: RequestManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentTrackBinding.inflate(inflater, container, false)

        setupObserver()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fragmentCurrentTrackArrowDown.setOnClickListener {
            findNavController().navigateUp()
        }


    }

    private fun setupObserver() {
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        viewModel.playbackState.observe(viewLifecycleOwner) { state ->
            if (state?.isPlaying == true) {
                binding.fragmentCurrentTrackPlaybackControl.setImageResource(R.drawable.ic_pause_cirrcle_white)
            } else if (state?.isPlayEnabled == true) {
                binding.fragmentCurrentTrackPlaybackControl.setImageResource(R.drawable.ic_play_circle_white)
            }
        }

        viewModel.currentPlayingSong.observe(viewLifecycleOwner) { currentSong ->
            if (currentSong != null) {
                binding.fragmentCurrentTrackTitle.text = currentSong.description?.title.toString()
                binding.fragmentCurrentTrackSubtitle.text = currentSong.description?.subtitle.toString()
                glide.load(currentSong.description.iconUri).into(binding.fragmentCurrentTrackImage)
                val maxProgress = currentSong.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt()
                val totalSecs = maxProgress / 1000
                val minutes = (totalSecs % 3600) / 60
                val seconds = totalSecs % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)
                binding.fragmentCurrentTrackSlider.value = 0f
                binding.fragmentCurrentTrackSlider.valueTo = maxProgress.toFloat()
                binding.fragmentCurrentTrackTotalTime.text = timeString
            }
        }

        viewModel.currentPlayerPosition.observe(viewLifecycleOwner) { position ->
//            Timber.d("CurrentPosition $position")
            if (position < (binding.fragmentCurrentTrackSlider.valueTo - 900)) {
                binding.fragmentCurrentTrackSlider.value = position.toFloat()
            }
            val totalSecs = position / 1000
            val minutes = (totalSecs % 3600) / 60
            val seconds = totalSecs % 60
            val timeString = String.format("%02d:%02d", minutes, seconds)
            binding.fragmentCurrentTrackElapsed.text = timeString
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}