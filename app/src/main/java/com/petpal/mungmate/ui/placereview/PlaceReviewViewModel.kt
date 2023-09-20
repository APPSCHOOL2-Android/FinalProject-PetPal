package com.petpal.mungmate.ui.placereview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petpal.mungmate.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaceReviewViewModel(private val repository: PlaceReviewRepository) : ViewModel() {

    // MutableStateFlow로 reviews 관찰
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    // placeId에 해당하는 모든 리뷰를 가져오고 _reviews를 업데이트하는 함수
    fun fetchAllReviewsForPlace(placeId: String) {
        viewModelScope.launch {
            repository.fetchAllReviewsForPlace(placeId).collect { fetchedReviews ->
                _reviews.value = fetchedReviews
            }
        }
    }
    fun deleteReviewForPlace(placeId: String, reviewId: String) {
        viewModelScope.launch {
            repository.deleteReviewForPlace(placeId, reviewId)
        }
    }
}

