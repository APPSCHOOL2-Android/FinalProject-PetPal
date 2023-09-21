package com.petpal.mungmate.model

//firestore저장용 데이터클래스
data class FirestoreUserBasicInfoData(
    val userImage: String, // 이미지 URL을 저장
    val nickname: String,
    val birthday: String,
    val ageVisible: Boolean,
    // enum SEX
    val gender: Int,
    // enum AVAILABILITY
    val availability: Int,
    val walkHoursStart: String? = null,
    val walkHoursEnd: String? = null
)