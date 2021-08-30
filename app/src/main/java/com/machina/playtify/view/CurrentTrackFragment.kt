package com.machina.playtify.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.slider.Slider
import com.machina.playtify.R
import com.machina.playtify.core.Helper
import com.machina.playtify.databinding.FragmentCurrentTrackBinding
import com.machina.playtify.player.isPlayEnabled
import com.machina.playtify.player.isPlaying
import com.machina.playtify.player.toSong
import com.machina.playtify.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CurrentTrackFragment : Fragment() {

    private var _binding: FragmentCurrentTrackBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var glide: RequestManager

    private var shouldUpdateSeekbar = true

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

        binding.fragmentCurrentTrackPlaybackControl.setOnClickListener {
            viewModel.currentPlayingSong.value?.toSong()?.let { song ->
                viewModel.playOrToggleSong(song, true)
            }
        }

        binding.fragmentCurrentTrackPrevious.setOnClickListener {
            viewModel.skipToPrevious()
        }

        binding.fragmentCurrentTrackNext.setOnClickListener {
            shouldUpdateSeekbar = false
            viewModel.skipToNextSong()
        }

        binding.fragmentCurrentTrackSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.fragmentCurrentTrackElapsed.text = Helper.millisToMMSS(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    viewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }
            }

        })


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
                Glide.with(requireContext()).asBitmap()
                    .load(currentSong.description.iconUri)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onLoadFailed(errorDrawable: Drawable?) {  }

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            Timber.d("Glide load for palette completed")
                            binding.fragmentCurrentTrackContainer.loadBitMapAsDarkMutedGradientBackground(
                                resource,
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                0f
                            )
                            binding.fragmentCurrentTrackImage.setImageBitmap(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {  }
                    })
                val maxProgress = currentSong.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt()
                with(binding) {
                    fragmentCurrentTrackTitle.text = currentSong.description?.title.toString()
                    fragmentCurrentTrackSubtitle.text = currentSong.description?.subtitle.toString()
                    fragmentCurrentTrackSlider.max = maxProgress
                    fragmentCurrentTrackTotalTime.text = Helper.millisToMMSS(maxProgress.toLong())
                }
                shouldUpdateSeekbar = true
            }
        }

        viewModel.currentPlayerPosition.observe(viewLifecycleOwner) { position ->
//            Timber.d("CurrentPosition $position")
            if (shouldUpdateSeekbar) {
                binding.fragmentCurrentTrackSlider.progress = position.toInt()
            }
            binding.fragmentCurrentTrackElapsed.text = Helper.millisToMMSS(position)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}