package com.petpal.mungmate.ui.matchhistory

data class MatchHistoryUiState(
    val userImage: String,
    val userNickName: String,
    val petName: String,
    val timestamp: String,
    val place: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MatchHistoryUiState

        if (userImage != other.userImage) return false
        if (userNickName != other.userNickName) return false
        if (petName != other.petName) return false
        if (timestamp != other.timestamp) return false
        if (place != other.place) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userImage.hashCode()
        result = 31 * result + userNickName.hashCode()
        result = 31 * result + petName.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + place.hashCode()
        return result
    }
}
