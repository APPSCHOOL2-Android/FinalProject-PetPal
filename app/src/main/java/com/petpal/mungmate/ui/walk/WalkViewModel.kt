package com.petpal.mungmate.ui.walk

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.petpal.mungmate.model.Favorite
import com.petpal.mungmate.model.KakaoSearchResponse
import com.petpal.mungmate.model.Match
import com.petpal.mungmate.model.PlaceData
import com.petpal.mungmate.model.ReceiveUser
import com.petpal.mungmate.model.Review
import com.petpal.mungmate.utils.onWalk.onWalk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WalkViewModel(private val repository: WalkRepository,application: Application) : AndroidViewModel(application) {

    val walkStartTime: Long = System.currentTimeMillis()
    private var startTimestamp: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    val elapsedTimeLiveData = MutableLiveData<String>()
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
    val usersOnWalk: MutableLiveData<List<ReceiveUser>> = MutableLiveData()
    val walkMatchingCount: MutableLiveData<Int> = MutableLiveData()
    private val _isUserBlocked = MutableLiveData<Boolean>()
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private var lastLocation: Location? = null
    val distanceMoved: MutableLiveData<Float> = MutableLiveData(0f)
    val matchesLiveData: MutableLiveData<List<Match>> = MutableLiveData()
    val averageRatingForUser: LiveData<Double> get() = _averageRatingForUser
    private val _averageRatingForUser = MutableLiveData<Double>()
    val usersOnWalkLocationChanges: MutableLiveData<List<ReceiveUser>> = MutableLiveData()

    val isUserBlocked: LiveData<Boolean> get() = _isUserBlocked
    val timerRunnable = object : Runnable {
        override fun run() {
            val elapsedTime = (System.currentTimeMillis() - startTimestamp) / 1000
            elapsedTimeLiveData.value = elapsedTime.toString()
            handler.postDelayed(this, 1000)
        }
    }

    // 타이머 시작
    fun startTimer() {
        startTimestamp = System.currentTimeMillis()
        handler.post(timerRunnable)
    }

    // 타이머 중지
    fun stopTimer() {
        handler.removeCallbacks(timerRunnable)
    }


    override fun onCleared() {
        super.onCleared()
        // ViewModel이 파괴될 때 Handler 콜백을 취소하여 메모리 누수를 방지합니다.
        handler.removeCallbacks(timerRunnable)
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0 ?: return
            val locationList = mutableListOf<Location>()
            for (location in p0.locations) {
                updateDistance(location)
            }

        }
    }
    private fun updateDistance(location: Location) {
        lastLocation?.let {
            val distance = it.distanceTo(location)
            distanceMoved.postValue((distanceMoved.value ?: 0f) + distance)
            val totalDistance = (distanceMoved.value ?: 0f) + distance
            distanceMoved.postValue(totalDistance)
            Log.d("WalkViewModel", "Moved distance: $distance, Total distance: $totalDistance")
        }
        lastLocation = location
    }

    fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }
//    override fun onCleared() {
//        super.onCleared()
//        stopLocationUpdates()  // ViewModel이 종료될 때 위치 업데이트 중지
//    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

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

    fun addPlaceToFavorite(placeData: PlaceData, favorite: Favorite) {
        viewModelScope.launch {
            try {
                repository.addFavorite(placeData, favorite)
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Failed to add placeData to favorites")
            }
        }
    }

    fun updateUserLocation(userId: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                repository.updateLocation(userId, latitude, longitude)
                // 필요한 경우 성공 메시지나 상태 업데이트
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Failed to update location")
            }
        }
    }

    fun setOnWalkStatusTrue(userId: String) {
        viewModelScope.launch {
            try {
                repository.updateOnWalkStatusTrue(userId)
                // 필요한 경우 성공 메시지나 상태 업데이트
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Failed to set onWalk status")
            }
        }
    }
    fun updateOnWalkStatusFalse(userId: String) {
        viewModelScope.launch {
            try {
                repository.updateOnWalkStatusFalse(userId)
                // 필요한 경우 성공 메시지나 상태 업데이트
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Failed to update location")
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
    fun observeUsersOnWalk() {
        viewModelScope.launch {
            repository.observeUsersOnWalkWithPets().collect { users ->
                usersOnWalk.postValue(users)
            }
        }
    }
    fun observeUsersOnWalkLocation() {
        viewModelScope.launch {
            repository.observeUsersOnWalkLocationChanges().collect { users ->
                usersOnWalkLocationChanges.postValue(users)
            }
        }
    }
    fun fetchMatchingWalkCount(userId: String) {
        viewModelScope.launch {
            try {
                val count = repository.fetchMatchingWalkCount(userId)
                walkMatchingCount.postValue(count)
            } catch (e: Exception) {
                errorMessage.postValue(e.localizedMessage ?: "Failed to fetch matching walk count")
            }
        }
    }
    fun updateLocationIfOnWalk(userId: String, latitude: Double, longitude: Double) {
        if (onWalk == true) {
            viewModelScope.launch {
                try {
                    repository.updateLocationIfOnWalk(userId, latitude, longitude)
                    // 업데이트 성공
                } catch (e: Exception) {
                    // 업데이트 실패: 예외 처리
                    errorMessage.postValue(e.localizedMessage ?: "Failed to update location")
                }
            }
        }
    }
    fun blockUser(userId: String, blockId: String) {
        viewModelScope.launch {
            try {
                repository.updateBlockUser(userId, blockId)
                _isUserBlocked.value = true
            } catch (e: Exception) {
                // 에러 처리
                _isUserBlocked.value = false
            }
        }
    }
    //    fun fetchMatchesByUserId(userId: String) {
//        viewModelScope.launch {
//            try {
//                val matches = repository.fetchMatchesByUserId(userId)
//                matchesLiveData.postValue(matches)
//            } catch (e: Exception) {
//                //
//            }
//        }
//    }
    fun fetchMatchesByUserId(userId: String) {
        repository.fetchMatchesByUserId(userId,
            onSuccess = { matches ->
                matchesLiveData.postValue(matches)
            },
            onFailure = { exception ->
                // Handle the error here, if necessary.
                // For example, you might post a different value to a LiveData or log the exception.
            })
    }
    fun fetchAverageRatingForUser(userId: String) {
        viewModelScope.launch {
            try {
                val averageRating = repository.fetchAverageRatingForUser(userId)
                _averageRatingForUser.postValue(averageRating)
                Log.d("에바야view",averageRating.toString())
            } catch (e: Exception) {
                // 필요한 경우 에러 처리 로직
            }
        }
    }

}