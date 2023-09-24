package com.petpal.mungmate.model

import com.google.firebase.Timestamp

data class UserReport(
    val reportedUserId: String,
    val reportCategory: String,
    val reportContent: String?,
    val timestamp: Timestamp
) {
    constructor(): this("", "", "", Timestamp.now())
}