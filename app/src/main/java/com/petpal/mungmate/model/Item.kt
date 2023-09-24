package com.petpal.mungmate.model

data class Item(
    val brand: String,
    val name: String,
    val mainImageId: Int,
    val price: Long,
    val option: String,
    val amount: Long
)