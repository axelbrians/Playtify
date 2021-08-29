package com.machina.playtify.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

): ViewModel() {

    val instance = Firebase
}