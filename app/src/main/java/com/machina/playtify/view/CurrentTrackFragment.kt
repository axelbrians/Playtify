package com.machina.playtify.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.machina.playtify.R
import com.machina.playtify.databinding.FragmentCurrentTrackBinding
import com.machina.playtify.databinding.FragmentHomeBinding

class CurrentTrackFragment : Fragment() {

    private var _binding: FragmentCurrentTrackBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentTrackBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}