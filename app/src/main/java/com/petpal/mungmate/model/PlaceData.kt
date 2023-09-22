package com.petpal.mungmate.model

data class PlaceData(
    val id: String,
    val name: String,
    val category: String,
    val longitude: String,
    val latitude: String,
    val phone: String,
    val address: String,
    val favoriteCount: Int = 0


)
