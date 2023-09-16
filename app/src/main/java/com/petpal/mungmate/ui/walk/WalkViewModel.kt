package com.petpal.mungmate.ui.walk

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petpal.mungmate.model.KakaoSearchResponse
import kotlinx.coroutines.launch

class WalkViewModel(private val repository: WalkRepository) : ViewModel() {
    val searchResults: MutableLiveData<KakaoSearchResponse> = MutableLiveData()
    private val errorMessage: MutableLiveData<String> = MutableLiveData()

    fun searchPlacesByKeyword(latitude: Double, longitude: Double, query: String) {
        viewModelScope.launch {
            Log.d("sisisi",query)
            try {
                val results = repository.searchPlacesByKeyword(latitude, longitude, query)
                Log.d("API_RESULTS", results.toString())
                searchResults.postValue(results)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error occurred: ${e.localizedMessage}")
                errorMessage.postValue(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}