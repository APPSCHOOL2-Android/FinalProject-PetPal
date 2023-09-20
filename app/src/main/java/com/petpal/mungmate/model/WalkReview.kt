package com.petpal.mungmate.model

import com.google.firebase.Timestamp

data class WalkReview(
    var rating: Double?,
    var comment: String?,
    var timestamp: Timestamp?
) {
    constructor() : this(0.0, "", null)
}