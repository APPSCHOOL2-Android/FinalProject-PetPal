package com.petpal.mungmate.model

import android.net.Uri

data class PetData(
    val imageURI: Uri? = null,
    val name: String,
    val breed: String,
    val birth: String,
    //enum PetSex
    val petSex: Int,
    val isNeutered: Boolean,
    val weight: Double,
    val character: String,
) {
    fun toHashMap(): HashMap<*, *> {
        return hashMapOf(
            "imageURI" to imageURI,
            "name" to name,
            "breed" to breed,
            "birth" to birth,
            "petSex" to petSex,
            "isNeutered" to isNeutered,
            "weight" to weight,
            "character" to character
        )
    }
}