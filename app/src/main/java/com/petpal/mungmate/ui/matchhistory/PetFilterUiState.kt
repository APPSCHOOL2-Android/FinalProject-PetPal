package com.petpal.mungmate.ui.matchhistory

data class PetFilterUiState (
    val image: String,
    val name: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PetFilterUiState

        if (image != other.image) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = image.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}