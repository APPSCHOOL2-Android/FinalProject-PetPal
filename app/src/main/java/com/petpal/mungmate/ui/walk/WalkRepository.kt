package com.petpal.mungmate.ui.walk

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.model.User
import com.petpal.mungmate.model.Favorite
import com.petpal.mungmate.model.KakaoSearchResponse
import com.petpal.mungmate.model.Pet
import com.petpal.mungmate.model.Place
import com.petpal.mungmate.model.ReceiveUser
import com.petpal.mungmate.model.Review
import com.petpal.mungmate.model.UserBasicInfoData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class WalkRepository {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://dapi.kakao.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: KakaoApiService = retrofit.create(KakaoApiService::class.java)
    private val db = FirebaseFirestore.getInstance()
    private val auth= Firebase.auth
    private val user=auth.currentUser
    private val userUid=user?.uid

    suspend fun searchPlacesByKeyword(latitude: Double, longitude: Double, query: String): KakaoSearchResponse {
        return apiService.searchPlacesByKeyword(latitude, longitude, query = query)
    }
    suspend fun searchPlacesByKeywordFilter(latitude: Double, longitude: Double, query: String,radius:Int): KakaoSearchResponse {
        return apiService.searchPlacesByKeyword(latitude, longitude, query = query, radius = radius)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getPlaceInfoFromFirestore(placeId: String): Map<String, Any?>? = suspendCoroutine { continuation ->
        val placeRef = db.collection("places").document(placeId)
        placeRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                continuation.resume(document.data)
            } else {
                continuation.resume(null)
            }
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }

    suspend fun fetchAllReviewsForPlace(placeId: String): List<Review> {
        val reviews = mutableListOf<Review>()
        val reviewRef = db.collection("places").document(placeId).collection("reviews")
        val task = reviewRef.get().await()
        for (document in task) {
            document.toObject(Review::class.java)?.let { reviews.add(it) }
        }
        return reviews
    }


    suspend fun getFavoriteCountSuspend(placeId: String): Int {
        var favoriteCount = 0
        val favoriteRef = db.collection("places").document(placeId).collection("favorite")
        favoriteCount = favoriteRef.get().await().size()
        return favoriteCount
    }

    fun isPlaceFavoritedByUser(placeId: String, userId: String): Flow<Boolean> = flow {
        val placeRef = db.collection("places").document(placeId)
        val placeDocument = placeRef.get().await()

        // placeId에 대한 문서가 존재하지 않으면 false 반환
        if (!placeDocument.exists()) {
            emit(false)
            return@flow  // End the flow after emitting false
        }

        val favoriteRef = placeRef.collection("favorite").document(userId)
        val favoriteDocument = favoriteRef.get().await()
        emit(favoriteDocument.exists())
    }

    suspend fun getReviewCountSuspend(placeId: String): Int {
        var reviewCount = 0
        val reviewRef = db.collection("places").document(placeId).collection("reviews")
        val task = reviewRef.get().await()
        reviewCount = task.size()
        return reviewCount
    }


    suspend fun fetchLatestReviewsSuspend(placeId: String): List<Review> {
        val reviews = mutableListOf<Review>()
        val reviewRef = db.collection("places").document(placeId).collection("reviews")
        val task = reviewRef.orderBy("date", Query.Direction.DESCENDING).limit(2).get().await()
        for (document in task) {
            document.toObject(Review::class.java)?.let { reviews.add(it) }
        }
        return reviews
    }

    suspend fun addFavorite(place: Place, favorite: Favorite) {
        val placesRef = db.collection("places")
        val placeDocument = placesRef.document(place.id)

        val document = placeDocument.get().await()

        if (!document.exists()) {
            //Place가 db에 없음
            placeDocument.set(place).await()
        }

        //Place가 이미 db에 있거나 추가된 다음에 실행됨(await)
        val favoriteRef = db.collection("places").document(place.id).collection("favorite")
        favoriteRef.document(favorite.userid).set(favorite).await()
    }

    suspend fun removeUserFavorite(placeId: String, userid: String) {
        val favoriteRef = db.collection("places").document(placeId).collection("favorite").document(userid)
        favoriteRef.delete().await()
    }
    fun observeFavoritesChanges(placeId: String): Flow<Int> = callbackFlow {
        val favoriteRef = db.collection("places").document(placeId).collection("favorite")
        val registration = favoriteRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }
            val count = snapshot?.documents?.size ?: 0
            trySend(count).isSuccess
        }
        awaitClose { registration.remove() }
    }

    suspend fun fetchUserNicknameByUid(uid: String): String? {
        val userRef = db.collection("users").document(uid)
        val document = userRef.get().await()
        return document.getString("nickname")
    }

    suspend fun updateLocationAndOnWalkStatusTrue(userId: String, latitude: Double, longitude: Double) {
        val userRef = db.collection("users").document(userId)
        userRef.update(mapOf(
            "onWalk" to true,
            "location" to mapOf(
                "latitude" to latitude,
                "longitude" to longitude
            )
        )).await()
    }
    suspend fun updateOnWalkStatusFalse(userId: String) {
        val userRef = db.collection("users").document(userId)
        userRef.update(
            mapOf(
            "onWalk" to false
            )

        ).await()
    }
    suspend fun updateBlockUser(userId: String, blockId: String) {
        val userRef = db.collection("users").document(userId)
        userRef.update(
            "blockUserList", FieldValue.arrayUnion(blockId)
        ).await()
    }
//    @OptIn(ExperimentalCoroutinesApi::class)
//    fun observeUsersOnWalk(): Flow<List<ReceiveUser>> = callbackFlow {
//        val query = db.collection("users").whereEqualTo("onWalk", true)
//        val listenerRegistration = query.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                close(e)
//                return@addSnapshotListener
//            }
//
//            val users = snapshot?.documents?.map { document ->
//                val user = document.toObject(ReceiveUser::class.java) ?: ReceiveUser()
//                user.uid = document.id  // 문서의 ID 설정
//                user
//            } ?: emptyList()
//
//            trySend(users).isSuccess
//        }
//        awaitClose { listenerRegistration.remove() }
//    }
    //chatgpt는 신이다
    suspend fun fetchMatchingWalkCount(userId: String): Int {
        // userId를 사용하여 해당 사용자의 문서를 가져옵니다.
        val userDocRef = db.collection("users").document(userId)
        val userDoc = userDocRef.get().await()

        // walkRecordList 필드를 가져옵니다.
        val walkRecordList = userDoc.get("walkRecordList") as? List<Map<String, Any?>> ?: emptyList()

        // walkMatchingId가 null이 아닌 항목들만 필터링합니다.
        val matchingWalks = walkRecordList.filter {
            it["walkMatchingId"] != null
        }
        // 일치하는 항목의 수를 반환합니다.
        return matchingWalks.size
    }


    fun observeUsersOnWalkWithPets(): Flow<List<ReceiveUser>> = callbackFlow {
        val query = db.collection("users").whereEqualTo("onWalk", true)
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)  // 오류가 발생하면 Flow를 닫습니다.
                return@addSnapshotListener
            }

            val tasks = snapshot?.documents?.map { userDocument ->
                val user = userDocument.toObject(ReceiveUser::class.java) ?: ReceiveUser()
                user.uid = userDocument.id

                userDocument.reference.collection("pets").get().continueWith { petSnapshot ->
                    val pets = petSnapshot.result?.mapNotNull { it.toObject(Pet::class.java) }
                    if (pets != null) {
                        user.pets = pets
                    }
                    user
                }
            } ?: emptyList()

            Tasks.whenAllSuccess<ReceiveUser>(tasks).addOnSuccessListener { users ->
                trySend(users).isSuccess
            }
        }

        // 콜백을 제거하려면 awaitClose를 사용합니다.
        awaitClose { registration.remove() }
    }


}


