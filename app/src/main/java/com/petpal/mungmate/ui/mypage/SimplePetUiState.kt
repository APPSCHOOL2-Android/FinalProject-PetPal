package com.petpal.mungmate.ui.mypage

data class SimplePetUiState(
    val name: String,
    val imageUrl: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimplePetUiState

        if (name != other.name) return false
        if (imageUrl != other.imageUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + imageUrl.hashCode()
        return result
    }
}