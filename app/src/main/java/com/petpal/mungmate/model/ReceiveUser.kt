package com.petpal.mungmate.model

data class ReceiveUser(
    var uid:String?=null,
    val userImage: String? = null,
    val nickname: String?=null,
    val birthday: String?=null,
    val ageVisible: Boolean?=false,
    //enum SEX
    val gender: Int?=null,
    //enum AVAILABILITY
    val availability: Int?=null,
    val walkHoursStart: String? = null,
    val walkHoursEnd: String? = null,
    val onWalk: Boolean? = null,
    val location: Map<String, Double>? = null,
    var pets: List<Pet> = listOf()
)
