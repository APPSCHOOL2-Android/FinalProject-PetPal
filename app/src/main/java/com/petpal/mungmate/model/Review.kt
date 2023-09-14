package com.petpal.mungmate.model

data class Review(
    var rating: Float = 0f,
    var writeid: String? = null,
    var review: String? = null,
    var timestamp: String? = null
) {
}