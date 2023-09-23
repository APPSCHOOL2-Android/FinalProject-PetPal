package com.petpal.mungmate.model

import android.graphics.Bitmap
import com.petpal.mungmate.ui.pet.PetSex

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
){
    constructor():this("", "", "", "", PetSex.MALE.ordinal, false, 0.0, "")
}