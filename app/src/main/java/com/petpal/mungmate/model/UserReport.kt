package com.petpal.mungmate.model

import com.google.firebase.Timestamp

data class UserReport(
    var reportedUserId: String?,
    var reportCategory: String?,
    var reportContent: String?,
    var timestamp: Timestamp?
) {
    constructor(): this("", "", "", null)
}