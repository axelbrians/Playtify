package com.machina.playtify.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.machina.playtify.MainActivity
import com.machina.playtify.adapter.HomeSongAdapter
import com.machina.playtify.databinding.FragmentHomeBinding
import com.machina.playtify.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAdapter: HomeSongAdapter
    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var glide: RequestManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupObserver()
        setupRecycler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.currentTrackBottomBar.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCurrentTrackFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupObserver() {
        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
    }

    private fun setupRecycler() {
        mAdapter = HomeSongAdapter { Timber.d("Song item clicked") }
        binding.songRecycler.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}