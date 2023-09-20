package com.petpal.mungmate.model

data class Review(
    val userid: String?=null,
    val date: String?=null,
    val rating: Float?=null,
    val comment: String?=null,
    val imageRes: String?=null
)