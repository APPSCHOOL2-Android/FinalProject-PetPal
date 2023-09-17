package com.petpal.mungmate.ui.walkhistory

data class WalkHistoryUiState(
    val dayOfWeek: String,
    val day: Long,
    val startTime: Long,
    val endTime: Long,
    val distance: Long,
    val memo: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WalkHistoryUiState

        if (dayOfWeek != other.dayOfWeek) return false
        if (day != other.day) return false
        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false
        if (distance != other.distance) return false
        if (memo != other.memo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dayOfWeek.hashCode()
        result = 31 * result + day.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + distance.hashCode()
        result = 31 * result + memo.hashCode()
        return result
    }
}
