package com.petpal.mungmate.model

data class UserBasicInfo(

    val userUid: String,
    val userImage: String,
    val nickname: String,
    val birthday: String,
    val ageVisible: Boolean,
    val gender: String,
    val walkTimeZoneStart: String,
    val walkTimeZoneEnd: String,

)

