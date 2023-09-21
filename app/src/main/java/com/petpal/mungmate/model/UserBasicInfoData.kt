package com.petpal.mungmate.model

import android.net.Uri

data class UserBasicInfoData(
    val userImage: Uri? = null,
    val nickname: String,
    val birthday: String,
    val ageVisible: Boolean,
    //enum SEX
    val gender: Int,
    //enum AVAILABILITY
    val availability: Int,
    val walkHoursStart: String? = null,
    val walkHoursEnd: String? = null,
    val onWalk: Boolean? = null,
    val location: Map<String, Double>? = null

    ) {
    fun toHashMap(): HashMap<*, *> {
        return hashMapOf(
            "userImage" to userImage,
            "nickname" to nickname,
            "birthday" to birthday,
            "ageVisible" to ageVisible,
            "gender" to gender,
            "availability" to availability,
            "walkHoursStart" to walkHoursStart,
            "walkHoursEnd" to walkHoursEnd
        )
    }
}

