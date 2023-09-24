package com.petpal.mungmate.model

import com.google.firebase.Timestamp

data class WalkReviewForMate(

    var rating: Float,
    var comment: String?,
    var timestamp: Timestamp?,
    var walkDistance: Double?=0.0,
    var walkDuration:Long?=null,
    var walkRecordEndTime:String?=null,
    var walkRecordStartTime:String?=null

    )

