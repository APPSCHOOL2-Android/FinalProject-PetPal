package com.petpal.mungmate.model

import android.graphics.Bitmap
import android.net.Uri

//프래그먼트 사이 사용 클래스
data class UserBasicInfoData(
    val userImage: Bitmap,
    val nickname: String,
    val birthday: String,
    val ageVisible: Boolean,
    //enum SEX
    val gender: Int,
    //enum AVAILABILITY
    val availability: Int,
    val walkHoursStart: String? = null,
    val walkHoursEnd: String? = null,
)

