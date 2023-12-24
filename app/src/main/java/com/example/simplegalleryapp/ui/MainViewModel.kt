package com.example.simplegalleryapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.simplegalleryapp.util.Event
import com.example.simplegalleryapp.util.PermissionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val _state: MutableStateFlow<Event<ViewState?>> = MutableStateFlow(Event(null))
    val state = _state.asStateFlow()

    init {
        checkUiState()
    }

    fun checkUiState() {
        if (PermissionUtils.arePermissionsGranted(application)) {
            _state.value = Event(ViewState.LaunchAlbumListScreen)
        } else {
            _state.value = Event(ViewState.RequestPermission)
        }
    }
}
