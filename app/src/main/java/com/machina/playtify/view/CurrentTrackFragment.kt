package com.machina.playtify.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.machina.playtify.R
import com.machina.playtify.databinding.FragmentCurrentTrackBinding
import com.machina.playtify.player.isPlayEnabled
import com.machina.playtify.player.isPlaying
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

    private var currentPlayingSong: MediaMetadataCompat? = null

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
                Glide.with(requireContext()).asBitmap()
                    .load(currentSong.description.iconUri)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onLoadFailed(errorDrawable: Drawable?) {

                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            Timber.d("Glide load for palette completed")
                            onGlideResourceReadyCallback(resource)
                            binding.fragmentCurrentTrackImage.setImageBitmap(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
//                loadGradientBackground(currentSong.description.iconUri)
//                glide.load(currentSong.description.iconUri).into(binding.fragmentCurrentTrackImage)
                binding.fragmentCurrentTrackTitle.text = currentSong.description?.title.toString()
                binding.fragmentCurrentTrackSubtitle.text = currentSong.description?.subtitle.toString()
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

    private fun onGlideResourceReadyCallback(bitmap: Bitmap) {
        // Palette is used to generate color based on the Bitmap
        // it can generate several color which some option can be null
        // Read more about Palette in official docs
        val paletteBuilder = Palette.Builder(bitmap).maximumColorCount(16)
        var palette = paletteBuilder.generate().getDarkMutedColor(0)
        if (palette == 0) palette = paletteBuilder.generate().getMutedColor(0)
        if (palette == 0) palette = paletteBuilder.generate().getLightMutedColor(0)
        if (palette == 0) palette = paletteBuilder.generate().getDominantColor(0)

        // this color Array used to create GradientDrawable effect that later will
        // be applied to image header
        val color = IntArray(2)
        color[0] = palette
        color[1] = 0xFF121212.toInt()
        Timber.d("palette: $palette")

        // Generate GradientDrawable based on the color that has been extracted
        // from the Bitmap above with Palette
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            color
        )
        gradientDrawable.cornerRadius = 0f

        binding.fragmentCurrentTrackContainer.background = gradientDrawable
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}