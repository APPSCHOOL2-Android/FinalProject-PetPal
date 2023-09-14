package com.petpal.mungmate.ui.otheruser

data class PostUiState(
    val title: String,
    val category: String,
    val dateCreated: String,
    val commentCnt: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PostUiState

        if (title != other.title) return false
        if (category != other.category) return false
        if (dateCreated != other.dateCreated) return false
        if (commentCnt != other.commentCnt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + dateCreated.hashCode()
        result = 31 * result + commentCnt.hashCode()
        return result
    }
}
