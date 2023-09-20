package com.petpal.mungmate.ui.walk

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petpal.mungmate.model.Favorite
import com.petpal.mungmate.model.KakaoSearchResponse
import com.petpal.mungmate.model.Place
import com.petpal.mungmate.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WalkViewModel(private val repository: WalkRepository) : ViewModel() {
    val searchResults: MutableLiveData<KakaoSearchResponse> = MutableLiveData()

    val reviewCount: MutableLiveData<Int> = MutableLiveData()
    val latestReviews: MutableLiveData<List<Review>> = MutableLiveData()
    val placeInfo: MutableLiveData<Map<String, Any?>?> = MutableLiveData()
    val isPlaceFavorited = MutableStateFlow<Boolean?>(null)
    private val errorMessage: MutableLiveData<String> = MutableLiveData()
    private val _favoriteCount = MutableStateFlow<Int?>(null)
    val isDataLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val favoriteCount: StateFlow<Int?> = _favoriteCount
    val reviewsForPlace = MutableLiveData<List<Review>>()
    val averageRatingForPlace = MutableLiveData<Float>()
    val userNickname: MutableLiveData<String?> = MutableLiveData()
    fun searchPlacesByKeyword(latitude: Double, longitude: Double, query: String) {
        viewModelScope.launch {
            try {
                val results = repository.searchPlacesByKeyword(latitude, longitude, query)
                searchResults.postValue(results)
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Unknown error")
            }
        }
    }
    fun searchPlacesByKeywordFilter(latitude: Double, longitude: Double, query: String,radius:Int) {
        viewModelScope.launch {
            try {
                val results = repository.searchPlacesByKeywordFilter(latitude, longitude, query,radius)
                searchResults.postValue(results)
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun fetchPlaceInfoFromFirestore(placeId: String) {
        viewModelScope.launch {
            isDataLoading.postValue(true)  // 데이터 로딩 시작
            try {
                val placeData = repository.getPlaceInfoFromFirestore(placeId)
                placeInfo.postValue(placeData)
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Failed to fetch place info")
            } finally {
                isDataLoading.postValue(false)  // 데이터 로딩 완료
            }
        }
    }

    fun fetchAverageRatingForPlace(placeId: String) {
        viewModelScope.launch {
            try {
                val reviews = repository.fetchAllReviewsForPlace(placeId)
                reviewsForPlace.postValue(reviews)
                val totalRating = reviews.map { it.rating ?: 0f }.sum()
                val avgRating = if (reviews.isNotEmpty()) totalRating / reviews.size else 0f
                averageRatingForPlace.postValue(avgRating)

            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Failed to fetch reviews")
            }
        }
    }

    fun fetchFavoriteCount(placeId: String) {
        viewModelScope.launch {
            repository.observeFavoritesChanges(placeId).collect { count ->
                _favoriteCount.value = count
            }
        }
        viewModelScope.launch {
            try {
                val count = repository.getFavoriteCountSuspend(placeId)
                _favoriteCount.value = count
            } catch (e: Exception) {
                errorMessage.value = e.localizedMessage ?: "Failed to fetch favorite count"
            }
        }
    }

    fun fetchReviewCount(placeId: String) {
        viewModelScope.launch {
            try {
                val count = repository.getReviewCountSuspend(placeId)
                reviewCount.postValue(count)
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Failed to fetch review count")
            }
        }
    }
    fun fetchIsPlaceFavoritedByUser(placeId: String, userId: String) {
        viewModelScope.launch {
            try {
                repository.isPlaceFavoritedByUser(placeId, userId).collect { isFavorited ->
                    isPlaceFavorited.value = isFavorited
                }
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Failed to check if place is favorited")
            }
        }
    }

    fun fetchLatestReviewsForPlace(placeId: String) {
        viewModelScope.launch {
            try {
                val reviews = repository.fetchLatestReviewsSuspend(placeId)
                latestReviews.postValue(reviews)
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Failed to fetch latest reviews")
            }
        }
    }

    fun addPlaceToFavorite(place: Place, favorite: Favorite) {
        viewModelScope.launch {
            try {
                repository.addFavorite(place, favorite)
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Failed to add place to favorites")
            }
        }
    }

    fun removeFavorite(placeId: String, userId: String) {
        viewModelScope.launch {
            try {
                repository.removeUserFavorite(placeId, userId)
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Failed to remove favorite")
            }
        }
    }
    fun fetchUserNickname(userId: String) {
        viewModelScope.launch {
            try {
                val nickname = repository.fetchUserNicknameByUid(userId)
                userNickname.postValue(nickname)
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Failed to fetch user's nickname")
            }
        }
    }
}