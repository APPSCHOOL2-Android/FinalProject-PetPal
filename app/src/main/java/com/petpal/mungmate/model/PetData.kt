package com.petpal.mungmate.model

import android.graphics.Bitmap

data class PetData(
    val petImageUrl: String,
    val name: String,
    val breed: String,
    val birth: String,
    //enum PetSex
    val petSex: Int,
    val isNeutered: Boolean,
    val weight: Double,
    val character: String,
)