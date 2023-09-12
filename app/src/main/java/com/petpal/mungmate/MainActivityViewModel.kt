package com.petpal.mungmate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    private val _postSplashTheme = MutableStateFlow(true)
    val postSplashTheme = _postSplashTheme.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000)
            _postSplashTheme.value = false
        }
    }
}