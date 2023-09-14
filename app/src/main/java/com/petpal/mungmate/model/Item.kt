package com.petpal.mungmate.model

data class Item(
    var brand: String,
    var name: String,
    var mainImageId: Int,
    var price: Long,
    var option: String,
    var amount: Long
)