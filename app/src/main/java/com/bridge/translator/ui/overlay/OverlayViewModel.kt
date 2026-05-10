package com.bridge.translator.ui.overlay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class OverlayViewModel : ViewModel() {

    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> = _snackbarMessage

    fun onSelectAreaClicked() {
        _snackbarMessage.value = "Select Area feature coming soon"
    }
}
