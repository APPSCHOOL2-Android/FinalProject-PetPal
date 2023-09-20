package com.petpal.mungmate.ui.placereview

import com.google.firebase.firestore.FirebaseFirestore
import com.petpal.mungmate.model.Review
import kotlinx.coroutines.tasks.await

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow



class PlaceReviewRepository {

    private val db = FirebaseFirestore.getInstance()

    fun fetchAllReviewsForPlace(placeId: String): Flow<List<Review>> {
        val reviewsFlow = MutableStateFlow<List<Review>>(emptyList())

        val reviewRef = db.collection("places").document(placeId).collection("reviews")
        reviewRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // 에러 처리 (예: 로그 출력)
                return@addSnapshotListener
            }

            val reviews = snapshot?.documents?.mapNotNull { it.toObject(Review::class.java) } ?: emptyList()
            reviewsFlow.value = reviews
        }

        return reviewsFlow.asStateFlow()
    }

}