package com.petpal.mungmate.model

import com.google.firebase.Timestamp

data class WalkReview(
    val rating: Double,
    val comment: String?,
    val timestamp: Timestamp
) {
    constructor() : this(0.0, "", Timestamp.now())
}